package app.cookyourbooks.model;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * Represents a precise decimal quantity.
 *
 * <p>Used for quantities specified as exact decimal values, such as "2.5 cups" or "100 grams".
 */
public class ExactQuantity extends Quantity {
  private final double amount;

  /**
   * Constructs an exact quantity with the given amount and unit.
   *
   * @param amount the amount (must be strictly positive, > 0.0)
   * @param unit the unit of measurement (must not be null)
   * @throws IllegalArgumentException if amount is not positive
   */
  public ExactQuantity(double amount, Unit unit) {
    super(unit);
    if (amount <= 0.0) {
      throw new IllegalArgumentException("amount must be positive");
    }
    this.amount = amount;
  }

  /**
   * Returns the amount as a decimal.
   *
   * @return the amount
   */
  @Override
  public double toDecimal() {
    return amount;
  }

  /**
   * Returns a formatted string representation using appropriate singular/plural form.
   *
   * <p>Format:
   *
   * <ul>
   *   <li>If amount equals 1.0: "{amount} {unit.getAbbreviation()}" (e.g., "1 cup", "1 g")
   *   <li>Otherwise: "{amount} {unit.getPluralAbbreviation()}" (e.g., "2.5 cups", "100 g")
   * </ul>
   *
   * @return a formatted string representation
   */
  @Override
  public String toString() {
    String formattedAmount = formatDecimal(amount);
    if (amount == 1.0) {
      return formattedAmount + " " + getUnit().getAbbreviation();
    } else {
      return formattedAmount + " " + getUnit().getPluralAbbreviation();
    }
  }

  /**
   * Compares this quantity with the specified object for equality.
   *
   * <p>Two ExactQuantity objects are equal if they have the same unit and the same amount (using
   * Double.compare for floating-point comparison).
   *
   * @param o the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ExactQuantity that)) {
      return false;
    }
    return (Double.compare(this.amount, that.amount) == 0) && this.getUnit() == that.getUnit();
  }

  /**
   * Returns a hash code value for this quantity.
   *
   * @return a hash code value
   */
  @Override
  public int hashCode() {
    return Objects.hash(getUnit(), amount);
  }
}
