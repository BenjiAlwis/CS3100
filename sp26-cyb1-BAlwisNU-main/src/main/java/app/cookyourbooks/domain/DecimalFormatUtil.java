package app.cookyourbooks.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.jspecify.annotations.NonNull;

/**
 * Utility class for formatting decimal values for quantity display.
 *
 * <p>This class centralizes all numeric formatting logic so that quantities (exact and ranged) are
 * displayed consistently and according to the assignment specification.
 *
 * <p>Formatting rules:
 *
 * <ul>
 *   <li>Values are rounded to a fixed number of decimal places using {@link RoundingMode#HALF_UP}
 *   <li>Trailing zeros are removed
 *   <li>Normal numbers are displayed in plain decimal form (e.g., {@code 100}, not {@code 1E+2})
 *   <li>Values that would normally be written in scientific notation preserve that style, but
 *       without a {@code +} sign in the exponent (e.g., {@code 1.23E308})
 * </ul>
 */
final class DecimalFormatUtil {

  /** Prevents instantiation. */
  private DecimalFormatUtil() {}

  /**
   * Formats a numeric value for display using the specified decimal precision.
   *
   * <p>The formatting behavior matches Java's {@link Double#toString(double)} style for large and
   * small values while applying rounding and trimming rules required by the CookYourBooks
   * specification.
   *
   * @param value the value to format
   * @param precision the maximum number of decimal places to retain
   * @return the formatted string (never null)
   */
  static @NonNull String format(double value, int precision) {
    // Round and trim using BigDecimal for correctness of normal formatting.
    BigDecimal bd = BigDecimal.valueOf(value).setScale(precision, RoundingMode.HALF_UP);
    bd = bd.stripTrailingZeros();

    // Determine whether the original value is normally represented in scientific notation.
    String doubleStyle = Double.toString(value);
    boolean wantsScientific = doubleStyle.indexOf('E') >= 0 || doubleStyle.indexOf('e') >= 0;

    if (!wantsScientific) {
      // Normal numbers: plain output like "100" rather than "1E+2".
      return bd.toPlainString();
    }

    // Scientific numbers: preserve Double formatting style.
    // Convert back to double to obtain the correct exponent format.
    String sci = Double.toString(bd.doubleValue());

    // Normalize exponent format by removing '+'.
    sci = sci.replace("E+", "E").replace("e+", "e");
    return sci;
  }
}
