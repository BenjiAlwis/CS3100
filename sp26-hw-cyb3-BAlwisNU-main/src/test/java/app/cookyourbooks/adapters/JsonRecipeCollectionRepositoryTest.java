package app.cookyourbooks.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import app.cookyourbooks.model.Cookbook;
import app.cookyourbooks.model.CookbookImpl;
import app.cookyourbooks.model.ExactQuantity;
import app.cookyourbooks.model.FractionalQuantity;
import app.cookyourbooks.model.Ingredient;
import app.cookyourbooks.model.MeasuredIngredient;
import app.cookyourbooks.model.PersonalCollection;
import app.cookyourbooks.model.PersonalCollectionImpl;
import app.cookyourbooks.model.RangeQuantity;
import app.cookyourbooks.model.Recipe;
import app.cookyourbooks.model.RecipeCollection;
import app.cookyourbooks.model.Unit;
import app.cookyourbooks.model.VagueIngredient;
import app.cookyourbooks.model.WebCollection;
import app.cookyourbooks.model.WebCollectionImpl;
import app.cookyourbooks.repository.RecipeCollectionRepository;

/**
 * Tests for the {@link JsonRecipeCollectionRepository} implementation.
 *
 * <p><strong>Type Preservation:</strong> Saving a {@link Cookbook} and loading it back must return
 * a {@code Cookbook}, not just a {@code RecipeCollection}.
 *
 * <p><strong>Order Independence:</strong> Methods like {@link RecipeCollectionRepository#findAll()}
 * have unspecified ordering. Do not write tests that depend on a specific order.
 */
@DisplayName("JsonRecipeCollectionRepository")
class JsonRecipeCollectionRepositoryTest {

  @TempDir private @Nullable Path tempDir;

  private @Nullable RecipeCollectionRepository repository;

  @BeforeEach
  void setUp() {
    repository = new JsonRecipeCollectionRepository(Objects.requireNonNull(tempDir));
  }

  @Nested
  @DisplayName("Cookbook round-trip")
  class CookbookRoundTrip {

    @Test
    @DisplayName("preserves cookbook type and metadata after round-trip")
    void preservesCookbookTypeAndMetadata() {
      Cookbook cookbook =
          CookbookImpl.builder()
              .id("cookbook-1")
              .title("The Joy of Cooking")
              .author("Irma Rombauer")
              .isbn("978-1501169717")
              .publicationYear(1931)
              .build();

      Objects.requireNonNull(repository).save(cookbook);
      Optional<RecipeCollection> loaded = Objects.requireNonNull(repository).findById("cookbook-1");

      assertTrue(loaded.isPresent());
      assertTrue(loaded.get() instanceof Cookbook, "Should preserve Cookbook type");
      Cookbook loadedCookbook = (Cookbook) loaded.get();
      assertEquals("Irma Rombauer", loadedCookbook.getAuthor().orElse(null));
      assertEquals("978-1501169717", loadedCookbook.getIsbn().orElse(null));
      assertEquals(1931, loadedCookbook.getPublicationYear().orElse(-1));
    }
  }

  @Nested
  @DisplayName("findAll preserves types")
  class FindAllPreservesTypes {

    @Test
    @DisplayName("returns all collections with correct concrete types")
    void returnsAllCollectionsWithCorrectTypes() {
      Cookbook cookbook = CookbookImpl.builder().id("c1").title("Cookbook").build();
      PersonalCollection personal =
          PersonalCollectionImpl.builder().id("p1").title("Personal").build();
      WebCollection web =
          WebCollectionImpl.builder()
              .id("w1")
              .title("Web")
              .sourceUrl(Objects.requireNonNull(URI.create("https://example.com")))
              .build();

      Objects.requireNonNull(repository).save(cookbook);
      Objects.requireNonNull(repository).save(personal);
      Objects.requireNonNull(repository).save(web);

      List<RecipeCollection> all = Objects.requireNonNull(repository).findAll();

      assertEquals(3, all.size());
      assertEquals(1, all.stream().filter(c -> c instanceof Cookbook).count());
      assertEquals(1, all.stream().filter(c -> c instanceof PersonalCollection).count());
      assertEquals(1, all.stream().filter(c -> c instanceof WebCollection).count());
    }
  }

  @Nested
  @DisplayName("findByTitle / findAllByTitle")
  class FindByTitleTests {

    @Test
    @DisplayName("findByTitle matches case-insensitively and returns any match")
    void findByTitleCaseInsensitive() {
      Cookbook c1 = CookbookImpl.builder().id("a").title("Same").build();
      PersonalCollection c2 = PersonalCollectionImpl.builder().id("b").title("same").build();
      Objects.requireNonNull(repository).save(c1);
      Objects.requireNonNull(repository).save(c2);

      Optional<RecipeCollection> found = Objects.requireNonNull(repository).findByTitle("SAME");
      assertTrue(found.isPresent());
      assertTrue(found.get().getId().equals("a") || found.get().getId().equals("b"));
    }

