package exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for Part3Exercises
 *
 * <p>These tests verify that you've correctly filled in the answers about VS Code.
 */
class Part3ExercisesTest {

  @Test
  @DisplayName("Q1: Open project menu option is correct")
  void testOpenProjectCorrect() {
    String answer = Part3Exercises.Q1_OPEN_PROJECT.toLowerCase(Locale.ROOT);
    assertTrue(
        answer.contains("open folder") || (answer.contains("open") && answer.contains("folder")),
        "Should mention 'Open Folder' menu option");
  }

  @Test
  @DisplayName("Q2: Gradle file is correct")
  void testGradleFileCorrect() {
    assertEquals(
        "build.gradle", Part3Exercises.Q2_GRADLE_FILE, "The Gradle build file in project root");
  }

  @Test
  @DisplayName("Q3: Terminal shortcut is filled in")
  void testTerminalShortcutFilled() {
    String answer = Part3Exercises.Q3_TERMINAL_SHORTCUT.toLowerCase(Locale.ROOT);
    assertTrue(
        answer.contains("`") || answer.contains("backtick"),
        "Terminal shortcut involves the backtick key");
  }

  @Test
  @DisplayName("Q4: Recommended shell is correct")
  void testRecommendedShellCorrect() {
    String answer = Part3Exercises.Q4_RECOMMENDED_SHELL.toLowerCase(Locale.ROOT);
    assertTrue(
        answer.contains("git bash") || answer.contains("bash"),
        "Windows users should use Git Bash");
  }

  @Test
  @DisplayName("Q5: Source control shortcut is filled in")
  void testSourceControlShortcutFilled() {
    String answer = Part3Exercises.Q5_SOURCE_CONTROL_SHORTCUT.toLowerCase(Locale.ROOT);
    assertTrue(
        answer.contains("shift") && answer.contains("g"),
        "Source control shortcut is Ctrl/Cmd+Shift+G");
  }

  @Test
  @DisplayName("Q6: Branch location is correct")
  void testBranchLocationCorrect() {
    String answer = Part3Exercises.Q6_BRANCH_LOCATION.toLowerCase(Locale.ROOT);
    assertTrue(
        answer.contains("bottom") || answer.contains("left") || answer.contains("status"),
        "Branch is shown in the bottom-left status bar");
  }
}
