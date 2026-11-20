package app.cookyourbooks.conversion;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import app.cookyourbooks.model.ExactQuantity;
import app.cookyourbooks.model.FractionalQuantity;
import app.cookyourbooks.model.Quantity;
import app.cookyourbooks.model.RangeQuantity;
import app.cookyourbooks.model.Unit;

/**
 * A record representing a single conversion rule between units.
 *
 * <p>Conversion rules can be generic (applying to all ingredients) or ingredient-specific (applying
 * only to a particular ingredient). Ingredient-specific rules enable density-based conversions,
 * such as converting cups of flour to grams.
 *
 * @param fromUnit the source unit for this conversion (never null)
 * @param toUnit the target unit for this conversion (never null)
 * @param factor the multiplication factor (e.g., 236.588 for cups→mL, must be positive)
 * @param ingredientName if non-null, this rule only applies to this ingredient (case-insensitive)
 */
public record ConversionRule(
    Unit fromUnit, Unit toUnit, double factor, @Nullable String ingredientName) {

  /**
   * Compact constructor that validates the record components.
   *
   * @throws IllegalArgumentException if factor is not positive
   */
  public ConversionRule {
    Objects.requireNonNull(fromUnit, "fromUnit must not be null");
    Objects.requireNonNull(toUnit, "toUnit must not be null");
    if (!(factor > 0.0) || Double.isNaN(factor)) {
      throw new IllegalArgumentException("factor must be positive");
    }
    if (ingredientName != null && ingredientName.isBlank()) {
      ingredientName = null;
    }
  }

  /**
   * Returns true if this rule can convert from the given unit to the target unit.
   *
   * <p>If this rule has an ingredientName, it also checks that the ingredient matches
   * (case-insensitive). If ingredientName is null (generic rule), it matches any ingredient.
   *
   * @param from the source unit (must not be null)
   * @param to the target unit (must not be null)
   * @param ingredient the ingredient name to check (may be null for generic checks)
   * @return true if this rule can perform the conversion
   */
  public boolean canConvert(Unit from, Unit to, @Nullable String ingredient) {
    Objects.requireNonNull(from, "from must not be null");
    Objects.requireNonNull(to, "to must not be null");

    if (this.fromUnit != from || this.toUnit != to) {
      return false;
    }

    // Generic rule: matches any ingredient context
    if (this.ingredientName == null) {
      return true;
    }

    // Ingredient-specific rule requires an ingredient name
    if (ingredient == null) {
      return false;
    }

    // STRICT matching except for case: no trimming, no normalization
    return this.ingredientName.equalsIgnoreCase(ingredient);
  }

  /**
   * Converts the given quantity using this rule's conversion factor.
   *
   * @param quantity the quantity to convert (must not be null, and its unit must equal fromUnit)
   * @return a new Quantity with converted amount(s) and toUnit
   * @throws IllegalArgumentException if quantity.getUnit() does not equal fromUnit
   */
  public Quantity convert(Quantity quantity) {
    Objects.requireNonNull(quantity, "quantity must not be null");
    if (quantity.getUnit() != this.fromUnit) {
      throw new IllegalArgumentException("quantity unit must match fromUnit");
    }

    if (quantity instanceof RangeQuantity rq) {
      return new RangeQuantity(rq.getMin() * factor, rq.getMax() * factor, toUnit);
    }

    if (quantity instanceof FractionalQuantity fq) {
      return new ExactQuantity(fq.toDecimal() * factor, toUnit);
    }

    return new ExactQuantity(quantity.toDecimal() * factor, toUnit);
  }
}
