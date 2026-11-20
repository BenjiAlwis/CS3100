package app.cookyourbooks.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import app.cookyourbooks.model.ExactQuantity;
import app.cookyourbooks.model.FractionalQuantity;
import app.cookyourbooks.model.Ingredient;
import app.cookyourbooks.model.Instruction;
import app.cookyourbooks.model.MeasuredIngredient;
import app.cookyourbooks.model.RangeQuantity;
import app.cookyourbooks.model.Recipe;
import app.cookyourbooks.model.Unit;
import app.cookyourbooks.model.VagueIngredient;
import app.cookyourbooks.repository.RecipeRepository;
import app.cookyourbooks.repository.RepositoryException;

/**
 * Comprehensive tests for the {@link JsonRecipeRepository} implementation.
 *
 * <p><strong>This test file is provided as a reference.</strong> It demonstrates thorough testing
 * of repository behavior including:
 *
 * <ul>
 *   <li>Round-trip serialization for all {@code Quantity} types (Exact, Fractional, Range)
 *   <li>Round-trip serialization for all {@code Ingredient} types (Measured, Vague)
 *   <li>All repository operations (save, findById, findByTitle, findAllByTitle, findAll, delete)
 *   <li>Edge cases and error handling
 * </ul>
 *
 * <p><strong>Order Independence:</strong> Methods like {@link RecipeRepository#findAll()} have
 * unspecified ordering. Tests use order-independent assertions (e.g., checking size and using
 * stream operations) rather than assuming specific order.
 */
@DisplayName("JsonRecipeRepository")
class JsonRecipeRepositoryTest {

  @TempDir Path tempDir;

  private RecipeRepository repository;

  @BeforeEach
  void setUp() {
    if (tempDir == null) {
      throw new IllegalStateException("tempDir is null, should have been injected by JUnit");
    }
    repository = new JsonRecipeRepository(tempDir);
  }

  // ===================================================================================
  // Round-trip tests for Quantity types
  // ===================================================================================

  @Nested
  @DisplayName("Quantity polymorphism")
  class QuantityPolymorphism {

    @Test
    @DisplayName("round-trip preserves ExactQuantity")
    void roundTripPreservesExactQuantity() {
      Recipe recipe =
          new Recipe(
              "exact-qty-id",
              "Exact Quantity Recipe",
              new ExactQuantity(8, Unit.WHOLE),
              List.of(
                  new MeasuredIngredient("flour", new ExactQuantity(2.5, Unit.CUP), null, null)),
              List.of(new Instruction(1, "Mix well", List.of())),
              List.of());

      repository.save(recipe);
      Optional<Recipe> loaded = repository.findById("exact-qty-id");

      assertTrue(loaded.isPresent());
      assertEquals(recipe, loaded.get());
      // Verify the quantity type is preserved
      Ingredient ing = loaded.get().getIngredients().get(0);
      assertTrue(ing instanceof MeasuredIngredient);
      assertTrue(((MeasuredIngredient) ing).getQuantity() instanceof ExactQuantity);
    }

    @Test
    @DisplayName("round-trip preserves FractionalQuantity")
    void roundTripPreservesFractionalQuantity() {
      Recipe recipe =
          new Recipe(
              "frac-qty-id",
              "Fractional Quantity Recipe",
              new FractionalQuantity(2, 1, 2, Unit.WHOLE), // 2 1/2 servings
              List.of(
                  new MeasuredIngredient(
                      "butter", new FractionalQuantity(0, 1, 4, Unit.CUP), null, null)),
              List.of(new Instruction(1, "Cream butter", List.of())),
              List.of());

      repository.save(recipe);
      Optional<Recipe> loaded = repository.findById("frac-qty-id");

      assertTrue(loaded.isPresent());
      assertEquals(recipe, loaded.get());
      // Verify the quantity type is preserved
      Ingredient ing = loaded.get().getIngredients().get(0);
      assertTrue(ing instanceof MeasuredIngredient);
      assertTrue(((MeasuredIngredient) ing).getQuantity() instanceof FractionalQuantity);
    }

