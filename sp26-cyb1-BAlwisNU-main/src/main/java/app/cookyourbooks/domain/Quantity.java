package app.cookyourbooks.domain;

import org.jspecify.annotations.NonNull;

/**
 * Abstract base class representing a quantity with an associated {@link Unit}.
 *
 * <p>Concrete subclasses define how the quantity is represented (e.g., exact, fractional, or
 * range-based).
 */
public abstract class Quantity {

  private final @NonNull Unit unit;

  /**
   * Constructs a quantity with the given unit.
   *
   * <p>Preconditions:
   *
   * <ul>
   *   <li>{@code unit} must be non-null (enforced by NullAway)
   * </ul>
   *
   * @param unit the unit of measurement for this quantity
   */
  protected Quantity(@NonNull Unit unit) {
    this.unit = unit;
  }

  /**
   * Returns the unit of measurement for this quantity.
   *
   * @return the non-null unit
   */
  public final @NonNull Unit getUnit() {
    return unit;
  }

  /**
   * Returns the numeric value of this quantity as a decimal.
   *
   * <p>This value is intended for calculations and comparisons.
   *
   * @return the decimal representation of this quantity
   */
  public abstract double toDecimal();

  /**
   * Returns a human-readable string representation of this quantity.
   *
   * @return the formatted string representation
   */
  @Override
  public abstract @NonNull String toString();
}
