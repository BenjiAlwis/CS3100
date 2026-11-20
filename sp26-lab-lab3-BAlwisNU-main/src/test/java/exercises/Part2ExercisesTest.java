package exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for Part2Exercises
 *
 * <p>These tests verify that you've correctly filled in the answers about terminal commands and
 * git.
 */
class Part2ExercisesTest {

  @Test
  @DisplayName("Q1: PWD command is correct")
  void testPwdCommandCorrect() {
    assertEquals("pwd", Part2Exercises.Q1_PWD_COMMAND, "The command to print working directory");
  }

  @Test
  @DisplayName("Q2: Git folder meaning is filled in")
  void testGitFolderMeaningFilled() {
    // Accept any reasonable answer that mentions git repository
    String answer = Part2Exercises.Q2_GIT_FOLDER_MEANING.toLowerCase(Locale.ROOT);
    assertTrue(
        answer.contains("git") || answer.contains("repo"),
        "Your answer should mention that .git indicates a git repository");
  }

  @Test
  @DisplayName("Q3: Navigate up two levels command is correct")
  void testUpTwoLevelsCorrect() {
    String answer = Part2Exercises.Q3_UP_TWO_LEVELS;
    assertTrue(
        answer.equals("cd ../..") || answer.equals("cd ../../"),
        "Command to navigate up two directories (cd ../.. or cd ../../)");
  }

  @Test
  @DisplayName("Q4: Fetch command is correct")
  void testFetchCommandCorrect() {
    assertEquals(
        "git fetch",
        Part2Exercises.Q4_FETCH_COMMAND,
        "Command to download changes without merging");
  }

  @Test
  @DisplayName("Q5: Fix rejection commands are correct")
  void testFixRejectionCorrect() {
    String answer = Part2Exercises.Q5_FIX_REJECTION.toLowerCase(Locale.ROOT);
    assertTrue(
        answer.contains("git pull") && answer.contains("git push"),
        "Answer should include both 'git pull' and 'git push'");
  }

  @Test
  @DisplayName("Q6: Java version is correct")
  void testJavaVersionCorrect() {
    assertEquals(21, Part2Exercises.Q6_JAVA_VERSION, "This course uses Java 21");
  }

  @Test
  @DisplayName("Q7: Java environment variable is correct")
  void testJavaEnvVarCorrect() {
    assertEquals(
        "JAVA_HOME",
        Part2Exercises.Q7_JAVA_ENV_VAR,
        "Environment variable pointing to Java installation");
  }
}