    @Test
    @DisplayName("round-trip preserves RangeQuantity")
    void roundTripPreservesRangeQuantity() {
      Recipe recipe =
          new Recipe(
              "range-qty-id",
              "Range Quantity Recipe",
              new RangeQuantity(4, 6, Unit.WHOLE), // 4-6 servings
              List.of(
                  new MeasuredIngredient(
                      "sugar", new RangeQuantity(1.5, 2.0, Unit.CUP), null, null)),
              List.of(new Instruction(1, "Add sugar to taste", List.of())),
              List.of());

      repository.save(recipe);
      Optional<Recipe> loaded = repository.findById("range-qty-id");

      assertTrue(loaded.isPresent());
      assertEquals(recipe, loaded.get());
      // Verify the quantity type is preserved
      Ingredient ing = loaded.get().getIngredients().get(0);
      assertTrue(ing instanceof MeasuredIngredient);
      assertTrue(((MeasuredIngredient) ing).getQuantity() instanceof RangeQuantity);
    }
  }

  // ===================================================================================
  // Round-trip tests for Ingredient types
  // ===================================================================================

  @Nested
  @DisplayName("Ingredient polymorphism")
  class IngredientPolymorphism {

    @Test
    @DisplayName("round-trip preserves MeasuredIngredient with all fields")
    void roundTripPreservesMeasuredIngredientWithAllFields() {
      Recipe recipe =
          new Recipe(
              "measured-ing-id",
              "Measured Ingredient Recipe",
              null,
              List.of(
                  new MeasuredIngredient(
                      "butter", new ExactQuantity(1, Unit.CUP), "softened", "use unsalted")),
              List.of(new Instruction(1, "Cream butter", List.of())),
              List.of());

      repository.save(recipe);
      Optional<Recipe> loaded = repository.findById("measured-ing-id");

      assertTrue(loaded.isPresent());
      assertEquals(recipe, loaded.get());
      Ingredient ing = loaded.get().getIngredients().get(0);
      assertTrue(ing instanceof MeasuredIngredient);
      MeasuredIngredient mi = (MeasuredIngredient) ing;
      assertEquals("butter", mi.getName());
      assertEquals("softened", mi.getPreparation());
      assertEquals("use unsalted", mi.getNotes());
    }

    @Test
    @DisplayName("round-trip preserves VagueIngredient")
    void roundTripPreservesVagueIngredient() {
      Recipe recipe =
          new Recipe(
              "vague-ing-id",
              "Vague Ingredient Recipe",
              null,
              List.of(
                  new VagueIngredient("salt", "to taste", null, null),
                  new VagueIngredient("fresh herbs", "for garnish", "chopped", "optional")),
              List.of(new Instruction(1, "Season and garnish", List.of())),
              List.of());

      repository.save(recipe);
      Optional<Recipe> loaded = repository.findById("vague-ing-id");

      assertTrue(loaded.isPresent());
      assertEquals(recipe, loaded.get());
      assertTrue(loaded.get().getIngredients().get(0) instanceof VagueIngredient);
      assertTrue(loaded.get().getIngredients().get(1) instanceof VagueIngredient);
    }

    @Test
    @DisplayName("round-trip preserves mixed ingredient types")
    void roundTripPreservesMixedIngredientTypes() {
      Recipe recipe =
          new Recipe(
              "mixed-ing-id",
              "Mixed Ingredients Recipe",
              new ExactQuantity(4, Unit.WHOLE),
              List.of(
                  new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), "sifted", null),
                  new VagueIngredient("salt", "to taste", null, null),
                  new MeasuredIngredient(
                      "milk", new FractionalQuantity(1, 1, 2, Unit.CUP), null, null),
                  new VagueIngredient("cooking spray", null, null, null)),
              List.of(
                  new Instruction(1, "Combine dry ingredients", List.of()),
                  new Instruction(2, "Add wet ingredients", List.of())),
              List.of());

      repository.save(recipe);
      Optional<Recipe> loaded = repository.findById("mixed-ing-id");

