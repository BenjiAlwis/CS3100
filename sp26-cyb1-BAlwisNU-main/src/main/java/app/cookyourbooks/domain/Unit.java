package app.cookyourbooks.domain;

import org.jspecify.annotations.NonNull;

/**
 * Enumerates all units of measurement supported by the CookYourBooks domain model.
 *
 * <p>Each unit belongs to:
 *
 * <ul>
 *   <li>a {@link UnitSystem} (IMPERIAL, METRIC, or HOUSE)
 *   <li>a {@link UnitDimension} (WEIGHT, VOLUME, COUNT, or OTHER)
 * </ul>
 *
 * <p>Each unit also provides a singular and plural abbreviation for display.
 */
public enum Unit {

  // Imperial (volume)
  CUP(UnitSystem.IMPERIAL, UnitDimension.VOLUME, "cup", "cups"),
  TABLESPOON(UnitSystem.IMPERIAL, UnitDimension.VOLUME, "tbsp", "tbsp"),
  TEASPOON(UnitSystem.IMPERIAL, UnitDimension.VOLUME, "tsp", "tsp"),
  FLUID_OUNCE(UnitSystem.IMPERIAL, UnitDimension.VOLUME, "fl oz", "fl oz"),

  // Imperial (weight)
  OUNCE(UnitSystem.IMPERIAL, UnitDimension.WEIGHT, "oz", "oz"),
  POUND(UnitSystem.IMPERIAL, UnitDimension.WEIGHT, "lb", "lb"),

  // Metric (volume)
  MILLILITER(UnitSystem.METRIC, UnitDimension.VOLUME, "ml", "ml"),
  LITER(UnitSystem.METRIC, UnitDimension.VOLUME, "L", "L"),

  // Metric (weight)
  GRAM(UnitSystem.METRIC, UnitDimension.WEIGHT, "g", "g"),
  KILOGRAM(UnitSystem.METRIC, UnitDimension.WEIGHT, "kg", "kg"),

  // House (other)
  PINCH(UnitSystem.HOUSE, UnitDimension.OTHER, "pinch", "pinches"),
  DASH(UnitSystem.HOUSE, UnitDimension.OTHER, "dash", "dashes"),
  HANDFUL(UnitSystem.HOUSE, UnitDimension.OTHER, "handful", "handfuls"),
  TO_TASTE(UnitSystem.HOUSE, UnitDimension.OTHER, "to taste", "to taste"),

  // House (count)
  WHOLE(UnitSystem.HOUSE, UnitDimension.COUNT, "whole", "whole");

  private final @NonNull UnitSystem system;
  private final @NonNull UnitDimension dimension;
  private final @NonNull String abbreviation;
  private final @NonNull String pluralAbbreviation;

  /**
   * Constructs a unit with the given classification and display forms.
   *
   * @param system the measurement system this unit belongs to
   * @param dimension the physical dimension of this unit
   * @param abbreviation the singular display form (e.g., "cup")
   * @param pluralAbbreviation the plural display form (e.g., "cups")
   */
  Unit(
      @NonNull UnitSystem system,
      @NonNull UnitDimension dimension,
      @NonNull String abbreviation,
      @NonNull String pluralAbbreviation) {
    this.system = system;
    this.dimension = dimension;
    this.abbreviation = abbreviation;
    this.pluralAbbreviation = pluralAbbreviation;
  }

  /**
   * Returns the measurement system this unit belongs to.
   *
   * @return the unit system (never null)
   */
  public @NonNull UnitSystem getSystem() {
    return system;
  }

  /**
   * Returns the physical dimension of this unit.
   *
   * @return the dimension (never null)
   */
  public @NonNull UnitDimension getDimension() {
    return dimension;
  }

  /**
   * Returns the singular form of this unit for display.
   *
   * @return the singular abbreviation (e.g., "cup")
   */
  public @NonNull String getAbbreviation() {
    return abbreviation;
  }

  /**
   * Returns the plural form of this unit for display.
   *
   * @return the plural abbreviation (e.g., "cups")
   */
  public @NonNull String getPluralAbbreviation() {
    return pluralAbbreviation;
  }
}
