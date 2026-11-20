package app.cookyourbooks.model;

import org.jspecify.annotations.Nullable;

/**
 * An abstract base class representing an ingredient in a recipe.
 *
 * <p>Ingredients have a name, optional preparation instructions (e.g., "diced", "melted"), and
 * optional notes. The ingredient hierarchy distinguishes between measured ingredients (with precise
 * quantities) and vague ingredients (without precise measurements).
 *
 * <p>Known subclasses:
 *
 * <ul>
 *   <li>{@link MeasuredIngredient} - Ingredients with precise measurements (e.g., "2 cups flour")
 *   <li>{@link VagueIngredient} - Ingredients without precise measurements (e.g., "salt to taste")
 * </ul>
 */
public abstract class Ingredient {
  protected String name;
  protected @Nullable String preparation;
  protected @Nullable String notes;

  /**
   * Constructs an ingredient with the given name, preparation, and notes.
   *
   * @param name the name of the ingredient (must not be null or blank)
   * @param preparation optional preparation instructions (e.g., "diced", "melted"), may be null
   * @param notes optional notes about the ingredient, may be null
   * @throws IllegalArgumentException if name is blank
   */
  protected Ingredient(String name, @Nullable String preparation, @Nullable String notes) {
    if (name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
    this.name = name.trim();
    this.preparation = (preparation != null) ? preparation.trim() : null;
    this.notes = (notes != null) ? notes.trim() : null;
  }

  /**
   * Returns the name of the ingredient.
   *
   * @return the ingredient name (never null or blank)
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the preparation instructions for this ingredient.
   *
   * @return the preparation instructions, or null if none
   */
  public @Nullable String getPreparation() {
    return preparation;
  }

  /**
   * Returns any notes about this ingredient.
   *
   * @return the notes, or null if none
   */
  public @Nullable String getNotes() {
    return notes;
  }

  /**
   * Returns a human-readable string representation of this ingredient.
   *
   * @return a formatted string representation
   */
  @Override
  public abstract String toString();

  /**
   * Compares this ingredient with the specified object for equality.
   *
   * @param o the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public abstract boolean equals(@Nullable Object o);

  /**
   * Returns a hash code value for this ingredient.
   *
   * @return a hash code value
   */
  @Override
  public abstract int hashCode();
}
