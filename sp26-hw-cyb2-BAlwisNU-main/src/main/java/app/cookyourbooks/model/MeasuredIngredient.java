package app.cookyourbooks.model;

import java.util.Locale;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * Represents an ingredient with a precise measurement.
 *
 * <p>MeasuredIngredients have a quantity (e.g., "2 cups", "100 grams") in addition to the base
 * ingredient properties (name, preparation, notes). They can be scaled and converted to different
 * units.
 *
 * <p>Examples:
 *
 * <ul>
 *   <li>"2 cups flour, sifted"
 *   <li>"100 grams butter, melted"
 *   <li>"1/2 teaspoon vanilla extract"
 * </ul>
 */
public class MeasuredIngredient extends Ingredient {
  private final Quantity quantity;

  /**
   * Constructs a measured ingredient with the given name, quantity, preparation, and notes.
   *
   * @param name the name of the ingredient (must not be null or blank)
   * @param quantity the quantity of the ingredient (must not be null)
   * @param preparation optional preparation instructions (e.g., "diced", "melted"), may be null
   * @param notes optional notes about the ingredient, may be null
   * @throws IllegalArgumentException if name is blank
   */
  public MeasuredIngredient(
      String name, Quantity quantity, @Nullable String preparation, @Nullable String notes) {
    super(name, preparation, notes);
    this.quantity = quantity;
  }

  /**
   * Returns the quantity of this ingredient.
   *
   * @return the quantity (never null)
   */
  public Quantity getQuantity() {
    return quantity;
  }

  /**
   * Returns a formatted string that includes quantity, name, and optionally preparation.
   *
   * <p>Format:
   *
   * <ul>
   *   <li>If preparation is null or empty: "{quantity.toString()} {name}"
   *   <li>If preparation is non-null and non-empty: "{quantity.toString()} {name}, {preparation}"
   * </ul>
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>"2.5 cups flour, sifted"
   *   <li>"1/2 cup sugar"
   *   <li>"100 g butter"
   *   <li>"3 whole eggs, room temperature"
   * </ul>
   *
   * @return a formatted string representation
   */
  @Override
  public String toString() {
    String prep = getPreparation();
    if (prep != null && !prep.isEmpty()) {
      return quantity.toString() + " " + getName() + ", " + prep;
    } else {
      return quantity.toString() + " " + getName();
    }
  }

  /**
   * Compares this measured ingredient with the specified object for equality.
   *
   * <p>Two MeasuredIngredient objects are equal if they have the same name (case-insensitive),
   * quantity, preparation, and notes.
   *
   * @param o the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MeasuredIngredient that)) {
      return false;
    }
    return this.getName().equalsIgnoreCase(that.getName())
        && Objects.equals(this.quantity, that.quantity)
        && Objects.equals(this.getPreparation(), that.getPreparation())
        && Objects.equals(this.getNotes(), that.getNotes());
  }

  /**
   * Returns a hash code value for this measured ingredient.
   *
   * @return a hash code value
   */
  @Override
  public int hashCode() {
    return Objects.hash(getName().toLowerCase(Locale.ROOT), quantity, getPreparation(), getNotes());
  }
}
