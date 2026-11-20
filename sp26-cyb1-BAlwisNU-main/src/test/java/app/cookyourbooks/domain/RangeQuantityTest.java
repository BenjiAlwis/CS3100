package app.cookyourbooks.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Comprehensive tests for RangeQuantity. */
class RangeQuantityTest {

  private static final double DELTA = 1e-9;

  /** Invalid ranges. */
  static Stream<Arguments> invalidRanges() {
    return Stream.of(
        Arguments.of(0.0, 1.0, Unit.CUP, "min == 0"),
        Arguments.of(-1.0, 1.0, Unit.CUP, "min < 0"),
        Arguments.of(2.0, 2.0, Unit.CUP, "max == min"),
        Arguments.of(3.0, 2.0, Unit.CUP, "max < min"));
  }

  @ParameterizedTest(name = "{3}")
  @MethodSource("invalidRanges")
  void constructorRejectsInvalidRanges(double min, double max, Unit unit, String label) {
    assertThrows(IllegalArgumentException.class, () -> new RangeQuantity(min, max, unit));
  }

  /** Midpoint calculation cases. */
  static Stream<Arguments> midpointCases() {
    return Stream.of(
        Arguments.of(2.0, 4.0, Unit.CUP, 3.0),
        Arguments.of(1.0, 2.0, Unit.CUP, 1.5),
        Arguments.of(100.0, 150.0, Unit.GRAM, 125.0));
  }

  @ParameterizedTest
  @MethodSource("midpointCases")
  void toDecimalReturnsMidpoint(double min, double max, Unit unit, double expected) {
    RangeQuantity q = new RangeQuantity(min, max, unit);
    assertEquals(expected, q.toDecimal(), DELTA);
  }

  /** toString formatting cases including precision trimming/rounding. */
  static Stream<Arguments> toStringCases() {
    return Stream.of(
        Arguments.of(2.0, 3.0, Unit.CUP, "2-3 cups"),
        Arguments.of(100.0, 150.0, Unit.GRAM, "100-150 g"),
        // precision limit: 3 decimals, rounded HALF_UP
        Arguments.of(1.23456, 2.34567, Unit.CUP, "1.235-2.346 cups"),
        // trimming trailing zeros
        Arguments.of(2.5000, 3.0000, Unit.CUP, "2.5-3 cups"));
  }

  @ParameterizedTest
  @MethodSource("toStringCases")
  void toStringFormatsCorrectly(double min, double max, Unit unit, String expected) {
    RangeQuantity q = new RangeQuantity(min, max, unit);
    assertEquals(expected, q.toString());
  }

  @Test
  void rangeQuantity_scientificNotation_hasNoPlusInExponent() {
    RangeQuantity q = new RangeQuantity(1.0, Double.MAX_VALUE, Unit.MILLILITER);
    assertEquals("1-1.7976931348623157E308 ml", q.toString());
  }
}
