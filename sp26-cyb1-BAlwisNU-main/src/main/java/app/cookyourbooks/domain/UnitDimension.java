package app.cookyourbooks.domain;

/**
 * Represents the physical dimension of a {@link Unit}.
 *
 * <p>The dimension describes what a unit measures, independent of the system (imperial, metric, or
 * house).
 */
public enum UnitDimension {

  /** Units that measure mass (e.g., grams, ounces). */
  WEIGHT,

  /** Units that measure volume (e.g., liters, cups). */
  VOLUME,

  /** Units that count discrete items (e.g., whole). */
  COUNT,

  /** Units that do not correspond to a physical quantity (e.g., pinch, dash). */
  OTHER
}
