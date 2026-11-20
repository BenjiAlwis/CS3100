package app.cookyourbooks.adapters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import app.cookyourbooks.model.Cookbook;
import app.cookyourbooks.model.PersonalCollection;
import app.cookyourbooks.model.Recipe;
import app.cookyourbooks.model.RecipeCollection;
import app.cookyourbooks.model.WebCollection;
import app.cookyourbooks.repository.RepositoryException;

/**
 * Exports recipes and recipe collections to Markdown format.
 *
 * <p>This exporter produces human-readable Markdown documents suitable for printing, sharing, or
 * importing into other applications.
 *
 * <p><strong>Recipe Format:</strong> The {@link #exportRecipe(Recipe)} method produces output in a
 * specific format documented in the assignment writeup.
 *
 * <p><strong>Collection Format:</strong> The {@link #exportCollection(RecipeCollection)} format is
 * a design decision. At minimum, include the collection title, relevant metadata, and all recipes.
 *
 * @see Recipe
 * @see RecipeCollection
 */
public class MarkdownExporter {

  private static final String FOOTER =
      "_Exported from CookYourBooks, learn more at https://www.cookyourbooks.app_";

  /** Constructs a new MarkdownExporter. */
  public MarkdownExporter() {
    // Stateless exporter - no initialization needed
  }

  /**
   * Exports a recipe to Markdown format.
   *
   * <p>The output format is specified in the assignment writeup. Uses {@code Ingredient.toString()}
   * for ingredient formatting and {@code Instruction.toString()} for instructions.
   *
   * @param recipe the recipe to export
   * @return the recipe as a Markdown string
   */
  public String exportRecipe(Recipe recipe) {
    StringBuilder sb = new StringBuilder();
    sb.append("# ").append(recipe.getTitle()).append("\n\n");

    var servings = recipe.getServings();
    if (servings != null) {
      sb.append("_Serves: ").append(servings.toString()).append("_\n\n");
    }

    sb.append("## Ingredients\n\n");
    for (var ing : recipe.getIngredients()) {
      sb.append("- ").append(ing.toString()).append("\n");
    }
    sb.append("\n");

    sb.append("## Instructions\n\n");
    for (var inst : recipe.getInstructions()) {
      sb.append(inst.toString()).append("\n");
    }
    sb.append("\n---\n\n").append(FOOTER);
    return Objects.requireNonNull(sb.toString());
  }

  /**
   * Exports a recipe collection to Markdown format.
   *
   * <p>The exact format is a design decision. At minimum:
   *
   * <ul>
   *   <li>Collection title as H1
   *   <li>Relevant metadata (author for cookbooks, URL for web sources, etc.)
   *   <li>Each recipe using the recipe format
   *   <li>Recipes separated by {@code ---}
   * </ul>
   *
   * @param collection the collection to export
   * @return the collection as a Markdown string
   */
  public String exportCollection(RecipeCollection collection) {
    StringBuilder sb = new StringBuilder();
    sb.append("## ").append(collection.getTitle()).append("\n\n");

    // Metadata line (optional per type)
    if (collection instanceof Cookbook cb) {
      cb.getAuthor().ifPresent(a -> sb.append("_By: ").append(a).append("_\n\n"));
    } else if (collection instanceof PersonalCollection pc) {
      pc.getDescription().ifPresent(d -> sb.append("_").append(d).append("_\n\n"));
    } else if (collection instanceof WebCollection wc) {
      sb.append("_Source: ").append(wc.getSourceUrl().toString()).append("_\n\n");
    }

    if (collection.getRecipes().isEmpty()) {
      return Objects.requireNonNull(sb.toString());
    }

    for (int i = 0; i < collection.getRecipes().size(); i++) {
      Recipe r = collection.getRecipes().get(i);

      sb.append("---\n\n");
      sb.append("# ").append(r.getTitle()).append("\n\n");

      var recipeServings = r.getServings();
      if (recipeServings != null) {
        sb.append("_Serves: ").append(recipeServings.toString()).append("_\n\n");
      }

      sb.append("## Ingredients\n\n");
      for (var ing : r.getIngredients()) {
        sb.append("- ").append(ing.toString()).append("\n");
      }
      sb.append("\n");

      sb.append("## Instructions\n\n");
      for (var inst : r.getInstructions()) {
        sb.append(inst.toString()).append("\n");
      }
      sb.append("\n");

      if (i == collection.getRecipes().size() - 1) {
        sb.append("---\n\n").append(FOOTER);
      }
    }

    return Objects.requireNonNull(sb.toString());
  }

  /**
   * Exports a recipe to a Markdown file.
   *
   * @param recipe the recipe to export
   * @param file the path to write the Markdown file
   * @throws app.cookyourbooks.repository.RepositoryException if the file cannot be written
   */
  public void exportToFile(Recipe recipe, Path file) {
    try {
      Files.writeString(file, exportRecipe(recipe));
    } catch (IOException e) {
      throw new RepositoryException("Failed to write recipe markdown to " + file, e);
    }
  }

  /**
   * Exports a recipe collection to a Markdown file.
   *
   * @param collection the collection to export
   * @param file the path to write the Markdown file
   * @throws app.cookyourbooks.repository.RepositoryException if the file cannot be written
   */
  public void exportToFile(RecipeCollection collection, Path file) {
    try {
      Files.writeString(file, exportCollection(collection));
    } catch (IOException e) {
      throw new RepositoryException("Failed to write collection markdown to " + file, e);
    }
  }
}
