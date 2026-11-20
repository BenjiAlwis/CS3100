package app.cookyourbooks.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for recipe collection implementations.
 *
 * <p>Write tests against the interfaces ({@link Cookbook}, {@link PersonalCollection}, {@link
 * WebCollection}), not your implementation classes directly.
 */
@DisplayName("RecipeCollection")
class RecipeCollectionTest {

  @Nested
  @DisplayName("Cookbook")
  class CookbookTests {

    @Nested
    @DisplayName("addRecipe")
    class AddRecipe {

      @Test
      @DisplayName("returns new cookbook with recipe added")
      void returnsNewCookbookWithRecipeAdded() {
        Cookbook original = CookbookImpl.builder().title("Test Cookbook").build();
        Recipe recipe = new Recipe("r1", "Pancakes", null, emptyList(), emptyList(), emptyList());

        Cookbook updated = original.addRecipe(recipe);

        assertTrue(original.getRecipes().isEmpty(), "Original should be unchanged");
        assertEquals(1, updated.getRecipes().size());
        assertEquals("Pancakes", updated.getRecipes().get(0).getTitle());
      }

      @Test
      @DisplayName("throws when adding recipe with duplicate ID")
      void throwsWhenAddingDuplicateId() {
        Recipe recipe = new Recipe("r1", "Pancakes", null, emptyList(), emptyList(), emptyList());
        Cookbook cookbook =
          CookbookImpl.builder().title("Test Cookbook").recipes(listOf(recipe)).build();

        Recipe duplicate =
          new Recipe(
            "r1", "Different Recipe", null, emptyList(), emptyList(), emptyList());

        assertThrows(IllegalArgumentException.class, () -> cookbook.addRecipe(duplicate));
      }
    }

    @Nested
    @DisplayName("getSourceType")
    class GetSourceType {

      @Test
      @DisplayName("returns PUBLISHED_BOOK for cookbooks")
      void returnsPublishedBook() {
        Cookbook cookbook = CookbookImpl.builder().id("c1").title("CB").build();
        assertEquals(SourceType.PUBLISHED_BOOK, cookbook.getSourceType());
      }
    }

    @Nested
    @DisplayName("getRecipes")
    class GetRecipes {

      @Test
      @DisplayName("returned list cannot mutate internal state")
      void returnedListCannotMutateInternalState() {
        Recipe r1 = new Recipe("r1", "One", null, emptyList(), emptyList(), emptyList());
        Cookbook cookbook =
          CookbookImpl.builder().id("c1").title("CB").recipes(listOf(r1)).build();

        List<Recipe> returned = cookbook.getRecipes();

        try {
          returned.add(new Recipe("r2", "Two", null, emptyList(), emptyList(), emptyList()));
        } catch (UnsupportedOperationException ignored) {

        }

        assertEquals(1, cookbook.getRecipes().size());
        assertEquals("r1", cookbook.getRecipes().get(0).getId());
      }
    }

    @Nested
    @DisplayName("containsRecipe")
    class ContainsRecipe {

      @Test
      @DisplayName("returns true iff a recipe with that ID exists")
      void returnsTrueIffIdExists() {
        Recipe a = new Recipe("a", "Alpha", null, emptyList(), emptyList(), emptyList());
        Recipe b = new Recipe("b", "Beta", null, emptyList(), emptyList(), emptyList());
        Cookbook cookbook =
          CookbookImpl.builder().id("c1").title("CB").recipes(listOf(a, b)).build();

        assertTrue(cookbook.containsRecipe("a"));
        assertTrue(cookbook.containsRecipe("b"));
        assertFalse(cookbook.containsRecipe("nope"));
      }
    }

    @Nested
    @DisplayName("findRecipeById")
    class FindRecipeById {

      @Test
      @DisplayName("returns the specific matching recipe, not just any recipe")
      void returnsSpecificMatchingRecipe() {
        Recipe a = new Recipe("a", "Alpha", null, emptyList(), emptyList(), emptyList());
        Recipe b = new Recipe("b", "Beta", null, emptyList(), emptyList(), emptyList());
        Recipe c = new Recipe("c", "Gamma", null, emptyList(), emptyList(), emptyList());
        Cookbook cookbook =
          CookbookImpl.builder().id("c1").title("CB").recipes(listOf(a, b, c)).build();

        assertEquals(a, cookbook.findRecipeById("a").orElseThrow());
        assertEquals(b, cookbook.findRecipeById("b").orElseThrow());
        assertEquals(c, cookbook.findRecipeById("c").orElseThrow());

        // Extra strength: fail if implementation returns first/last by mistake.
        assertNotEquals(a, cookbook.findRecipeById("b").orElseThrow());
        assertNotEquals(c, cookbook.findRecipeById("b").orElseThrow());

        assertTrue(cookbook.findRecipeById("nope").isEmpty());
      }
    }

    @Nested
    @DisplayName("removeRecipe")
    class RemoveRecipe {

      @Test
      @DisplayName("throws when removing a non-existent recipe ID")
      void throwsWhenMissing() {
        Cookbook cookbook = CookbookImpl.builder().id("c1").title("CB").build();
        assertThrows(IllegalArgumentException.class, () -> cookbook.removeRecipe("does-not-exist"));
      }
    }