      assertTrue(loaded.isPresent());
      assertEquals(recipe, loaded.get());
      assertEquals(4, loaded.get().getIngredients().size());
      assertTrue(loaded.get().getIngredients().get(0) instanceof MeasuredIngredient);
      assertTrue(loaded.get().getIngredients().get(1) instanceof VagueIngredient);
      assertTrue(loaded.get().getIngredients().get(2) instanceof MeasuredIngredient);
      assertTrue(loaded.get().getIngredients().get(3) instanceof VagueIngredient);
    }
  }

  // ===================================================================================
  // save and findById tests
  // ===================================================================================

  @Nested
  @DisplayName("save and findById")
  class SaveAndFindById {

    @Test
    @DisplayName("round-trip preserves recipe with all fields")
    void roundTripPreservesRecipeWithAllFields() {
      Recipe recipe =
          new Recipe(
              "test-id",
              "Chocolate Cake",
              new ExactQuantity(8, Unit.WHOLE),
              List.of(new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null)),
              List.of(new Instruction(1, "Mix ingredients", List.of())),
              List.of());

      repository.save(recipe);
      Optional<Recipe> loaded = repository.findById("test-id");

      assertTrue(loaded.isPresent());
      assertEquals(recipe, loaded.get());
    }

    @Test
    @DisplayName("round-trip preserves recipe with minimal fields")
    void roundTripPreservesRecipeWithMinimalFields() {
      Recipe recipe =
          new Recipe("minimal-id", "Simple Recipe", null, List.of(), List.of(), List.of());

      repository.save(recipe);
      Optional<Recipe> loaded = repository.findById("minimal-id");

      assertTrue(loaded.isPresent());
      assertEquals(recipe, loaded.get());
    }

    @Test
    @DisplayName("findById returns empty for non-existent id")
    void findByIdReturnsEmptyForNonExistentId() {
      assertTrue(repository.findById("does-not-exist").isEmpty());
    }

    @Test
    @DisplayName("save overwrites existing recipe with same ID")
    void saveOverwritesExistingRecipe() {
      Recipe original =
          new Recipe("overwrite-id", "Original Title", null, List.of(), List.of(), List.of());
      Recipe updated =
          new Recipe("overwrite-id", "Updated Title", null, List.of(), List.of(), List.of());

      repository.save(original);
      repository.save(updated);

      Optional<Recipe> loaded = repository.findById("overwrite-id");
      assertTrue(loaded.isPresent());
      assertEquals("Updated Title", loaded.get().getTitle());
    }
  }

  // ===================================================================================
  // findByTitle tests
  // ===================================================================================

  @Nested
  @DisplayName("findByTitle")
  class FindByTitle {

    @Test
    @DisplayName("finds recipe with case-insensitive match")
    void findsRecipeCaseInsensitive() {
      Recipe recipe = new Recipe("id-1", "Chocolate Cake", null, List.of(), List.of(), List.of());
      repository.save(recipe);

      assertTrue(repository.findByTitle("chocolate cake").isPresent());
      assertTrue(repository.findByTitle("CHOCOLATE CAKE").isPresent());
      assertTrue(repository.findByTitle("Chocolate Cake").isPresent());
      assertTrue(repository.findByTitle("ChOcOlAtE cAkE").isPresent());
    }

    @Test
    @DisplayName("returns empty when no match found")
    void returnsEmptyWhenNoMatch() {
      Recipe recipe = new Recipe("id-1", "Chocolate Cake", null, List.of(), List.of(), List.of());
      repository.save(recipe);

      assertTrue(repository.findByTitle("Vanilla Cake").isEmpty());
      assertTrue(repository.findByTitle("Chocolate").isEmpty()); // Partial match should not work
    }

    @Test
    @DisplayName("returns any match when multiple recipes have same title")
    void returnsAnyMatchWhenMultipleSameTitle() {
      Recipe r1 = new Recipe("id-1", "Cake", null, List.of(), List.of(), List.of());
      Recipe r2 = new Recipe("id-2", "Cake", null, List.of(), List.of(), List.of());
      repository.save(r1);
      repository.save(r2);

      Optional<Recipe> found = repository.findByTitle("Cake");
      assertTrue(found.isPresent());
      // We don't assert which one - either is valid
      assertTrue(found.get().getId().equals("id-1") || found.get().getId().equals("id-2"));
    }
  }

  // ===================================================================================
  // findAllByTitle tests
  // ===================================================================================

  @Nested
  @DisplayName("findAllByTitle")
  class FindAllByTitle {

    @Test
    @DisplayName("returns all recipes with matching title (case-insensitive)")
    void returnsAllMatchingRecipes() {
      Recipe r1 = new Recipe("id-1", "Chocolate Cake", null, List.of(), List.of(), List.of());
      Recipe r2 = new Recipe("id-2", "chocolate cake", null, List.of(), List.of(), List.of());
      Recipe r3 = new Recipe("id-3", "Vanilla Cake", null, List.of(), List.of(), List.of());
      repository.save(r1);
      repository.save(r2);
      repository.save(r3);

      List<Recipe> results = repository.findAllByTitle("CHOCOLATE CAKE");

      assertEquals(2, results.size());
      assertTrue(results.stream().anyMatch(r -> r.getId().equals("id-1")));
      assertTrue(results.stream().anyMatch(r -> r.getId().equals("id-2")));
    }

    @Test
    @DisplayName("returns empty list when no matches")
    void returnsEmptyListWhenNoMatches() {
      Recipe recipe = new Recipe("id-1", "Chocolate Cake", null, List.of(), List.of(), List.of());
      repository.save(recipe);

      List<Recipe> results = repository.findAllByTitle("Strawberry Shortcake");
      assertTrue(results.isEmpty());
    }
  }

  // ===================================================================================
  // findAll tests
  // ===================================================================================

  @Nested
  @DisplayName("findAll")
  class FindAll {

    @Test
    @DisplayName("returns empty list for empty repository")
    void returnsEmptyListForEmptyRepository() {
      List<Recipe> results = repository.findAll();
      assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("returns all saved recipes")
    void returnsAllSavedRecipes() {
      Recipe r1 = new Recipe("id-1", "Recipe One", null, List.of(), List.of(), List.of());
      Recipe r2 = new Recipe("id-2", "Recipe Two", null, List.of(), List.of(), List.of());
      Recipe r3 = new Recipe("id-3", "Recipe Three", null, List.of(), List.of(), List.of());
      repository.save(r1);
      repository.save(r2);
      repository.save(r3);

      List<Recipe> results = repository.findAll();

      assertEquals(3, results.size());
      // Order-independent assertions
      assertTrue(results.stream().anyMatch(r -> r.getId().equals("id-1")));
      assertTrue(results.stream().anyMatch(r -> r.getId().equals("id-2")));
      assertTrue(results.stream().anyMatch(r -> r.getId().equals("id-3")));
    }
  }

  // ===================================================================================
  // delete tests
  // ===================================================================================

  @Nested
  @DisplayName("delete")
  class Delete {

    @Test
    @DisplayName("removes recipe from repository")
    void removesRecipeFromRepository() {
      Recipe recipe =
          new Recipe("delete-me", "To Be Deleted", null, List.of(), List.of(), List.of());
      repository.save(recipe);
      assertTrue(repository.findById("delete-me").isPresent());

      repository.delete("delete-me");

      assertTrue(repository.findById("delete-me").isEmpty());
    }

    @Test
    @DisplayName("is idempotent - deleting non-existent recipe does not throw")
    void isIdempotent() {
      // Delete a recipe that was never saved
      repository.delete("never-existed");

      // Delete a recipe twice
      Recipe recipe = new Recipe("delete-twice", "Test", null, List.of(), List.of(), List.of());
      repository.save(recipe);
      repository.delete("delete-twice");
      repository.delete("delete-twice"); // Should not throw
    }
  }

  // ===================================================================================
  // Constructor and error handling tests
  // ===================================================================================

  @Nested
  @DisplayName("constructor")
  class Constructor {

    @Test
    @DisplayName("creates directory if it doesn't exist")
    void createsDirectoryIfNotExists() {
      Path newDir = tempDir.resolve("new-repo-dir");
      assertFalse(Files.exists(newDir));

      new JsonRecipeRepository(newDir);

      assertTrue(Files.exists(newDir));
      assertTrue(Files.isDirectory(newDir));
    }

    @Test
    @DisplayName("throws RepositoryException if path exists but is not a directory")
    void throwsIfPathIsNotDirectory() throws IOException {
      Path file = tempDir.resolve("not-a-directory.txt");
      Files.createFile(file);

      assertThrows(RepositoryException.class, () -> new JsonRecipeRepository(file));
    }
  }

  @Nested
  @DisplayName("error handling")
  class ErrorHandling {

    @Test
    @DisplayName("throws RepositoryException when reading corrupt JSON")
    void throwsWhenReadingCorruptJson() throws IOException {
      // Save a valid recipe first
      Recipe recipe = new Recipe("corrupt-test", "Test", null, List.of(), List.of(), List.of());
      repository.save(recipe);

      // Corrupt the JSON file
      Path jsonFile = tempDir.resolve("corrupt-test.json");
      Files.writeString(jsonFile, "{ this is not valid JSON }");

      // Should throw when trying to read
      assertThrows(RepositoryException.class, () -> repository.findById("corrupt-test"));
    }

    @Test
    @DisplayName("findAll throws RepositoryException when directory contains corrupt JSON")
    void findAllThrowsWhenCorruptJson() throws IOException {
      // Save a valid recipe
      Recipe recipe = new Recipe("valid", "Valid Recipe", null, List.of(), List.of(), List.of());
      repository.save(recipe);

      // Create a corrupt JSON file
      Path corruptFile = tempDir.resolve("corrupt.json");
      Files.writeString(corruptFile, "not json at all");

      // findAll should throw when it encounters the corrupt file
      assertThrows(RepositoryException.class, () -> repository.findAll());
    }
  }
}
