package app.cookyourbooks.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Comprehensive tests for ExactQuantity. */
class ExactQuantityTest {

  private static final double DELTA = 1e-9;

  /** Invalid amounts (<= 0.0) should throw IllegalArgumentException. */
  static Stream<Arguments> invalidAmounts() {
    return Stream.of(Arguments.of(0.0), Arguments.of(-1.0), Arguments.of(-0.0001));
  }

  @ParameterizedTest
  @MethodSource("invalidAmounts")
  void constructorRejectsNonPositiveAmounts(double amount) {
    assertThrows(IllegalArgumentException.class, () -> new ExactQuantity(amount, Unit.CUP));
  }

  @Test
  void constructorAcceptsSmallestPositiveDouble() {
    assertDoesNotThrow(() -> new ExactQuantity(Double.MIN_VALUE, Unit.GRAM));
  }

  /** Test cases for decimal conversion. */
  static Stream<Arguments> toDecimalCases() {
    return Stream.of(
        Arguments.of(1.0, Unit.CUP, 1.0),
        Arguments.of(2.5, Unit.CUP, 2.5),
        Arguments.of(100.0, Unit.GRAM, 100.0));
  }

  @ParameterizedTest
  @MethodSource("toDecimalCases")
  void toDecimalReturnsAmount(double amount, Unit unit, double expected) {
    ExactQuantity q = new ExactQuantity(amount, unit);
    assertEquals(expected, q.toDecimal(), DELTA);
  }

  /** Test cases for toString formatting, including rounding and singular/plural rules. */
  static Stream<Arguments> toStringCases() {
    return Stream.of(
        // Singular/plural decision uses exact comparison (amount == 1.0)
        Arguments.of(1.0, Unit.CUP, "1 cup"),
        Arguments.of(2.0, Unit.CUP, "2 cups"),

        // Trailing zeros trimmed
        Arguments.of(2.50, Unit.CUP, "2.5 cups"),
        Arguments.of(2.000, Unit.CUP, "2 cups"),

        // Units where plural == singular
        Arguments.of(100.0, Unit.GRAM, "100 g"),

        // Precision limit: DECIMAL_PRECISION = 3, rounded HALF_UP
        Arguments.of(1.23456, Unit.CUP, "1.235 cups"),
        Arguments.of(1.23000, Unit.CUP, "1.23 cups"));
  }

  @ParameterizedTest
  @MethodSource("toStringCases")
  void toStringFormatsCorrectly(double amount, Unit unit, String expected) {
    ExactQuantity q = new ExactQuantity(amount, unit);
    assertEquals(expected, q.toString());
  }
}
