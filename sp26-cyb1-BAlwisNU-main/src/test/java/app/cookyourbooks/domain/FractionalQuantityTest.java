package app.cookyourbooks.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Comprehensive tests for FractionalQuantity. */
class FractionalQuantityTest {

  private static final double DELTA = 1e-9;

  /** Invalid constructor inputs that violate preconditions. */
  static Stream<Arguments> invalidConstructorArgs() {
    return Stream.of(
        Arguments.of(-1, 0, 2, Unit.CUP, "whole < 0"),
        Arguments.of(0, -1, 2, Unit.CUP, "numerator < 0"),
        Arguments.of(0, 1, 0, Unit.CUP, "denominator == 0"),
        Arguments.of(0, 1, -3, Unit.CUP, "denominator < 0"),
        Arguments.of(0, 0, 3, Unit.CUP, "whole == 0 and numerator == 0"));
  }

  @ParameterizedTest(name = "{4}")
  @MethodSource("invalidConstructorArgs")
  void constructorRejectsInvalidParts(
      int whole, int numerator, int denominator, Unit unit, String label) {
    assertThrows(
        IllegalArgumentException.class,
        () -> new FractionalQuantity(whole, numerator, denominator, unit));
  }

  /** Valid constructor inputs should not throw; also confirms no reduction required. */
  static Stream<Arguments> validConstructorArgs() {
    return Stream.of(
        Arguments.of(0, 1, 2, Unit.CUP),
        Arguments.of(2, 1, 3, Unit.CUP),
        // Not reduced to lowest terms is allowed (e.g., 2/4)
        Arguments.of(0, 2, 4, Unit.CUP),
        Arguments.of(1, 0, 7, Unit.CUP));
  }

  @ParameterizedTest
  @MethodSource("validConstructorArgs")
  void constructorAcceptsValidParts(int whole, int numerator, int denominator, Unit unit) {
    // If construction succeeds, preconditions are satisfied.
    new FractionalQuantity(whole, numerator, denominator, unit);
  }

  /** Decimal conversion cases. */
  static Stream<Arguments> toDecimalCases() {
    return Stream.of(
        Arguments.of(2, 1, 3, Unit.CUP, 2.0 + (1.0 / 3.0)),
        Arguments.of(0, 1, 2, Unit.CUP, 0.5),
        Arguments.of(1, 0, 5, Unit.CUP, 1.0));
  }

  @ParameterizedTest
  @MethodSource("toDecimalCases")
  void toDecimalComputesCorrectly(
      int whole, int numerator, int denominator, Unit unit, double expected) {
    FractionalQuantity q = new FractionalQuantity(whole, numerator, denominator, unit);
    assertEquals(expected, q.toDecimal(), DELTA);
  }

  /** toString branch coverage per spec. */
  static Stream<Arguments> toStringCases() {
    return Stream.of(
        // whole > 0 and numerator > 0 => plural
        Arguments.of(2, 1, 3, Unit.CUP, "2 1/3 cups"),

        // whole > 0 and numerator == 0 => singular if whole == 1 else plural
        Arguments.of(1, 0, 7, Unit.CUP, "1 cup"),
        Arguments.of(2, 0, 7, Unit.CUP, "2 cups"),

        // whole == 0 and numerator > 0
        // if numerator == 1 and denominator == 1 => "1 {unit abbrev}"
        Arguments.of(0, 1, 1, Unit.CUP, "1 cup"),
        // otherwise => "{num}/{den} {unit abbrev}" (note: singular abbreviation)
        Arguments.of(0, 1, 2, Unit.CUP, "1/2 cup"),
        Arguments.of(0, 2, 4, Unit.CUP, "2/4 cup"));
  }

  @ParameterizedTest
  @MethodSource("toStringCases")
  void toStringFormatsCorrectly(
      int whole, int numerator, int denominator, Unit unit, String expected) {
    FractionalQuantity q = new FractionalQuantity(whole, numerator, denominator, unit);
    assertEquals(expected, q.toString());
  }
}
