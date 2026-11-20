package app.cookyourbooks.cli;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import app.cookyourbooks.cli.completion.CookModeHolder;
import app.cookyourbooks.model.Ingredient;
import app.cookyourbooks.model.IngredientRef;
import app.cookyourbooks.model.Instruction;
import app.cookyourbooks.model.MeasuredIngredient;
import app.cookyourbooks.model.PersonalCollectionImpl;
import app.cookyourbooks.model.Recipe;
import app.cookyourbooks.model.RecipeCollection;
import app.cookyourbooks.model.ShoppingItem;
import app.cookyourbooks.model.ShoppingList;
import app.cookyourbooks.model.SourceType;
import app.cookyourbooks.model.Unit;
import app.cookyourbooks.model.VagueIngredient;
import app.cookyourbooks.repository.RepositoryException;
import app.cookyourbooks.services.ShoppingListAggregator;

/**
 * Interactive command-line interface for the CookYourBooks application.
 *
 * <p>This class owns the read-eval-print loop, parses user input, dispatches supported commands,
 * and writes user-facing output through {@link CliContext}.
 */
public final class CybCli implements Runnable {

  private static final ObjectMapper OBJECT_MAPPER = createMapper();

  private final CliContext ctx;
  private boolean running = true;

  /**
   * Creates a CLI runner with the provided runtime context.
   *
   * @param ctx the CLI context containing repositories, services, and I/O adapters
   */
  public CybCli(CliContext ctx) {
    this.ctx = ctx;
  }

  /**
   * Creates and configures the shared JSON mapper used for recipe import.
   *
   * @return the configured object mapper
   */
  private static ObjectMapper createMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(
        com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper;
  }

  /** Starts the interactive read-eval-print loop until the user exits. */
  @Override
  public void run() {
    while (running) {
      String line;
      try {
        line = ctx.readLine("> ");
      } catch (Exception e) {
        break;
      }

      if (line == null) {
        continue;
      }

      handle(line.trim());
    }
  }

