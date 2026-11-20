package app.cookyourbooks.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.cookyourbooks.conversion.ConversionRegistry;
import app.cookyourbooks.exception.UnsupportedConversionException;
import app.cookyourbooks.model.ExactQuantity;
import app.cookyourbooks.model.Ingredient;
import app.cookyourbooks.model.MeasuredIngredient;
import app.cookyourbooks.model.Recipe;
import app.cookyourbooks.model.RecipeCollection;
import app.cookyourbooks.model.ShoppingItem;
import app.cookyourbooks.model.ShoppingItemImpl;
import app.cookyourbooks.model.ShoppingList;
import app.cookyourbooks.model.ShoppingListImpl;
import app.cookyourbooks.model.Unit;
import app.cookyourbooks.model.VagueIngredient;
import app.cookyourbooks.repository.RecipeCollectionRepository;
import app.cookyourbooks.repository.RecipeRepository;

/**
 * Implementation of {@link RecipeService}.
 *
 * <p>Coordinates parsing, transformations, persistence, aggregation, and search.
 */
public class RecipeServiceImpl implements RecipeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RecipeServiceImpl.class);

  private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

  private final RecipeRepository recipeRepository;
  private final RecipeCollectionRepository collectionRepository;
  private final ConversionRegistry conversionRegistry;

  private final RecipeTextParser recipeTextParser = new RecipeTextParser();

  /**
   * Creates a {@link RecipeServiceImpl} with the given dependencies.
   *
   * @param recipeRepository repository for individual recipes
   * @param collectionRepository repository for recipe collections
   * @param conversionRegistry registry for unit conversions
   */
  public RecipeServiceImpl(
      RecipeRepository recipeRepository,
      RecipeCollectionRepository collectionRepository,
      ConversionRegistry conversionRegistry) {
    this.recipeRepository = recipeRepository;
    this.collectionRepository = collectionRepository;
    this.conversionRegistry = conversionRegistry;
  }

  @Override
  public @NonNull Recipe importFromJson(@NonNull Path jsonFile, @NonNull String collectionId) {
    RecipeCollection collection =
        collectionRepository
            .findById(collectionId)
            .orElseThrow(() -> new CollectionNotFoundException(collectionId));

    try {
      Recipe recipe = MAPPER.readValue(jsonFile.toFile(), Recipe.class);
      recipeRepository.save(recipe);
      RecipeCollection updated = collection.addRecipe(recipe);
      collectionRepository.save(updated);

      LOGGER.info(
          "Imported recipe '{}' from JSON to collection '{}'", recipe.getTitle(), collectionId);
      return recipe;
    } catch (IOException e) {
      LOGGER.error("Failed to read file: {}", jsonFile, e);
      throw new ImportException("Could not import from " + jsonFile, e);
    }
  }

  @Override
  public @NonNull Recipe importFromText(@NonNull String recipeText, @NonNull String collectionId)
      throws ParseException {
    RecipeCollection collection =
        collectionRepository
            .findById(collectionId)
            .orElseThrow(() -> new CollectionNotFoundException(collectionId));

    LOGGER.debug("Parsing recipe text ({} characters)", recipeText.length());
    try {
      Recipe recipe = recipeTextParser.parse(recipeText);

      LOGGER.debug("Parsed {} ingredients", recipe.getIngredients().size());
      LOGGER.debug("Parsed {} instructions", recipe.getInstructions().size());

      recipeRepository.save(recipe);
      RecipeCollection updated = collection.addRecipe(recipe);
      collectionRepository.save(updated);

      LOGGER.info(
          "Imported recipe '{}' from text to collection '{}'", recipe.getTitle(), collectionId);
      return recipe;
    } catch (ParseException e) {
      LOGGER.error("Failed to parse recipe text", e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Failed to parse recipe text", e);
      throw new ParseException("Failed to parse recipe text", e);
    }
  }

  @Override
  public @NonNull Recipe scaleRecipe(@NonNull String recipeId, int targetServings) {
    if (targetServings <= 0) {
      throw new IllegalArgumentException("targetServings must be positive");
    }

    Recipe original =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(() -> new RecipeNotFoundException(recipeId));

    if (original.getServings() == null) {
      throw new IllegalArgumentException("recipe has no servings information");
    }

    int originalServings = original.getServings().getAmount();
    double factor = (double) targetServings / (double) originalServings;
    LOGGER.debug("Scaling factor: {}", factor);

    Recipe scaled = original.scale(factor);
    recipeRepository.save(scaled);

    LOGGER.info(
        "Scaled recipe '{}' from {} to {} servings",
        original.getTitle(),
        originalServings,
        targetServings);

    return scaled;
  }

  @Override
  public @NonNull Recipe convertRecipe(@NonNull String recipeId, @NonNull Unit targetUnit)
      throws UnsupportedConversionException {
    Recipe original =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(() -> new RecipeNotFoundException(recipeId));

    Recipe converted = original.convert(targetUnit, conversionRegistry);
    recipeRepository.save(converted);

    LOGGER.info("Converted recipe '{}' to {}", original.getTitle(), targetUnit);
    return converted;
  }

  @Override
  public @NonNull ShoppingList generateShoppingList(@NonNull List<String> recipeIds) {
    if (recipeIds.isEmpty()) {
      ShoppingList empty = new ShoppingListImpl(List.of(), List.of());
      LOGGER.info("Generated shopping list from {} recipes", 0);
      return empty;
    }

    record Key(String nameLower, Unit unit) {}

    Map<Key, Double> totals = new LinkedHashMap<>();
    Map<Key, String> displayNames = new LinkedHashMap<>();

    Set<String> uncountableSeen = new LinkedHashSet<>();
    List<String> uncountable = new ArrayList<>();

    for (String id : recipeIds) {
      Recipe recipe =
          recipeRepository.findById(id).orElseThrow(() -> new RecipeNotFoundException(id));

      LOGGER.debug("Aggregating ingredients from recipe '{}'", recipe.getTitle());

      for (Ingredient ing : recipe.getIngredients()) {
        if (ing instanceof MeasuredIngredient m) {
          String nameLower = m.getName().toLowerCase(Locale.ROOT);
          Unit unit = m.getQuantity().getUnit();
          Key key = new Key(nameLower, unit);

          if (!totals.containsKey(key)) {
            totals.put(key, m.getQuantity().toDecimal());
            displayNames.put(key, m.getName());
          } else {
            totals.put(key, totals.get(key) + m.getQuantity().toDecimal());
          }
        } else if (ing instanceof VagueIngredient v) {
          String nameLower = v.getName().toLowerCase(Locale.ROOT);
          if (uncountableSeen.add(nameLower)) {
            uncountable.add(v.getName());
          }
        } else {
          // Defensive: treat unknown Ingredient subtype as uncountable by name.
          String nameLower = ing.getName().toLowerCase(Locale.ROOT);
          if (uncountableSeen.add(nameLower)) {
            uncountable.add(ing.getName());
          }
        }
      }
    }

    List<ShoppingItem> items = new ArrayList<>();
    for (Map.Entry<Key, Double> e : totals.entrySet()) {
      Key key = e.getKey();
      double total = e.getValue();
      String displayName =
          java.util.Objects.requireNonNull(
              displayNames.get(key), "Missing display name for " + key);
      items.add(new ShoppingItemImpl(displayName, new ExactQuantity(total, key.unit())));
    }

    ShoppingList list = new ShoppingListImpl(items, uncountable);

    LOGGER.info("Generated shopping list from {} recipes", recipeIds.size());
    return list;
  }

  @Override
  public @NonNull List<Recipe> findByIngredient(@NonNull String ingredientName) {
    String needle = ingredientName.toLowerCase(Locale.ROOT);

    List<Recipe> results =
        recipeRepository.findAll().stream()
            .filter(
                r ->
                    r.getIngredients().stream()
                        .map(Ingredient::getName)
                        .anyMatch(n -> n.toLowerCase(Locale.ROOT).contains(needle)))
            .toList();

    LOGGER.info("Found {} recipes containing '{}'", results.size(), ingredientName);
    return results;
  }
}
