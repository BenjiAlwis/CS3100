package exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for Part1Exercises
 *
 * <p>These tests verify that you've correctly filled in the answers in Part1Exercises.java.
 *
 * <p><strong>Exercise 4.5 Instructions:</strong> You must write the first two tests yourself! After
 * writing each test:
 *
 * <ol>
 *   <li>Run {@code ./gradlew test} — your test should PASS
 *   <li>Change your answer in Part1Exercises.java to something wrong
 *   <li>Run {@code ./gradlew test} again — your test should now FAIL
 *   <li>Change your answer back to the correct value
 * </ol>
 *
 * <p>If your test didn't fail when the answer was wrong, your test isn't checking what you think
 * it's checking. Fix it!
 */
class Part1ExercisesTest {

  // ==================== Exercise 4.5: YOU MUST WRITE THESE TESTS ====================
  // These two tests are incomplete. You need to write proper assertions.
  // See the README for instructions on how to verify your tests work correctly.

  @Test
  @DisplayName("Q1: Exception type is correctly identified")
  void testExceptionTypeIsCorrect() {
    // TODO: Write a test that verifies Q1_EXCEPTION_TYPE has the correct value
    // Hint: Use assertEquals(expected, Part1Exercises.Q1_EXCEPTION_TYPE)
    // The correct value can be found in the Assignment 1 specification

  }

  @Test
  @DisplayName("Q2: DECIMAL_PRECISION has the correct value")
  void testDecimalPrecisionIsCorrect() {
    // TODO: Write a test that verifies Q2_DECIMAL_PRECISION has the correct value
    // Hint: Use assertEquals(expected, Part1Exercises.Q2_DECIMAL_PRECISION)
    // The correct value can be found in the Assignment 1 specification

  }

  // ==================== Pre-written Tests (Do Not Modify) ====================

  @Test
  @DisplayName("Q3: Fractional test points value is filled in")
  void testFractionalTestPointsFilled() {
    assertNotEquals(
        0, Part1Exercises.Q3_FRACTIONAL_TEST_POINTS, "Fill in Q3_FRACTIONAL_TEST_POINTS");
  }

  @Test
  @DisplayName("Q4: Null annotation is filled in")
  void testNullAnnotationFilled() {
    assertTrue(!Part1Exercises.Q4_NULL_ANNOTATION.isEmpty(), "Fill in Q4_NULL_ANNOTATION");
  }

  @Test
  @DisplayName("Q5: toString format is filled in")
  void testToStringFormatFilled() {
    assertTrue(!Part1Exercises.Q5_TOSTRING_FORMAT.isEmpty(), "Fill in Q5_TOSTRING_FORMAT");
  }

  @Test
  @DisplayName("Q6: Expected output from instructor test is correct")
  void testExpectedOutputCorrect() {
    assertEquals(
        "2.500 cups",
        Part1Exercises.Q6_EXPECTED_OUTPUT,
        "Check SampleInstructorTest for the expected output");
  }

  @Test
  @DisplayName("Q7: Assertion method is correct")
  void testAssertionMethodCorrect() {
    assertEquals(
        "assertThrows",
        Part1Exercises.Q7_ASSERTION_METHOD,
        "Check SampleInstructorTest for the assertion method used to verify exceptions");
  }

  @Test
  @DisplayName("Q8: Max submissions value is filled in")
  void testMaxSubmissionsFilled() {
    assertNotEquals(0, Part1Exercises.Q8_MAX_SUBMISSIONS, "Fill in Q8_MAX_SUBMISSIONS");
  }

  @Test
  @DisplayName("Q9: Hints available answer should be considered")
  void testHintsAvailableConsidered() {
    assertTrue(Part1Exercises.Q9_HINTS_AVAILABLE, "Make sure you've considered Q9_HINTS_AVAILABLE");
  }
}
