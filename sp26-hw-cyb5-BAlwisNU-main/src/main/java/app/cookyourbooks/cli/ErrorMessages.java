package app.cookyourbooks.cli;

/**
 * Centralized error-message templates for CLI command responses.
 *
 * <p>Messages in this class are intentionally aligned with the assignment specification output.
 */
public final class ErrorMessages {

  /** Prevents instantiation of this utility class. */
  private ErrorMessages() {}

  /**
   * Builds the standard message for a missing recipe title.
   *
   * @param title the recipe title that was not found
   * @return the formatted error message
   */
  public static String recipeNotFound(String title) {
    return "Recipe not found: '" + title + "'. Use 'search' to find recipes by ingredient.";
  }

  /**
   * Builds the standard message for a missing collection title.
   *
   * @param title the collection title that was not found
   * @return the formatted error message
   */
  public static String collectionNotFound(String title) {
    return "Collection not found: '" + title + "'. Use 'collections' to see available collections.";
  }
}
