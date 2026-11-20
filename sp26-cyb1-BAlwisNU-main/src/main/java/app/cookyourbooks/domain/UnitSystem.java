package app.cookyourbooks.domain;

/**
 * Represents the measurement system used by a {@link Unit}.
 *
 * <p>Each unit belongs to exactly one measurement system, which groups related units together.
 */
public enum UnitSystem {

  /** Imperial system (e.g., cups, ounces, pounds). */
  IMPERIAL,

  /** Metric system (e.g., grams, liters). */
  METRIC,

  /** Informal household units (e.g., pinch, dash, whole). */
  HOUSE
}
