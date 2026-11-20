package app.cookyourbooks.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link UserLibrary} implementation.
 *
 * <p>Write tests against the {@link UserLibrary} interface, not your implementation class.
 *
 * <p><strong>Order Independence:</strong> Methods like {@link UserLibrary#getCollections()} and
 * {@link UserLibrary#findRecipesByTitle(String)} have unspecified ordering. Do not write tests that
 * depend on a specific order.
 */
@DisplayName("UserLibrary")
class UserLibraryTest {

  @Nested
  @DisplayName("addCollection")
  class AddCollection {

    @Test
    @DisplayName("returns new library with collection added")
    void returnsNewLibraryWithCollectionAdded() {
      UserLibrary library = new UserLibraryImpl(List.of());
      Cookbook cookbook = CookbookImpl.builder().id("c1").title("My Cookbook").build();

      UserLibrary updated = library.addCollection(cookbook);

      assertTrue(library.getCollections().isEmpty(), "Original should be unchanged");
      assertEquals(1, updated.getCollections().size());
    }

    @Test
    @DisplayName("throws when adding collection with duplicate ID")
    void throwsWhenAddingDuplicateId() {
      Cookbook c1 = CookbookImpl.builder().id("same-id").title("First").build();
      Cookbook c2 = CookbookImpl.builder().id("same-id").title("Second").build();
      UserLibrary library = new UserLibraryImpl(List.of(c1));

      assertThrows(IllegalArgumentException.class, () -> library.addCollection(c2));
    }
  }

  @Nested
  @DisplayName("findRecipesByTitle")
  class FindRecipesByTitle {

    @Test
    @DisplayName("finds recipes across multiple collections with case-insensitive match")
    void findsRecipesAcrossCollections() {
      Recipe r1 = new Recipe("r1", "Chocolate Cake", null, List.of(), List.of(), List.of());
      Recipe r2 = new Recipe("r2", "chocolate cake", null, List.of(), List.of(), List.of());
      Cookbook c1 = CookbookImpl.builder().title("C1").recipes(List.of(r1)).build();
      PersonalCollection c2 =
          PersonalCollectionImpl.builder().title("C2").recipes(List.of(r2)).build();
      UserLibrary library = new UserLibraryImpl(List.of(c1, c2));

      List<Recipe> results = library.findRecipesByTitle("CHOCOLATE CAKE");

      assertEquals(2, results.size());
    }

    @Test
    @DisplayName("returns empty when none match exactly (case-insensitive)")
    void returnsEmptyWhenNoMatch() {
      Recipe r1 = new Recipe("r1", "Chocolate Cake", null, List.of(), List.of(), List.of());
      Cookbook c1 = CookbookImpl.builder().title("C1").recipes(List.of(r1)).build();
      UserLibrary library = new UserLibraryImpl(List.of(c1));

      assertTrue(library.findRecipesByTitle("Chocolate").isEmpty());
      assertTrue(library.findRecipesByTitle("Vanilla Cake").isEmpty());
    }
  }

  @Nested
  @DisplayName("findCollectionByTitle / findAllCollectionsByTitle")
  class FindCollectionsByTitle {

    @Test
    @DisplayName("findCollectionByTitle returns any matching collection (case-insensitive)")
    void findAnyByTitle() {
      Cookbook c1 = CookbookImpl.builder().id("a").title("Same").build();
      PersonalCollection c2 = PersonalCollectionImpl.builder().id("b").title("same").build();
      UserLibrary library = new UserLibraryImpl(List.of(c1, c2));

      assertTrue(library.findCollectionByTitle("SAME").isPresent());
    }

    @Test
    @DisplayName("findAllCollectionsByTitle returns all matching collections")
    void findAllByTitle() {
      Cookbook c1 = CookbookImpl.builder().id("a").title("Same").build();
      PersonalCollection c2 = PersonalCollectionImpl.builder().id("b").title("same").build();
      Cookbook c3 = CookbookImpl.builder().id("c").title("Other").build();
      UserLibrary library = new UserLibraryImpl(List.of(c1, c2, c3));

      assertEquals(2, library.findAllCollectionsByTitle("SAME").size());
    }
  }

  @Nested
  @DisplayName("findRecipeById")
  class FindRecipeById {

    @Test
    @DisplayName("finds recipe by id across collections")
    void findsAcrossCollections() {
      Recipe r1 = new Recipe("r1", "A", null, List.of(), List.of(), List.of());
      Recipe r2 = new Recipe("r2", "B", null, List.of(), List.of(), List.of());
      Cookbook c1 = CookbookImpl.builder().title("C1").recipes(List.of(r1)).build();
      PersonalCollection c2 =
          PersonalCollectionImpl.builder().title("C2").recipes(List.of(r2)).build();

      UserLibrary library = new UserLibraryImpl(List.of(c1, c2));

      assertTrue(library.findRecipeById("r2").isPresent());
      assertTrue(library.findRecipeById("nope").isEmpty());
    }
  }

  @Nested
  @DisplayName("removeCollection")
  class RemoveCollection {

    @Test
    @DisplayName("throws when removing a non-existent collection ID")
    void throwsWhenRemovingMissingCollection() {
      Cookbook c1 = CookbookImpl.builder().id("c1").title("One").build();
      UserLibrary library = new UserLibraryImpl(List.of(c1));

      assertThrows(
          IllegalArgumentException.class, () -> library.removeCollection("does-not-exist"));
    }

    @Test
    @DisplayName("returns new library with the collection removed and leaves original unchanged")
    void removesExistingCollectionImmutably() {
      Cookbook c1 = CookbookImpl.builder().id("c1").title("One").build();
      Cookbook c2 = CookbookImpl.builder().id("c2").title("Two").build();
      UserLibrary library = new UserLibraryImpl(List.of(c1, c2));

      UserLibrary updated = library.removeCollection("c1");

      // original unchanged
      assertEquals(2, library.getCollections().size());

      // updated has one fewer (order unspecified, so check by ID membership)
      assertEquals(1, updated.getCollections().size());
      assertTrue(updated.findCollectionById("c1").isEmpty());
      assertTrue(updated.findCollectionById("c2").isPresent());
    }
  }
}