    @Test
    @DisplayName("findAllByTitle returns all matches (case-insensitive)")
    void findAllByTitle() {
      Cookbook c1 = CookbookImpl.builder().id("a").title("Same").build();
      PersonalCollection c2 = PersonalCollectionImpl.builder().id("b").title("same").build();
      Cookbook c3 = CookbookImpl.builder().id("c").title("Other").build();
      Objects.requireNonNull(repository).save(c1);
      Objects.requireNonNull(repository).save(c2);
      Objects.requireNonNull(repository).save(c3);

      List<RecipeCollection> results = Objects.requireNonNull(repository).findAllByTitle("SAME");
      assertEquals(2, results.size());
    }
  }

  @Nested
  @DisplayName("nested recipes round-trip")
  class NestedRecipesRoundTrip {

    @Test
    @DisplayName("persists recipes inside a collection")
    void persistsNestedRecipes() {
      Recipe r1 = new Recipe("r1", "Chocolate Cake", null, emptyList(), emptyList(), emptyList());
      Recipe r2 = new Recipe("r2", "Vanilla Cookies", null, emptyList(), emptyList(), emptyList());

      Cookbook cookbook =
          CookbookImpl.builder().id("cb").title("Desserts").recipes(listOf(r1, r2)).build();

      Objects.requireNonNull(repository).save(cookbook);

      Optional<RecipeCollection> loaded = Objects.requireNonNull(repository).findById("cb");
      assertTrue(loaded.isPresent());
      assertEquals(cookbook, loaded.get());
    }

    @Test
    @DisplayName("preserves recipe servings Quantity concrete types after round-trip")
    void preservesServingsQuantityTypes() {
      Recipe exactServings =
          new Recipe(
              "r-exact",
              "Exact",
              new ExactQuantity(4, Unit.WHOLE),
              emptyList(),
              emptyList(),
              emptyList());
      Recipe fractionalServings =
          new Recipe(
              "r-frac",
              "Fractional",
              new FractionalQuantity(1, 1, 2, Unit.WHOLE),
              emptyList(),
              emptyList(),
              emptyList());
      Recipe rangeServings =
          new Recipe(
              "r-range",
              "Range",
              new RangeQuantity(2, 3, Unit.WHOLE),
              emptyList(),
              emptyList(),
              emptyList());

      Cookbook cookbook =
          CookbookImpl.builder()
              .id("cb-servings")
              .title("Servings Types")
              .recipes(listOf(exactServings, fractionalServings, rangeServings))
              .build();

      Objects.requireNonNull(repository).save(cookbook);

      RecipeCollection loadedCollection =
          Objects.requireNonNull(repository).findById("cb-servings").orElseThrow();
      assertTrue(loadedCollection instanceof Cookbook);
      List<Recipe> loadedRecipes = loadedCollection.getRecipes();
      assertEquals(3, loadedRecipes.size());

      Recipe loadedExact =
          loadedRecipes.stream().filter(r -> r.getId().equals("r-exact")).findFirst().orElseThrow();
      Recipe loadedFrac =
          loadedRecipes.stream().filter(r -> r.getId().equals("r-frac")).findFirst().orElseThrow();
      Recipe loadedRange =
          loadedRecipes.stream().filter(r -> r.getId().equals("r-range")).findFirst().orElseThrow();

      assertTrue(loadedExact.getServings() != null);
      assertTrue(loadedExact.getServings() instanceof ExactQuantity);

      assertTrue(loadedFrac.getServings() != null);
      assertTrue(loadedFrac.getServings() instanceof FractionalQuantity);

      assertTrue(loadedRange.getServings() != null);
      assertTrue(loadedRange.getServings() instanceof RangeQuantity);
    }

    @Test
    @DisplayName("preserves Ingredient concrete types inside nested recipes after round-trip")
    void preservesNestedIngredientTypes() {
      Ingredient measured =
          new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);
      Ingredient vague = new VagueIngredient("salt", "to taste", null, null);

      Recipe recipe =
          new Recipe(
              "r-ing", "Ingredient Types", null, listOf(measured, vague), emptyList(), emptyList());

      Cookbook cookbook =
          CookbookImpl.builder()
              .id("cb-ingredients")
              .title("Ingredient Types")
              .recipes(listOf(recipe))
              .build();

      Objects.requireNonNull(repository).save(cookbook);

      RecipeCollection loadedCollection =
          Objects.requireNonNull(repository).findById("cb-ingredients").orElseThrow();
      Recipe loadedRecipe = loadedCollection.getRecipes().get(0);

      assertEquals("r-ing", loadedRecipe.getId());
      assertEquals(2, loadedRecipe.getIngredients().size());

      Ingredient loaded0 = loadedRecipe.getIngredients().get(0);
      Ingredient loaded1 = loadedRecipe.getIngredients().get(1);

      assertTrue(
          loaded0 instanceof MeasuredIngredient,
          "First ingredient should remain MeasuredIngredient");
      assertTrue(
          loaded1 instanceof VagueIngredient, "Second ingredient should remain VagueIngredient");
    }
  }

  private static <T> List<T> emptyList() {
    return Objects.requireNonNull(List.of());
  }

  private static <T> List<T> listOf(T item) {
    return Objects.requireNonNull(List.of(item));
  }

  private static <T> List<T> listOf(T first, T second) {
    return Objects.requireNonNull(List.of(first, second));
  }

  private static <T> List<T> listOf(T first, T second, T third) {
    return Objects.requireNonNull(List.of(first, second, third));
  }
}