    @Test
    @DisplayName("getRecipes returns a list that cannot mutate the collection's internal state")
    void getRecipesIsImmutableViewOrDefensiveCopy() {
      Recipe r1 = new Recipe("r1", "One", null, emptyList(), emptyList(), emptyList());
      Recipe r2 = new Recipe("r2", "Two", null, emptyList(), emptyList(), emptyList());

      Cookbook cookbook =
          CookbookImpl.builder().id("c1").title("Test").recipes(listOf(r1, r2)).build();

      List<Recipe> exposed = cookbook.getRecipes();

      boolean threw = false;
      try {
        exposed.remove(0);
      } catch (UnsupportedOperationException e) {
        threw = true;
      }

      assertEquals(2, cookbook.getRecipes().size());
      assertTrue(cookbook.containsRecipe("r1"));
      assertTrue(cookbook.containsRecipe("r2"));

      if (!threw) {
        assertEquals(1, exposed.size(), "Local list may have changed, but cookbook must not");
        assertEquals(2, cookbook.getRecipes().size(), "Cookbook must remain unchanged");
      }
    }
  }

  @Nested
  @DisplayName("PersonalCollection")
  class PersonalCollectionTests {

    @Test
    @DisplayName("blank description/notes treated as empty Optional")
    void blankOptionalFields() {
      PersonalCollection c =
          PersonalCollectionImpl.builder()
              .id("p1")
              .title("Family")
              .description("   ")
              .notes("")
              .build();

      assertTrue(c.getDescription().isEmpty());
      assertTrue(c.getNotes().isEmpty());
      assertEquals(SourceType.PERSONAL, c.getSourceType());
    }

    @Test
    @DisplayName("removeRecipe throws when not present")
    void removeThrowsWhenMissing() {
      PersonalCollection c = PersonalCollectionImpl.builder().title("Family").build();
      assertThrows(IllegalArgumentException.class, () -> c.removeRecipe("nope"));
    }

    @Test
    @DisplayName(
        "containsRecipe and findRecipeById work by ID (not title) and return correct recipe")
    void containsAndFindRecipeByIdAreCorrect() {
        Recipe a = new Recipe("a", "Alpha", null, emptyList(), emptyList(), emptyList());
        Recipe b = new Recipe("b", "Beta", null, emptyList(), emptyList(), emptyList());

      PersonalCollection c =
          PersonalCollectionImpl.builder().id("p2").title("Family").recipes(listOf(a, b)).build();

      assertTrue(c.containsRecipe("a"));
      assertFalse(c.containsRecipe("nope"));

      assertEquals(a, c.findRecipeById("a").orElseThrow());
      assertEquals(b, c.findRecipeById("b").orElseThrow());
      assertTrue(c.findRecipeById("nope").isEmpty());
    }
  }

  @Nested
  @DisplayName("WebCollection")
  class WebCollectionTests {

    @Test
    @DisplayName("requires sourceUrl at build time")
    void requiresSourceUrl() {
      assertThrows(
          IllegalStateException.class, () -> WebCollectionImpl.builder().title("Web").build());
    }

    @Test
    @DisplayName("blank siteName treated as empty Optional")
    void blankSiteName() {
      WebCollection c =
          WebCollectionImpl.builder()
              .id("w1")
              .title("Web")
            .sourceUrl(Objects.requireNonNull(URI.create("https://example.com")))
              .siteName("   ")
              .build();

      assertTrue(c.getSiteName().isEmpty());
      assertEquals(SourceType.WEBSITE, c.getSourceType());
    }

    @Test
    @DisplayName("containsRecipe and findRecipeById return correct recipe")
    void containsAndFindRecipeByIdAreCorrect() {
      Recipe a = new Recipe("a", "Alpha", null, emptyList(), emptyList(), emptyList());
      Recipe b = new Recipe("b", "Beta", null, emptyList(), emptyList(), emptyList());

      WebCollection c =
          WebCollectionImpl.builder()
              .id("w2")
              .title("Web")
            .sourceUrl(Objects.requireNonNull(URI.create("https://example.com")))
            .recipes(listOf(a, b))
              .build();

      assertTrue(c.containsRecipe("a"));
      assertFalse(c.containsRecipe("nope"));

      assertEquals(a, c.findRecipeById("a").orElseThrow());
      assertEquals(b, c.findRecipeById("b").orElseThrow());
      assertTrue(c.findRecipeById("nope").isEmpty());
    }
  }

  @Nested
  @DisplayName("Recipe list immutability")
  class RecipeListImmutability {

    @Test
    @DisplayName("PersonalCollection getRecipes cannot mutate internal state")
    void personalCollectionRecipesImmutable() {
      Recipe r1 = new Recipe("r1", "One", null, emptyList(), emptyList(), emptyList());
      Recipe r2 = new Recipe("r2", "Two", null, emptyList(), emptyList(), emptyList());

      PersonalCollection c =
          PersonalCollectionImpl.builder().title("Family").recipes(listOf(r1, r2)).build();

      List<Recipe> exposed = c.getRecipes();

      try {
        exposed.remove(0);
      } catch (UnsupportedOperationException ignored) {

      }

      assertEquals(2, c.getRecipes().size());
      assertTrue(c.containsRecipe("r1"));
      assertTrue(c.containsRecipe("r2"));
    }

    @Test
    @DisplayName("WebCollection getRecipes cannot mutate internal state")
    void webCollectionRecipesImmutable() {
      Recipe r1 = new Recipe("r1", "One", null, emptyList(), emptyList(), emptyList());
      Recipe r2 = new Recipe("r2", "Two", null, emptyList(), emptyList(), emptyList());

      WebCollection c =
          WebCollectionImpl.builder()
              .title("Web")
              .sourceUrl(Objects.requireNonNull(URI.create("https://example.com")))
              .recipes(listOf(r1, r2))
              .build();

      List<Recipe> exposed = c.getRecipes();

      try {
        exposed.clear();
      } catch (UnsupportedOperationException ignored) {

      }

      assertEquals(2, c.getRecipes().size());
      assertTrue(c.containsRecipe("r1"));
      assertTrue(c.containsRecipe("r2"));
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
