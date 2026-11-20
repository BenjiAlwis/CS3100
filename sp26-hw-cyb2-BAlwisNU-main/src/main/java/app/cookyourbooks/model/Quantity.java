package app.cookyourbooks.model;

import java.util.Locale;
import java.util.Objects;

/**
 * An abstract base class representing a quantity with an associated unit.
 *
 * <p>Quantities can be expressed in different ways (exact decimal values, fractions, or ranges),
 * but all share the common property of having a unit and the ability to be converted to a decimal
 * representation.
 */
public abstract class Quantity {
  private final Unit unit;

  protected static final int DECIMAL_PRECISION = 3;

  /**
   * Formats a double value as a string, removing trailing zeros and unnecessary decimal points.
   * Limits precision to DECIMAL_PRECISION decimal places.
   *
   * @param value the value to format
   * @return the formatted string (e.g., "2" for 2.0, "2.5" for 2.5, "0.33" for 0.333)
   */
  protected static String formatDecimal(double value) {
    // For very large numbers, use Java's default toString (scientific notation)
    // This matches what happens when concatenating Double.MAX_VALUE as a string
    if (Math.abs(value) >= 1e10 || Double.isInfinite(value) || Double.isNaN(value)) {
      return Objects.requireNonNull(Double.toString(value));
    }

    // For normal numbers, format with DECIMAL_PRECISION, then remove trailing zeros
    String formatted = String.format(Locale.US, "%." + DECIMAL_PRECISION + "f", value);

    // Remove trailing zeros and decimal point if not needed
    if (formatted.contains(".")) {
      formatted = formatted.replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    return Objects.requireNonNull(formatted);
  }

  /**
   * Constructs a quantity with the given unit.
   *
   * @param unit the unit of measurement (must not be null)
   */
  protected Quantity(Unit unit) {
    this.unit = unit;
  }

  /**
   * Returns the unit of measurement.
   *
   * @return the unit (never null)
   */
  public Unit getUnit() {
    return unit;
  }

  /**
   * Returns the numeric value as a single decimal for display/calculation purposes.
   *
   * @return the decimal representation of this quantity
   */
  public abstract double toDecimal();

  /**
   * Returns a human-readable string representation of this quantity.
   *
   * @return a formatted string representation
   */
  @Override
  public abstract String toString();
}
