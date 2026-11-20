package app.cookyourbooks.domain;

import java.util.Objects;

import org.jspecify.annotations.NonNull;

/**
 * A {@link Quantity} represented as a mixed number: a whole part plus a fraction.
 *
 * <p>Examples include "1/2 cup" and "2 1/3 tablespoons". Fractions are not required to be reduced.
 */
public final class FractionalQuantity extends Quantity {

  private final int whole;
  private final int numerator;
  private final int denominator;

  /**
   * Constructs a fractional quantity with the given parts and unit.
   *
   * <p>Preconditions:
   *
   * <ul>
   *   <li>{@code whole} must be non-negative (>= 0)
   *   <li>{@code numerator} must be non-negative (>= 0)
   *   <li>{@code denominator} must be positive (> 0)
   *   <li>At least one of {@code whole} or {@code numerator} must be positive
   *   <li>{@code unit} must be non-null (enforced by NullAway)
   * </ul>
   *
   * @param whole the whole-number part (>= 0)
   * @param numerator the numerator of the fractional part (>= 0)
   * @param denominator the denominator of the fractional part (> 0)
   * @param unit the unit of measurement (non-null)
   * @throws IllegalArgumentException if any precondition is violated
   */
  public FractionalQuantity(int whole, int numerator, int denominator, @NonNull Unit unit) {
    super(unit);

    if (whole < 0) {
      throw new IllegalArgumentException("whole must be >= 0");
    }
    if (numerator < 0) {
      throw new IllegalArgumentException("numerator must be >= 0");
    }
    if (denominator <= 0) {
      throw new IllegalArgumentException("denominator must be > 0");
    }
    if (whole == 0 && numerator == 0) {
      throw new IllegalArgumentException("total quantity must be > 0");
    }

    this.whole = whole;
    this.numerator = numerator;
    this.denominator = denominator;
  }

  /**
   * Returns the whole-number part.
   *
   * @return the whole-number part (>= 0)
   */
  public int getWhole() {
    return whole;
  }

  /**
   * Returns the numerator of the fractional part.
   *
   * @return the numerator (>= 0)
   */
  public int getNumerator() {
    return numerator;
  }

  /**
   * Returns the denominator of the fractional part.
   *
   * @return the denominator (> 0)
   */
  public int getDenominator() {
    return denominator;
  }

  /**
   * Returns the decimal equivalent of this mixed number.
   *
   * @return {@code whole + (numerator / (double) denominator)}
   */
  @Override
  public double toDecimal() {
    return whole + (numerator / (double) denominator);
  }

  /**
   * Returns a human-readable string representation of this quantity.
   *
   * <p>Formatting rules:
   *
   * <ul>
   *   <li>If {@code whole > 0} and {@code numerator > 0}: "{whole} {numerator}/{denominator}
   *       {unitPlural}"
   *   <li>If {@code whole > 0} and {@code numerator == 0}:
   *       <ul>
   *         <li>If {@code whole == 1}: "{whole} {unitSingular}"
   *         <li>Otherwise: "{whole} {unitPlural}"
   *       </ul>
   *   <li>If {@code whole == 0} and {@code numerator > 0}:
   *       <ul>
   *         <li>If {@code numerator == 1 && denominator == 1}: "1 {unitSingular}"
   *         <li>Otherwise: "{numerator}/{denominator} {unitSingular}"
   *       </ul>
   * </ul>
   *
   * @return the formatted string representation
   */
  @Override
  public @NonNull String toString() {
    if (whole > 0 && numerator > 0) {
      return whole + " " + numerator + "/" + denominator + " " + getUnit().getPluralAbbreviation();
    }

    if (whole > 0) { // numerator == 0 here
      if (whole == 1) {
        return whole + " " + getUnit().getAbbreviation();
      }
      return whole + " " + getUnit().getPluralAbbreviation();
    }

    // whole == 0 and numerator > 0
    if (numerator == 1 && denominator == 1) {
      return numerator + " " + getUnit().getAbbreviation();
    }
    return numerator + "/" + denominator + " " + getUnit().getAbbreviation();
  }

  /**
   * Compares this quantity to another object for equality.
   *
   * <p>Two fractional quantities are equal if they have the same whole, numerator, denominator, and
   * unit.
   *
   * @param o the object to compare
   * @return {@code true} if the other object is an equal fractional quantity
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FractionalQuantity that)) return false;
    return whole == that.whole
        && numerator == that.numerator
        && denominator == that.denominator
        && getUnit() == that.getUnit();
  }

  /**
   * Computes a hash code consistent with {@link #equals(Object)}.
   *
   * @return the hash code for this quantity
   */
  @Override
  public int hashCode() {
    return Objects.hash(whole, numerator, denominator, getUnit());
  }
}
