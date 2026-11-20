package exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Sample Instructor Tests
 *
 * <p>This file simulates the kind of tests the autograder runs. Study these tests to understand:
 *
 * <ul>
 *   <li>What assertions are used to check expected values
 *   <li>How exceptions are verified
 *   <li>What format outputs are expected
 * </ul>
 *
 * <p>Use this file to answer questions in Part1Exercises.java.
 */
class SampleInstructorTest {

  // ==================== ExactQuantity Tests ====================

  @Test
  @DisplayName("ExactQuantity toString formats correctly for 2.5 cups")
  void testExactQuantityToStringFormatting() {
    // This test demonstrates the expected output format for ExactQuantity
    // When amount is 2.5 and unit is cups, toString should return:
    String expected = "2.500 cups";

    // In a real test, you would create an ExactQuantity and call toString()
    // ExactQuantity quantity = new ExactQuantity(2.5, Unit.CUP);
    // assertEquals(expected, quantity.toString());

    // For this sample, we just verify the expected format
    assertEquals("2.500 cups", expected);
  }

  @Test
  @DisplayName("ExactQuantity constructor throws exception for non-positive amount")
  void testExactQuantityRejectsNonPositiveAmount() {
    // This test demonstrates how we verify exception throwing
    // The assertThrows method is used to verify exceptions are thrown

    // In a real test:
    // assertThrows(IllegalArgumentException.class,
    //     () -> new ExactQuantity(0, Unit.CUP));

    // For this sample, we demonstrate the assertion pattern
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          throw new IllegalArgumentException("Amount must be positive");
        });
  }

  @Test
  @DisplayName("ExactQuantity uses correct decimal precision")
  void testExactQuantityDecimalPrecision() {
    // ExactQuantity should display exactly 3 decimal places
    // This is defined by the DECIMAL_PRECISION constant (value: 3)

    String formatted = String.format("%.3f", 2.5);
    assertEquals("2.500", formatted);
  }

  // ==================== FractionalQuantity Tests ====================

  @Test
  @DisplayName("FractionalQuantity toString for mixed number")
  void testFractionalQuantityMixedNumber() {
    // When whole > 0 AND numerator > 0, format is: "whole numerator/denominator units"
    // For example: 2 1/3 cups

    String expected = "2 1/3 cups";

    // In a real test:
    // FractionalQuantity quantity = new FractionalQuantity(2, 1, 3, Unit.CUP);
    // assertEquals(expected, quantity.toString());

    assertEquals("2 1/3 cups", expected);
  }

  @Test
  @DisplayName("FractionalQuantity toString for fraction only")
  void testFractionalQuantityFractionOnly() {
    // When whole == 0 AND numerator > 0, format is: "numerator/denominator units"
    // For example: 1/2 cup

    String expected = "1/2 cup";
    assertEquals("1/2 cup", expected);
  }

  // ==================== Unit Tests ====================

  @Test
  @DisplayName("Unit provides singular and plural abbreviations")
  void testUnitAbbreviations() {
    // Units have both singular and plural forms
    // CUP -> "cup" (singular), "cups" (plural)

    String singular = "cup";
    String plural = "cups";

    assertEquals("cup", singular);
    assertEquals("cups", plural);
  }
}