  /**
   * Parses and dispatches a single CLI command line.
   *
   * @param line the trimmed input line to execute
   */
  private void handle(String line) {
    if (line.isBlank()) {
      return;
    }

    List<String> args = QuotedTokenizer.tokenize(line);
    if (args.isEmpty()) {
      return;
    }

    String cmd = args.get(0).toLowerCase(Locale.ROOT);

    try {
      switch (cmd) {
        case "help" -> handleHelp(args);
        case "quit", "exit" -> handleQuit();
        case "collections" -> handleCollections();
        case "collection" -> handleCollection(args);
        case "recipes" -> handleRecipes(args);
        case "conversions" -> handleConversions();
        case "conversion" -> handleConversion(args);
        case "show" -> handleShow(args);
        case "search" -> handleSearch(args);
        case "import" -> handleImport(args);
        case "delete" -> handleDelete(args);
        case "scale" -> handleScale(args);
        case "convert" -> handleConvert(args);
        case "shopping-list" -> handleShoppingList(args);
        case "export" -> handleExport(args);
        case "cook" -> handleCook(args);
        default -> ctx.println("Unknown command: " + args.get(0));
      }
    } catch (RepositoryException e) {
      ctx.println("Storage error: " + e.getMessage());
    } catch (Exception e) {
      ctx.println(e.getMessage() == null ? "Operation failed." : e.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  // General
  // ---------------------------------------------------------------------------

  /**
   * Handles the help command and optional topic-specific help.
   *
   * @param args tokenized command arguments
   */
  private void handleHelp(List<String> args) {
    if (args.size() == 1) {
      ctx.println("CookYourBooks Commands");
      ctx.println("");
      ctx.println("Library");
      ctx.println("  collections");
      ctx.println("  collection create <name>");
      ctx.println("  recipes <collection>");
      ctx.println("  conversions");
      ctx.println("  conversion add");
      ctx.println("  conversion remove <identifier>");
      ctx.println("");
      ctx.println("Recipe");
      ctx.println("  show <recipe>");
      ctx.println("  search <ingredient>");
      ctx.println("  import json <file> <collection>");
      ctx.println("  delete <recipe>");
      ctx.println("");
      ctx.println("Tools");
      ctx.println("  scale <recipe> <servings>");
      ctx.println("  convert <recipe> <unit>");
      ctx.println("  shopping-list <recipe1> [recipe2] ...");
      ctx.println("  export <recipe> <file>");
      ctx.println("  cook <recipe>");
      ctx.println("");
      ctx.println("General");
      ctx.println("  help [command]");
      ctx.println("  quit");
      return;
    }

    String topic = String.join(" ", args.subList(1, args.size())).toLowerCase(Locale.ROOT);
    switch (topic) {
      case "show" -> {
        ctx.println("show <recipe>");
        ctx.println("Display full recipe details.");
      }
      case "scale" -> {
        ctx.println("scale <recipe> <servings>");
        ctx.println("Scale a recipe to the target servings and preview/save the result.");
      }
      case "convert" -> {
        ctx.println("convert <recipe> <unit>");
        ctx.println("Convert measured ingredients to a target unit and preview/save the result.");
      }
      default -> {
        ctx.println("Unknown command: " + topic);
      }
    }
  }

  /** Starts CLI shutdown and prints the farewell message. */
  private void handleQuit() {
    ctx.println("Goodbye!");
    running = false;
  }

  // ---------------------------------------------------------------------------
  // Library commands
  // ---------------------------------------------------------------------------

  /** Lists all recipe collections with source and recipe counts. */
  private void handleCollections() {
    List<RecipeCollection> collections = ctx.collRepo().findAll();
    if (collections.isEmpty()) {
      ctx.println("No collections yet. Use 'collection create <name>' to create one.");
      return;
    }

    ctx.println("Collections:");
    int i = 1;
    for (RecipeCollection c : collections) {
      ctx.println(
          i++
              + ". "
              + badge(c.getSourceType())
              + " "
              + c.getTitle()
              + " ("
              + c.getRecipes().size()
              + " recipes)");
    }
  }

  /**
   * Handles collection management subcommands.
   *
   * @param args tokenized command arguments
   */
  private void handleCollection(List<String> args) {
    if (args.size() < 2 || !"create".equalsIgnoreCase(args.get(1))) {
      ctx.println("Unknown command: collection");
      return;
    }

    if (args.size() < 3) {
      ctx.println("Usage: collection create <name>");
      return;
    }

    String title = args.get(2).trim();
    if (title.isBlank()) {
      ctx.println("Please provide a collection name.");
      return;
    }

    var collection = PersonalCollectionImpl.builder().title(title).build();
    ctx.collRepo().save(collection);
    ctx.println("Created personal collection '" + title + "'.");
  }

  /**
   * Lists recipes in a selected collection.
   *
   * @param args tokenized command arguments
   */
  private void handleRecipes(List<String> args) {
    if (args.size() < 2) {
      ctx.println("Usage: recipes <collection>");
      return;
    }

    String query = args.get(1);
    Optional<RecipeCollection> maybe = findCollection(query);
    if (maybe.isEmpty()) {
      ctx.println(ErrorMessages.collectionNotFound(query));
      return;
    }

    RecipeCollection c = maybe.get();
    ctx.println(c.getTitle() + " (" + c.getRecipes().size() + " recipes)");
    for (Recipe r : c.getRecipes()) {
      String servings =
          r.getServings() == null ? "No servings" : "Serves " + r.getServings().toString();
      ctx.println("- " + r.getTitle() + " — " + servings);
    }
  }

  /** Displays all configured house conversion rules. */
  private void handleConversions() {
    var rules = ctx.library().getHouseConversions();
    if (rules.isEmpty()) {
      ctx.println("No house conversions defined. Use 'conversion add' to add one.");
      return;
    }

    ctx.println("House Conversions");
    for (var r : rules) {
      String ingredient = r.ingredientName() == null ? "any" : r.ingredientName();
      String rhs = formatNumber(r.factor()) + " " + r.toUnit().getAbbreviation();
      ctx.println("1 " + r.fromUnit().getAbbreviation() + " " + ingredient + " = " + rhs);
    }
  }

  /**
   * Handles conversion subcommands.
   *
   * @param args tokenized command arguments
   */
  private void handleConversion(List<String> args) {
    if (args.size() < 2) {
      ctx.println("Unknown command: conversion");
      return;
    }

    String sub = args.get(1).toLowerCase(Locale.ROOT);
    switch (sub) {
      case "add" -> handleConversionAdd();
      case "remove" -> handleConversionRemove(args);
      default -> ctx.println("Unknown command: conversion " + sub);
    }
  }

  /** Prompts for and persists a new house conversion rule. */
  private void handleConversionAdd() {
    String fromAmountRaw = ctx.readLine("From amount: ").trim();
    double fromAmount;
    try {
      fromAmount = Double.parseDouble(fromAmountRaw);
    } catch (NumberFormatException e) {
      ctx.println("Invalid amount. Please enter a number.");
      return;
    }

    String fromUnitRaw = ctx.readLine("From unit: ").trim();
    Unit fromUnit;
    try {
      fromUnit = Unit.parse(fromUnitRaw);
    } catch (IllegalArgumentException e) {
      ctx.println("Unknown unit: " + fromUnitRaw);
      return;
    }

    String ingredientRaw = ctx.readLine("Ingredient (or any): ").trim();
    String ingredient = ingredientRaw.equalsIgnoreCase("any") ? "any" : ingredientRaw;

    String toAmountRaw = ctx.readLine("To amount: ").trim();
    double toAmount;
    try {
      toAmount = Double.parseDouble(toAmountRaw);
    } catch (NumberFormatException e) {
      ctx.println("Invalid amount. Please enter a number.");
      return;
    }

    String toUnitRaw = ctx.readLine("To unit: ").trim();
    Unit toUnit;
    try {
      toUnit = Unit.parse(toUnitRaw);
    } catch (IllegalArgumentException e) {
      ctx.println("Unknown unit: " + toUnitRaw);
      return;
    }

    String identifier =
        fromUnit.getAbbreviation().toLowerCase(Locale.ROOT)
            + " "
            + (ingredient.equalsIgnoreCase("any") ? "any" : ingredient.toLowerCase(Locale.ROOT));

    boolean exists =
        ctx.library().getHouseConversions().stream()
            .anyMatch(
                r -> {
                  String ruleId =
                      r.fromUnit().getAbbreviation().toLowerCase(Locale.ROOT)
                          + " "
                          + (r.ingredientName() == null
                              ? "any"
                              : r.ingredientName().toLowerCase(Locale.ROOT));
                  return ruleId.equals(identifier);
                });

    if (exists) {
      ctx.println("A conversion already exists for " + identifier + ".");
      return;
    }

    double factor = toAmount / fromAmount;
    var rule =
        new app.cookyourbooks.conversion.ConversionRule(
            fromUnit, toUnit, factor, ingredient.equalsIgnoreCase("any") ? null : ingredient);

    ctx.library().addHouseConversion(rule);

    ctx.println(
        "Added: "
            + formatNumber(fromAmount)
            + " "
            + fromUnit.getAbbreviation()
            + " "
            + ingredient
            + " = "
            + formatNumber(toAmount)
            + " "
            + toUnit.getAbbreviation());
  }

  /**
   * Removes a house conversion rule by identifier.
   *
   * @param args tokenized command arguments
   */
  private void handleConversionRemove(List<String> args) {
    if (args.size() < 3) {
      ctx.println("Usage: conversion remove <identifier>");
      return;
    }

    String identifier = args.get(2);
    if (ctx.library().removeHouseConversion(identifier)) {
      ctx.println("Removed conversion: " + identifier);
    } else {
      ctx.println("No conversion found: " + identifier);
    }
  }

  // ---------------------------------------------------------------------------
  // Recipe commands
  // ---------------------------------------------------------------------------

  /**
   * Prints full details for a recipe resolved from the query.
   *
   * @param args tokenized command arguments
   */
  private void handleShow(List<String> args) {
    if (args.size() < 2) {
      ctx.println("Usage: show <recipe>");
      return;
    }

    Resolution res = resolveRecipe(args.get(1));
    if (res.kind == ResolutionKind.NOT_FOUND) {
      ctx.println(ErrorMessages.recipeNotFound(args.get(1)));
      return;
    }
    if (res.kind == ResolutionKind.AMBIGUOUS) {
      printAmbiguous(res.matches);
      return;
    }

    Recipe recipe = requireRecipe(res);
    ctx.println(formatRecipeFull(recipe));
  }

  /**
   * Searches recipes by ingredient name substring.
   *
   * @param args tokenized command arguments
   */
  private void handleSearch(List<String> args) {
    if (args.size() < 2) {
      ctx.println("Usage: search <ingredient>");
      return;
    }

    String ingredient = args.get(1);
    List<RecipeMatch> matches = searchByIngredient(ingredient);
    if (matches.isEmpty()) {
      ctx.println("No recipes found containing '" + ingredient + "'");
      return;
    }

    ctx.println("Recipes containing '" + ingredient + "'");
    for (RecipeMatch m : matches) {
      ctx.println("- " + m.recipe.getTitle() + " (" + m.collectionTitle + ")");
    }
    ctx.println("Found " + matches.size() + " recipes");
  }

  /**
   * Imports a recipe from JSON into a target collection.
   *
   * @param args tokenized command arguments
   * @throws IOException if the recipe file cannot be read or parsed
   */
  private void handleImport(List<String> args) throws IOException {
    if (args.size() < 2 || !"json".equalsIgnoreCase(args.get(1))) {
      ctx.println("Unknown command: import");
      return;
    }
    if (args.size() < 4) {
      ctx.println("Usage: import json <file> <collection>");
      return;
    }

    Path file = Path.of(args.get(2));
    String collectionTitle = args.get(3);

    Optional<RecipeCollection> maybe = findCollection(collectionTitle);
    if (maybe.isEmpty()) {
      ctx.println(ErrorMessages.collectionNotFound(collectionTitle));
      return;
    }

    Recipe imported = OBJECT_MAPPER.readValue(file.toFile(), Recipe.class);
    RecipeCollection updated = maybe.get().addRecipe(imported);
    ctx.collRepo().save(updated);

    ctx.println("Imported '" + imported.getTitle() + "' into '" + maybe.get().getTitle() + "'");
  }

  /**
   * Deletes a resolved recipe after interactive confirmation.
   *
   * @param args tokenized command arguments
   */
  private void handleDelete(List<String> args) {
    if (args.size() < 2) {
      ctx.println("Usage: delete <recipe>");
      return;
    }

    Resolution res = resolveRecipe(args.get(1));
    if (res.kind == ResolutionKind.NOT_FOUND) {
      ctx.println(ErrorMessages.recipeNotFound(args.get(1)));
      return;
    }
    if (res.kind == ResolutionKind.AMBIGUOUS) {
      printAmbiguous(res.matches);
      return;
    }

    Recipe recipe = requireRecipe(res);

    String confirm = ctx.readLine("Delete '" + recipe.getTitle() + "'? [y/N]: ").trim();
    if (!confirm.equalsIgnoreCase("y")) {
      return;
    }

    ctx.recipeRepo().delete(recipe.getId());
    ctx.println("Deleted recipe '" + recipe.getTitle() + "'");
  }

  // ---------------------------------------------------------------------------
  // Tool commands
  // ---------------------------------------------------------------------------

  /**
   * Scales a recipe to a target serving count and optionally saves the result.
   *
   * @param args tokenized command arguments
   */
  private void handleScale(List<String> args) {
    if (args.size() < 3) {
      ctx.println("Usage: scale <recipe> <servings>");
      return;
    }

    Resolution res = resolveRecipe(args.get(1));
    if (res.kind == ResolutionKind.NOT_FOUND) {
      ctx.println(ErrorMessages.recipeNotFound(args.get(1)));
      return;
    }
    if (res.kind == ResolutionKind.AMBIGUOUS) {
      printAmbiguous(res.matches);
      return;
    }

    int targetServings;
    try {
      targetServings = Integer.parseInt(args.get(2));
      if (targetServings <= 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      ctx.println("Invalid servings.");
      return;
    }

    Recipe recipe = requireRecipe(res);
    if (recipe.getServings() == null) {
      ctx.println("Recipe has no serving information: " + recipe.getTitle());
      return;
    }

    double factor = targetServings / (double) recipe.getServings().getAmount();
    Recipe scaled = recipe.scale(factor);

    ctx.println(formatScaleComparison(recipe, scaled, factor));
    String save = ctx.readLine("Save scaled recipe? [y/N]: ").trim();
    if (!save.equalsIgnoreCase("y")) {
      ctx.println("Scaling discarded");
      return;
    }

    Recipe named =
        new Recipe(
            null,
            recipe.getTitle() + " (scaled to " + targetServings + ")",
            scaled.getServings(),
            scaled.getIngredients(),
            scaled.getInstructions(),
            scaled.getConversionRules());

    saveDerivedRecipe(recipe, named);
    ctx.println("Saved scaled recipe '" + named.getTitle() + "'");
  }

  /**
   * Converts a recipe's measured ingredients to a target unit and optionally saves the result.
   *
   * @param args tokenized command arguments
   */
  private void handleConvert(List<String> args) {
    if (args.size() < 3) {
      ctx.println("Usage: convert <recipe> <unit>");
      return;
    }

    Resolution res = resolveRecipe(args.get(1));
    if (res.kind == ResolutionKind.NOT_FOUND) {
      ctx.println(ErrorMessages.recipeNotFound(args.get(1)));
      return;
    }
    if (res.kind == ResolutionKind.AMBIGUOUS) {
      printAmbiguous(res.matches);
      return;
    }

    Unit targetUnit;
    try {
      targetUnit = Unit.parse(args.get(2));
    } catch (IllegalArgumentException e) {
      ctx.println("Unknown unit: " + args.get(2));
      return;
    }

    Recipe recipe = requireRecipe(res);

    Recipe converted;
    try {
      converted = recipe.convert(targetUnit, ctx.library().getConversionRegistry());
    } catch (Exception e) {
      ctx.println(e.getMessage() != null ? e.getMessage() : "Operation failed.");
      return;
    }

    ctx.println(formatConvertComparison(recipe, converted, targetUnit));
    String save = ctx.readLine("Save converted recipe? [y/N]: ").trim();
    if (!save.equalsIgnoreCase("y")) {
      ctx.println("Conversion discarded");
      return;
    }

    Recipe named =
        new Recipe(
            null,
            recipe.getTitle() + " (converted to " + targetUnit.getAbbreviation() + ")",
            converted.getServings(),
            converted.getIngredients(),
            converted.getInstructions(),
            converted.getConversionRules());

    saveDerivedRecipe(recipe, named);
    ctx.println("Saved converted recipe '" + named.getTitle() + "'");
  }

  /**
   * Aggregates selected recipes into a shopping list.
   *
   * @param args tokenized command arguments
   */
  private void handleShoppingList(List<String> args) {
    if (args.size() < 2) {
      ctx.println("Usage: shopping-list <recipe1> [recipe2] ...");
      return;
    }

    List<Recipe> recipes = new ArrayList<>();
    for (int i = 1; i < args.size(); i++) {
      String q = args.get(i).trim();
      if (q.isBlank()) {
        continue;
      }

      Resolution res = resolveRecipe(q);
      if (res.kind == ResolutionKind.NOT_FOUND) {
        ctx.println(ErrorMessages.recipeNotFound(q));
        return;
      }
      if (res.kind == ResolutionKind.AMBIGUOUS) {
        printAmbiguous(res.matches);
        return;
      }
      recipes.add(requireRecipe(res));
    }

    List<Ingredient> ingredients = new ArrayList<>();
    for (Recipe r : recipes) {
      ingredients.addAll(r.getIngredients());
    }

    ShoppingList list = new ShoppingListAggregator().aggregate(ingredients);
    ctx.println(formatShoppingList(list));
  }

  /**
   * Exports a recipe to a markdown file.
   *
   * @param args tokenized command arguments
   */
  private void handleExport(List<String> args) {
    if (args.size() < 3) {
      ctx.println("Usage: export <recipe> <file>");
      return;
    }

    Resolution res = resolveRecipe(args.get(1));
    if (res.kind == ResolutionKind.NOT_FOUND) {
      ctx.println(ErrorMessages.recipeNotFound(args.get(1)));
      return;
    }
    if (res.kind == ResolutionKind.AMBIGUOUS) {
      printAmbiguous(res.matches);
      return;
    }

    Recipe recipe = requireRecipe(res);
    Path out = Path.of(args.get(2));
    ctx.exporter().exportToFile(recipe, out);
    ctx.println("Exported '" + recipe.getTitle() + "' to " + out);
  }

  /**
   * Starts interactive cook mode for a resolved recipe.
   *
   * @param args tokenized command arguments
   */
  private void handleCook(List<String> args) {
    if (args.size() < 2) {
      ctx.println("Usage: cook <recipe>");
      return;
    }

    Resolution res = resolveRecipe(args.get(1));
    if (res.kind == ResolutionKind.NOT_FOUND) {
      ctx.println(ErrorMessages.recipeNotFound(args.get(1)));
      return;
    }
    if (res.kind == ResolutionKind.AMBIGUOUS) {
      printAmbiguous(res.matches);
      return;
    }

    Recipe recipe = requireRecipe(res);
    if (recipe.getInstructions().isEmpty()) {
      ctx.println("Error: Recipe has no instructions.");
      return;
    }

    runCookMode(recipe, ctx.cookModeHolder());
  }

  // ---------------------------------------------------------------------------
  // Cook mode
  // ---------------------------------------------------------------------------

  /**
   * Runs the cook-mode navigation loop.
   *
   * @param recipe the recipe currently being cooked
   * @param holder the cook-mode state holder used by completion
   */
  private void runCookMode(Recipe recipe, CookModeHolder holder) {
    holder.setInCookMode(true);
    try {
      int index = 0;
      printCookHeader(recipe);
      printCookIngredients(recipe);

      while (true) {
        printCookStep(recipe, index);
        String cmd = ctx.readLine("cook> ").trim().toLowerCase(Locale.ROOT);

        switch (cmd) {
          case "next", "n" -> {
            if (index < recipe.getInstructions().size() - 1) {
              index++;
            } else {
              ctx.println("Finished cooking " + recipe.getTitle() + "! Enjoy!");
              return;
            }
          }
          case "prev", "p" -> {
            if (index > 0) {
              index--;
            } else {
              ctx.println("Already at the beginning.");
            }
          }
          case "ingredients", "i" -> printCookIngredients(recipe);
          case "quit", "exit", "q" -> {
            return;
          }
          default -> {
            // ignore unknown cook-mode command
          }
        }
      }
    } finally {
      holder.setInCookMode(false);
    }
  }

  /**
   * Prints the cook-mode header for a recipe.
   *
   * @param recipe the active recipe
   */
  private void printCookHeader(Recipe recipe) {
    ctx.println("════════════════════════════════════════");
    ctx.println("COOKING: " + recipe.getTitle());
    if (recipe.getServings() != null) {
      ctx.println("Serves " + recipe.getServings());
    }
    ctx.println("════════════════════════════════════════");
  }

  /**
   * Prints ingredients in a two-column cook-mode layout.
   *
   * @param recipe the active recipe
   */
  private void printCookIngredients(Recipe recipe) {
    ctx.println("Ingredients:");
    List<String> lines = recipe.getIngredients().stream().map(this::formatIngredientShort).toList();

    for (int i = 0; i < lines.size(); i += 2) {
      if (i + 1 < lines.size()) {
        ctx.println(String.format("%-36s %s", lines.get(i), lines.get(i + 1)));
      } else {
        ctx.println(lines.get(i));
      }
    }
  }

  /**
   * Prints the current cook-mode step and command hints.
   *
   * @param recipe the active recipe
   * @param index zero-based instruction index
   */
  private void printCookStep(Recipe recipe, int index) {
    if (recipe.getInstructions().isEmpty()) {
      ctx.println("───────");
      ctx.println("No instructions.");
      ctx.println("[ingredients] [quit]");
      return;
    }

    Instruction step = recipe.getInstructions().get(index);
    ctx.println("───────");
    ctx.println("Step " + (index + 1) + " of " + recipe.getInstructions().size());
    ctx.println(step.getText());

    if (step.getIngredientRefs().isEmpty()) {
      ctx.println("(no ingredients used in this step)");
    } else {
      ctx.println("Uses: " + formatRefs(step.getIngredientRefs()));
    }
    ctx.println("[next] [prev] [ingredients] [quit]");
  }

  // ---------------------------------------------------------------------------
  // Lookup / resolution
  // ---------------------------------------------------------------------------

  /**
   * Finds a collection by title or id.
   *
   * @param query the collection title or id
   * @return the matching collection, if found
   */
  private Optional<RecipeCollection> findCollection(String query) {
    return ctx.collRepo().findAll().stream()
        .filter(c -> c.getTitle().equalsIgnoreCase(query) || c.getId().equalsIgnoreCase(query))
        .findFirst();
  }

  /**
   * Finds recipes that contain an ingredient name matching the query.
   *
   * @param ingredientQuery ingredient text to search for
   * @return matching recipes paired with their collection titles
   */
  private List<RecipeMatch> searchByIngredient(String ingredientQuery) {
    String q = ingredientQuery.toLowerCase(Locale.ROOT);
    List<RecipeMatch> out = new ArrayList<>();
    for (RecipeCollection c : ctx.collRepo().findAll()) {
      for (Recipe r : c.getRecipes()) {
        boolean found =
            r.getIngredients().stream()
                .map(Ingredient::getName)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .anyMatch(name -> name.contains(q));
        if (found) {
          out.add(new RecipeMatch(r, c.getTitle()));
        }
      }
    }
    return out;
  }

  /**
   * Resolves a user recipe query against title, id prefix, and partial title matches.
   *
   * @param query recipe selector text
   * @return a resolution containing one match, multiple matches, or not found state
   */
  private Resolution resolveRecipe(String query) {
    String q = query.toLowerCase(Locale.ROOT);
    List<RecipeMatch> all = allRecipeMatches();

    List<RecipeMatch> exact =
        all.stream().filter(m -> m.recipe.getTitle().equalsIgnoreCase(query)).toList();
    if (exact.size() == 1) {
      return Resolution.one(exact.get(0).recipe);
    }
    if (exact.size() > 1) {
      return Resolution.ambiguous(exact);
    }

    List<RecipeMatch> shortId =
        all.stream().filter(m -> m.recipe.getId().toLowerCase(Locale.ROOT).startsWith(q)).toList();
    if (shortId.size() == 1) {
      return Resolution.one(shortId.get(0).recipe);
    }
    if (shortId.size() > 1) {
      return Resolution.ambiguous(shortId);
    }

    List<RecipeMatch> partial =
        all.stream().filter(m -> m.recipe.getTitle().toLowerCase(Locale.ROOT).contains(q)).toList();

    if (partial.isEmpty()) {
      return Resolution.notFound();
    }
    if (partial.size() == 1) {
      return Resolution.one(partial.get(0).recipe);
    }
    return Resolution.ambiguous(partial);
  }

  /**
   * Returns the resolved recipe from a successful resolution.
   *
   * @param res the resolution result
   * @return the resolved recipe
   * @throws NullPointerException if the resolution does not contain a recipe
   */
  private static Recipe requireRecipe(Resolution res) {
    return Objects.requireNonNull(res.recipe, "recipe should be non-null when resolution is FOUND");
  }

  /**
   * Builds a flattened list of all recipes paired with their collection titles.
   *
   * @return all recipe matches across all collections
   */
  private List<RecipeMatch> allRecipeMatches() {
    List<RecipeMatch> out = new ArrayList<>();
    for (RecipeCollection c : ctx.collRepo().findAll()) {
      for (Recipe r : c.getRecipes()) {
        out.add(new RecipeMatch(r, c.getTitle()));
      }
    }
    return out;
  }

  /**
   * Saves a derived recipe to every collection that contains the original recipe.
   *
   * @param original the source recipe used to derive a new variant
   * @param derived the newly derived recipe to save
   */
  private void saveDerivedRecipe(Recipe original, Recipe derived) {
    for (RecipeCollection c : ctx.collRepo().findAll()) {
      if (c.containsRecipe(original.getId())) {
        ctx.collRepo().save(c.addRecipe(derived));
      }
    }
  }

  // ---------------------------------------------------------------------------
  // Formatting
  // ---------------------------------------------------------------------------

  /**
   * Formats a full recipe view with sections for ingredients and instructions.
   *
   * @param recipe the recipe to format
   * @return formatted multiline recipe text
   */
  private String formatRecipeFull(Recipe recipe) {
    StringBuilder sb = new StringBuilder();
    sb.append("════════════════════════════════════════\n");
    sb.append(recipe.getTitle()).append("\n");
    sb.append("════════════════════════════════════════\n");
    if (recipe.getServings() != null) {
      sb.append("Serves ").append(recipe.getServings()).append("\n");
    }

    sb.append("\nIngredients:\n");
    for (Ingredient i : recipe.getIngredients()) {
      sb.append("• ").append(i).append("\n");
    }

    sb.append("\nInstructions:\n");
    for (Instruction inst : recipe.getInstructions()) {
      sb.append(inst).append("\n");
    }
    return sb.toString();
  }

  /**
   * Formats a side-by-side ingredient comparison for scaling output.
   *
   * @param original the original recipe
   * @param scaled the scaled recipe
   * @param factor the scaling multiplier
   * @return formatted scaling preview text
   */
  private String formatScaleComparison(Recipe original, Recipe scaled, double factor) {
    StringBuilder sb = new StringBuilder();
    sb.append("════════════════════════════════════════\n");
    sb.append(original.getTitle()).append("\n");
    sb.append("Scale factor: ").append(formatNumber(factor)).append("x\n");
    sb.append("Ingredient                Original            Scaled\n");
    sb.append("────────────────────────────────────────────────────\n");

    int n = Math.min(original.getIngredients().size(), scaled.getIngredients().size());
    for (int i = 0; i < n; i++) {
      Ingredient a = original.getIngredients().get(i);
      Ingredient b = scaled.getIngredients().get(i);
      sb.append(
          String.format(
              "%-24s %-18s → %s%n",
              truncate(a.getName(), 20), formatIngredientQuantity(a), formatIngredientQuantity(b)));
    }
    return sb.toString();
  }

  /**
   * Formats a side-by-side ingredient comparison for conversion output.
   *
   * @param original the original recipe
   * @param converted the converted recipe
   * @param unit the target conversion unit
   * @return formatted conversion preview text
   */
  private String formatConvertComparison(Recipe original, Recipe converted, Unit unit) {
    StringBuilder sb = new StringBuilder();
    sb.append("════════════════════════════════════════\n");
    sb.append(original.getTitle()).append("\n");
    sb.append("Converted to ").append(unit.name()).append("\n");
    sb.append("Ingredient                Original            Converted\n");
    sb.append("────────────────────────────────────────────────────\n");

    int n = Math.min(original.getIngredients().size(), converted.getIngredients().size());
    for (int i = 0; i < n; i++) {
      Ingredient a = original.getIngredients().get(i);
      Ingredient b = converted.getIngredients().get(i);
      sb.append(
          String.format(
              "%-24s %-18s → %s%n",
              truncate(a.getName(), 20), formatIngredientQuantity(a), formatIngredientQuantity(b)));
    }
    return sb.toString();
  }

  /**
   * Formats an aggregated shopping list for display.
   *
   * @param list the shopping list to format
   * @return formatted shopping list text
   */
  private String formatShoppingList(ShoppingList list) {
    StringBuilder sb = new StringBuilder();
    sb.append("Measured Items:\n");
    for (ShoppingItem item : list.getItems()) {
      sb.append("• ").append(item.getQuantity()).append(" ").append(item.getName()).append("\n");
    }
    if (!list.getUncountableItems().isEmpty()) {
      sb.append("\nAlso needed:\n");
      for (String s : list.getUncountableItems()) {
        sb.append("• ").append(s).append("\n");
      }
    }
    sb.append("\nTotal: ")
        .append(list.getItems().size())
        .append(" measured items, ")
        .append(list.getUncountableItems().size())
        .append(" vague items");
    return sb.toString();
  }

  /**
   * Prints disambiguation options when a query matches multiple recipes.
   *
   * @param matches candidate recipe matches
   */
  private void printAmbiguous(List<RecipeMatch> matches) {
    ctx.println("Multiple recipes match:");
    boolean sameTitle =
        matches.stream().map(m -> m.recipe.getTitle().toLowerCase(Locale.ROOT)).distinct().count()
            == 1;

    for (RecipeMatch m : matches) {
      ctx.println(
          "- [" + shortId(m.recipe) + "] " + m.recipe.getTitle() + " (" + m.collectionTitle + ")");
    }

    if (sameTitle) {
      ctx.println("Please specify a short ID.");
    } else {
      ctx.println("Please specify a more complete title or short ID.");
    }
  }

  /**
   * Formats ingredient references for a single instruction step.
   *
   * @param refs ingredient references used by a step
   * @return a comma-separated description of referenced ingredients
   */
  private String formatRefs(List<IngredientRef> refs) {
    List<String> parts = new ArrayList<>();
    for (IngredientRef ref : refs) {
      String qty = ref.quantity() == null ? "to taste" : ref.quantity().toString();
      String extra = "";
      if (ref.ingredient() instanceof MeasuredIngredient m && m.getPreparation() != null) {
        extra = ", " + m.getPreparation();
      }
      parts.add(qty + " " + ref.ingredient().getName() + extra);
    }
    return String.join(", ", parts);
  }

  /**
   * Formats a short ingredient label for compact cook-mode output.
   *
   * @param ingredient the ingredient to format
   * @return short ingredient text
   */
  private String formatIngredientShort(Ingredient ingredient) {
    return ingredient.toString();
  }

  /**
   * Formats only the quantity portion for ingredient comparison tables.
   *
   * @param ingredient the ingredient to inspect
   * @return the quantity description for the ingredient
   */
  private String formatIngredientQuantity(Ingredient ingredient) {
    if (ingredient instanceof MeasuredIngredient m) {
      return m.getQuantity().toString();
    }
    if (ingredient instanceof VagueIngredient v) {
      return v.getDescription() == null || v.getDescription().isBlank()
          ? "to taste"
          : v.getDescription();
    }
    return ingredient.toString();
  }

  /**
   * Returns a bracketed source-type badge string.
   *
   * @param type the recipe source type
   * @return badge text for display
   */
  private static String badge(SourceType type) {
    return switch (type) {
      case PERSONAL -> "[Personal]";
      case PUBLISHED_BOOK -> "[Book]";
      case WEBSITE -> "[Website]";
    };
  }

  /**
   * Returns a readable short identifier for a recipe.
   *
   * @param r the recipe
   * @return up to the first eight characters of the recipe id
   */
  private static String shortId(Recipe r) {
    return r.getId().substring(0, Math.min(8, r.getId().length()));
  }

  /**
   * Truncates a string to a maximum length.
   *
   * @param s the source string
   * @param n the maximum character count
   * @return the original string if short enough, otherwise its prefix
   */
  private static String truncate(String s, int n) {
    return s.length() <= n ? s : s.substring(0, n);
  }

  /**
   * Formats a numeric value for user-facing output.
   *
   * @param d the number to format
   * @return formatted number with trimmed trailing zeros
   */
  private static String formatNumber(double d) {
    if (Math.abs(d - Math.rint(d)) < 1e-9) {
      return String.format(Locale.US, "%.1f", d).replaceAll("\\.0$", ".0");
    }
    return String.format(Locale.US, "%.3f", d).replaceAll("0*$", "").replaceAll("\\.$", "");
  }

  // ---------------------------------------------------------------------------
  // Small helper records
  // ---------------------------------------------------------------------------

  private enum ResolutionKind {
    FOUND,
    AMBIGUOUS,
    NOT_FOUND
  }

  private record RecipeMatch(Recipe recipe, String collectionTitle) {}

  private static final class Resolution {
    private final ResolutionKind kind;
    private final @Nullable Recipe recipe;
    private final List<RecipeMatch> matches;

    /**
     * Creates a resolution container for recipe lookup outcomes.
     *
     * @param kind the resolution kind
     * @param recipe the resolved recipe, if any
     * @param matches ambiguous matches when applicable
     */
    private Resolution(ResolutionKind kind, @Nullable Recipe recipe, List<RecipeMatch> matches) {
      this.kind = kind;
      this.recipe = recipe;
      this.matches = matches;
    }

    static Resolution one(Recipe recipe) {
      return new Resolution(ResolutionKind.FOUND, recipe, List.of());
    }

    static Resolution ambiguous(List<RecipeMatch> matches) {
      return new Resolution(ResolutionKind.AMBIGUOUS, null, matches);
    }

    static Resolution notFound() {
      return new Resolution(ResolutionKind.NOT_FOUND, null, List.of());
    }
  }
}
