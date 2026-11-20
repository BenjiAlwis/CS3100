package exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for Part4Exercises
 *
 * <p>These tests verify that you've correctly filled in the answers about Gradle.
 */
class Part4ExercisesTest {

  @Test
  @DisplayName("Q1: Build command is correct")
  void testBuildCommandCorrect() {
    assertEquals(
        "./gradlew build", Part4Exercises.Q1_BUILD_COMMAND, "Command to compile and run tests");
  }

  @Test
  @DisplayName("Q2: Clean command is correct")
  void testCleanCommandCorrect() {
    assertEquals(
        "./gradlew clean", Part4Exercises.Q2_CLEAN_COMMAND, "Command to remove build outputs");
  }

  @Test
  @DisplayName("Q3: Why wrapper explanation is filled in")
  void testWhyWrapperFilled() {
    String answer = Part4Exercises.Q3_WHY_WRAPPER.toLowerCase(Locale.ROOT);
    assertTrue(
        answer.contains("consist") || answer.contains("version") || answer.contains("same"),
        "Should mention consistency or version management");
  }

  @Test
  @DisplayName("Q4: Error introduced description is filled in")
  void testErrorIntroducedFilled() {
    // This is student-specific, just check it's not empty
    assertFalse(Part4Exercises.Q4_ERROR_INTRODUCED.isEmpty(), "Fill in Q4_ERROR_INTRODUCED");
  }

  @Test
  @DisplayName("Q5: Error message is filled in")
  void testErrorMessageFilled() {
    // This is student-specific, just check it's not empty
    assertFalse(Part4Exercises.Q5_ERROR_MESSAGE.isEmpty(), "Fill in Q5_ERROR_MESSAGE");
  }

  @Test
  @DisplayName("Q6: Test report path is correct")
  void testTestReportPathCorrect() {
    String answer = Part4Exercises.Q6_TEST_REPORT_PATH.toLowerCase(Locale.ROOT);
    assertTrue(
        answer.contains("build") && answer.contains("reports") && answer.contains("test"),
        "Path should include build/reports and test");
  }

  @Test
  @DisplayName("Q7: Test only command is correct")
  void testTestOnlyCommandCorrect() {
    assertEquals(
        "./gradlew test", Part4Exercises.Q7_TEST_ONLY_COMMAND, "Command to run only tests");
  }

  @Test
  @DisplayName("Q8: Format command is correct")
  void testFormatCommandCorrect() {
    assertEquals(
        "./gradlew spotlessApply",
        Part4Exercises.Q8_FORMAT_COMMAND,
        "Command to auto-fix formatting");
  }

  @Test
  @DisplayName("Q9: NullAway prevents exception is correct")
  void testNullAwayPreventsCorrect() {
    assertEquals(
        "NullPointerException",
        Part4Exercises.Q9_NULLAWAY_PREVENTS,
        "NullAway helps prevent NullPointerException");
  }
}
