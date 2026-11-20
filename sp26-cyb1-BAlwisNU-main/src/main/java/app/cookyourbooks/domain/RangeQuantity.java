package app.cookyourbooks.domain;

import java.util.Objects;

import org.jspecify.annotations.NonNull;

/**
 * A {@link Quantity} that represents a range of values.
 *
 * <p>Examples include "2-3 cups" and "100-150 grams".
 */
public final class RangeQuantity extends Quantity {

  /** Maximum number of decimal places to display when formatting values. */
  public static final int DECIMAL_PRECISION = 3;

  private final double min;
  private final double max;

  /**
   * Constructs a range quantity with the given minimum and maximum values.
   *
   * <p>Preconditions:
   *
   * <ul>
   *   <li>{@code min} must be strictly positive (> 0.0)
   *   <li>{@code max} must be strictly greater than {@code min}
   *   <li>{@code unit} must be non-null (enforced by NullAway)
   * </ul>
   *
   * @param min the minimum amount (> 0.0)
   * @param max the maximum amount (> min)
   * @param unit the unit of measurement (non-null)
   * @throws IllegalArgumentException if any precondition is violated
   */
  public RangeQuantity(double min, double max, @NonNull Unit unit) {
    super(unit);
    if (min <= 0.0) {
      throw new IllegalArgumentException("min must be > 0.0");
    }
    if (max <= min) {
      throw new IllegalArgumentException("max must be > min");
    }
    this.min = min;
    this.max = max;
  }

  /**
   * Returns the minimum value of this range.
   *
   * @return the minimum value (> 0.0)
   */
  public double getMin() {
    return min;
  }

  /**
   * Returns the maximum value of this range.
   *
   * @return the maximum value (> min)
   */
  public double getMax() {
    return max;
  }

  /**
   * Returns the midpoint of this range.
   *
   * @return {@code (min + max) / 2.0}
   */
  @Override
  public double toDecimal() {
    return (min + max) / 2.0;
  }

  /**
   * Returns a human-readable string representation of this range.
   *
   * <p>Formatting rules:
   *
   * <ul>
   *   <li>Both {@code min} and {@code max} are formatted with at most {@link #DECIMAL_PRECISION}
   *       decimal places
   *   <li>The plural form of the unit is always used
   *   <li>Format: "{min}-{max} {unitPlural}"
   * </ul>
   *
   * @return the formatted string representation
   */
  @Override
  public @NonNull String toString() {
    String minStr = DecimalFormatUtil.format(min, DECIMAL_PRECISION);
    String maxStr = DecimalFormatUtil.format(max, DECIMAL_PRECISION);
    return minStr + "-" + maxStr + " " + getUnit().getPluralAbbreviation();
  }

  /**
   * Compares this range to another object for equality.
   *
   * <p>Two range quantities are equal if they have the same minimum, maximum, and unit.
   *
   * @param o the object to compare
   * @return {@code true} if the other object is an equal range quantity
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RangeQuantity that)) return false;
    return Double.compare(that.min, min) == 0
        && Double.compare(that.max, max) == 0
        && getUnit() == that.getUnit();
  }

  /**
   * Computes a hash code consistent with {@link #equals(Object)}.
   *
   * @return the hash code for this range quantity
   */
  @Override
  public int hashCode() {
    return Objects.hash(min, max, getUnit());
  }
}
