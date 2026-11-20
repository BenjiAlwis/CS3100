package app.cookyourbooks.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import app.cookyourbooks.model.CookbookImpl;
import app.cookyourbooks.model.ExactQuantity;
import app.cookyourbooks.model.Instruction;
import app.cookyourbooks.model.MeasuredIngredient;
import app.cookyourbooks.model.PersonalCollectionImpl;
import app.cookyourbooks.model.Recipe;
import app.cookyourbooks.model.Unit;
import app.cookyourbooks.model.WebCollectionImpl;

/**
 * Tests for the MarkdownExporter class.
 *
 * <p>For {@code exportRecipe}, test exact string matches against the expected Markdown format. For
 * {@code exportCollection}, focus on verifying that required content is present rather than exact
 * format.
 */
@DisplayName("MarkdownExporter")
class MarkdownExporterTest {

  private final MarkdownExporter exporter = new MarkdownExporter();

  @TempDir private @Nullable Path tempDir;

  @Nested
  @DisplayName("exportRecipe")
  class ExportRecipe {

    @Test
    @DisplayName("exports recipe with servings in correct format")
    void exportsRecipeWithServings() {
      Recipe recipe =
          new Recipe(
              "id",
              "Pancakes",
              new ExactQuantity(4, Unit.WHOLE),
              List.of(new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null)),
              listOf(new Instruction(1, "Mix ingredients", emptyList())),
              emptyList());

      String markdown = exporter.exportRecipe(recipe);

      assertTrue(markdown.startsWith("# Pancakes\n"));
      assertTrue(markdown.contains("_Serves: 4 whole_"));
      assertTrue(markdown.contains("## Ingredients"));
      assertTrue(markdown.contains("- 2 cups flour"));
      assertTrue(markdown.contains("## Instructions"));
      assertTrue(markdown.contains("1. Mix ingredients"));
      assertTrue(markdown.contains("_Exported from CookYourBooks"));
    }

    @Test
    @DisplayName("omits servings line when recipe has no servings")
    void omitsServingsWhenNull() {
      Recipe recipe =
          new Recipe("id", "Simple Recipe", null, emptyList(), emptyList(), emptyList());

      String markdown = exporter.exportRecipe(recipe);

      assertTrue(markdown.startsWith("# Simple Recipe\n\n## Ingredients"));
    }

    @Test
    @DisplayName("special characters preserved as-is")
    void preservesSpecialCharacters() {
      Recipe recipe =
          new Recipe(
              "id",
              "Crêpes *Best*",
              null,
              List.of(
                  new MeasuredIngredient("sugar #1", new ExactQuantity(1, Unit.CUP), null, null)),
              listOf(new Instruction(1, "Stir & serve", emptyList())),
              emptyList());

      String md = exporter.exportRecipe(recipe);
      assertTrue(md.contains("# Crêpes *Best*\n"));
      assertTrue(md.contains("- 1 cup sugar #1\n"));
      assertTrue(md.contains("1. Stir & serve\n"));
    }
  }

  @Nested
  @DisplayName("exportCollection")
  class ExportCollection {

    @Test
    @DisplayName("exported markdown contains collection title and all recipe titles")
    void containsCollectionAndRecipeTitles() {
      var cookbook =
          CookbookImpl.builder()
              .title("Test Cookbook")
              .author("Test Author")
              .recipes(
                  List.of(
                      new Recipe(
                          "r1", "Chocolate Cake", null, emptyList(), emptyList(), emptyList()),
                      new Recipe(
                          "r2", "Vanilla Cookies", null, emptyList(), emptyList(), emptyList())))
              .build();

      String markdown = exporter.exportCollection(cookbook);

      assertTrue(markdown.contains("Test Cookbook"));
      assertTrue(markdown.contains("Chocolate Cake"));
      assertTrue(markdown.contains("Vanilla Cookies"));
    }

    @Test
    @DisplayName("cookbook includes author metadata line when present")
    void cookbookAuthorLine() {
      Recipe r = new Recipe("r1", "Cake", null, emptyList(), emptyList(), emptyList());
      var cookbook =
          CookbookImpl.builder().title("Joy").author("Julia Child").recipes(listOf(r)).build();

      String md = exporter.exportCollection(cookbook);

      assertTrue(md.startsWith("## Joy\n\n_By: Julia Child_\n\n---\n\n# Cake\n\n"));
      // Only final recipe includes footer
      assertTrue(
          md.endsWith(
              "_Exported from CookYourBooks, learn more at https://www.cookyourbooks.app_"));
    }

    @Test
    @DisplayName("personal collection includes description metadata when present")
    void personalDescriptionLine() {
      Recipe r = new Recipe("r1", "Cake", null, emptyList(), emptyList(), emptyList());
      var pc =
          PersonalCollectionImpl.builder()
              .title("Family")
              .description("Handed down")
              .recipes(listOf(r))
              .build();

      String md = exporter.exportCollection(pc);
      assertTrue(md.contains("## Family\n\n_Handed down_\n\n---\n\n"));
    }

    @Test
    @DisplayName("web collection always includes source metadata line")
    void webSourceLine() {
      Recipe r = new Recipe("r1", "Cake", null, emptyList(), emptyList(), emptyList());
      var wc =
          WebCollectionImpl.builder()
              .title("Web")
              .sourceUrl(Objects.requireNonNull(java.net.URI.create("https://example.com")))
              .recipes(listOf(r))
              .build();

      String md = exporter.exportCollection(wc);
      assertTrue(md.contains("_Source: https://example.com_\n\n"));
    }

    @Test
    @DisplayName(
        "collection with no recipes includes only header/metadata and no separators/footer")
    void noRecipesNoSeparators() {
      var cookbook = CookbookImpl.builder().title("Empty").author("A").build();
      String md = exporter.exportCollection(cookbook);

      assertTrue(md.contains("## Empty\n\n_By: A_"));
      assertTrue(!md.contains("---\n\n#"));
      assertTrue(!md.contains("learn more at https://www.cookyourbooks.app"));
    }

    @Test
    @DisplayName("multiple recipes: only final recipe has footer")
    void multipleRecipesFooterOnlyOnce() {
      Recipe r1 = new Recipe("r1", "One", null, emptyList(), emptyList(), emptyList());
      Recipe r2 = new Recipe("r2", "Two", null, emptyList(), emptyList(), emptyList());
      var cookbook = CookbookImpl.builder().title("C").recipes(listOf(r1, r2)).build();

      String md = exporter.exportCollection(cookbook);

      int footerCount =
          md.split("_Exported from CookYourBooks, learn more at https://www.cookyourbooks.app_", -1)
                  .length
              - 1;
      assertEquals(1, footerCount);
    }
  }

  @Nested
  @DisplayName("exportToFile")
  class ExportToFile {

    @Test
    @DisplayName("writes recipe markdown to file")
    void writesRecipeToFile() throws Exception {
      Recipe recipe = new Recipe("id", "Simple", null, emptyList(), emptyList(), emptyList());
      Path file = Objects.requireNonNull(tempDir).resolve("recipe.md");

      exporter.exportToFile(recipe, Objects.requireNonNull(file));

      assertTrue(Files.exists(file));
      assertTrue(Files.readString(file).startsWith("# Simple\n"));
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
}
