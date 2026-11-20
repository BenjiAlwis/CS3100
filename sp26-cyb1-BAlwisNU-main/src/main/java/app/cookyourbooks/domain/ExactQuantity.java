package app.cookyourbooks.domain;

import java.util.Objects;

import org.jspecify.annotations.NonNull;

/**
 * A {@link Quantity} that represents a single precise decimal amount.
 *
 * <p>Examples include "2.5 cups" and "100 grams".
 */
public final class ExactQuantity extends Quantity {

  /** Maximum number of decimal places to display when formatting quantities. */
  public static final int DECIMAL_PRECISION = 3;

  private final double amount;

  /**
   * Constructs an exact quantity with the given amount and unit.
   *
   * <p>Preconditions:
   *
   * <ul>
   *   <li>{@code amount} must be strictly positive (> 0.0)
   *   <li>{@code unit} must be non-null (enforced by NullAway)
   * </ul>
   *
   * @param amount the numeric amount (must be > 0.0)
   * @param unit the unit of measurement (non-null)
   * @throws IllegalArgumentException if {@code amount} is not positive
   */
  public ExactQuantity(double amount, @NonNull Unit unit) {
    super(unit);
    if (amount <= 0.0) {
      throw new IllegalArgumentException("amount must be > 0.0");
    }
    this.amount = amount;
  }

  /**
   * Returns the numeric amount of this quantity.
   *
   * @return the amount (always > 0.0)
   */
  public double getAmount() {
    return amount;
  }

  /**
   * Returns the decimal value of this quantity.
   *
   * @return the amount as a decimal
   */
  @Override
  public double toDecimal() {
    return amount;
  }

  /**
   * Returns a human-readable string representation of this quantity.
   *
   * <p>Formatting rules:
   *
   * <ul>
   *   <li>The amount is formatted with at most {@link #DECIMAL_PRECISION} decimal places
   *   <li>Singular unit is used when {@code amount == 1.0}
   *   <li>Plural unit is used otherwise
   * </ul>
   *
   * @return the formatted string representation
   */
  @Override
  public @NonNull String toString() {
    String formatted = DecimalFormatUtil.format(amount, DECIMAL_PRECISION);
    if (amount == 1.0) {
      return formatted + " " + getUnit().getAbbreviation();
    }
    return formatted + " " + getUnit().getPluralAbbreviation();
  }

  /**
   * Compares this quantity to another object for equality.
   *
   * <p>Two exact quantities are equal if they have the same amount and the same unit.
   *
   * @param o the object to compare
   * @return {@code true} if the other object is an equal exact quantity
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ExactQuantity that)) return false;
    return Double.compare(that.amount, amount) == 0 && getUnit() == that.getUnit();
  }

  /**
   * Computes a hash code consistent with {@link #equals(Object)}.
   *
   * @return the hash code for this quantity
   */
  @Override
  public int hashCode() {
    return Objects.hash(amount, getUnit());
  }
}
