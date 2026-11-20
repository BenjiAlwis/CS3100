package app.cookyourbooks.domain;

import java.util.Locale;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * An {@link Ingredient} with a precise {@link Quantity}.
 *
 * <p>Measured ingredients are displayed as "{quantity} {name}" and may optionally include a
 * preparation after a comma.
 */
public final class MeasuredIngredient extends Ingredient {

  private final @NonNull Quantity quantity;

  /**
   * Constructs a measured ingredient with a precise quantity.
   *
   * <p>Preconditions:
   *
   * <ul>
   *   <li>{@code name} must be non-null (enforced by NullAway) and not blank
   *   <li>{@code quantity} must be non-null (enforced by NullAway)
   *   <li>{@code preparation} may be null
   *   <li>{@code notes} may be null
   * </ul>
   *
   * @param name the ingredient name (non-null; must not be blank)
   * @param quantity the quantity for this ingredient (non-null)
   * @param preparation optional preparation text (may be null; trimmed if non-null)
   * @param notes optional notes text (may be null; trimmed if non-null)
   * @throws IllegalArgumentException if {@code name} is blank (null is prevented by NullAway)
   */
  public MeasuredIngredient(
      @NonNull String name,
      @NonNull Quantity quantity,
      @Nullable String preparation,
      @Nullable String notes) {
    super(name, preparation, notes);
    this.quantity = quantity;
  }

  /**
   * Returns the precise quantity for this ingredient.
   *
   * @return the non-null quantity
   */
  public @NonNull Quantity getQuantity() {
    return quantity;
  }

  /**
   * Returns a human-readable representation of this measured ingredient.
   *
   * <p>Formatting rules:
   *
   * <ul>
   *   <li>If preparation is null or empty: "{quantity} {name}"
   *   <li>If preparation is non-empty: "{quantity} {name}, {preparation}"
   * </ul>
   *
   * <p>Notes are not included in the returned string.
   *
   * @return the formatted string representation
   */
  @Override
  public @NonNull String toString() {
    String prep = getPreparation();
    if (prep == null || prep.isEmpty()) {
      return quantity.toString() + " " + getName();
    }
    return quantity.toString() + " " + getName() + ", " + prep;
  }

  /**
   * Compares this ingredient to another object for equality.
   *
   * <p>Equality is based on:
   *
   * <ul>
   *   <li>name (case-insensitive)
   *   <li>quantity
   *   <li>preparation
   *   <li>notes
   * </ul>
   *
   * @param o the object to compare
   * @return {@code true} if the other object is a measured ingredient with the same identity
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MeasuredIngredient that)) return false;
    return getName().equalsIgnoreCase(that.getName())
        && Objects.equals(quantity, that.quantity)
        && Objects.equals(getPreparation(), that.getPreparation())
        && Objects.equals(getNotes(), that.getNotes());
  }

  /**
   * Computes a hash code consistent with {@link #equals(Object)}.
   *
   * @return the hash code for this ingredient
   */
  @Override
  public int hashCode() {
    return Objects.hash(getName().toLowerCase(Locale.ROOT), quantity, getPreparation(), getNotes());
  }
}
