package exercises;

/**
 * Part 4: Gradle Demystified
 *
 * <p>Fill in the answers to these questions based on Gradle commands and build concepts.
 */
public final class Part4Exercises {

  /** Private constructor to prevent instantiation. */
  private Part4Exercises() {
    // intentionally empty
  }

  // ==================== Exercise 4.1: Essential Gradle Commands ====================

  /** Question 1: What Gradle command compiles code AND runs tests? */
  public static final String Q1_BUILD_COMMAND = "./gradlew build"; // Fill this in

  /** Question 2: What Gradle command removes all previous build outputs? */
  public static final String Q2_CLEAN_COMMAND = "./gradlew clean"; // Fill this in

  /** Question 3: Why use ./gradlew instead of just gradle? */
  public static final String Q3_WHY_WRAPPER = "This ensures everyone on the team uses the same Gradle version"; // Fill this in (hint: consistency)

  // ==================== Exercise 4.2: Reading Gradle Errors ====================

  /** Question 4: What syntax error did you introduce? */
  public static final String Q4_ERROR_INTRODUCED = "Missing semicolon at the end of a statement"; // e.g., "removed semicolon on line 5"

  /** Question 5: Copy the key part of the error message Gradle showed you */
  public static final String Q5_ERROR_MESSAGE = " ';' expected"; // e.g., "';' expected"

  // ==================== Exercise 4.3: Understanding Test Output ====================

  /** Question 6: Where does Gradle put the HTML test report? */
  public static final String Q6_TEST_REPORT_PATH = "build/reports/tests/test/index.html"; // Fill this in

  /** Question 7: What Gradle command runs ONLY the tests (not other checks)? */
  public static final String Q7_TEST_ONLY_COMMAND = "./gradlew test"; // Fill this in

  // ==================== Exercise 4.6: Code Quality Tools ====================

  /** Question 8: What Gradle command auto-fixes formatting issues? */
  public static final String Q8_FORMAT_COMMAND = "./gradlew spotlessApply"; // Fill this in

  /** Question 9: What common Java exception does NullAway help prevent? */
  public static final String Q9_NULLAWAY_PREVENTS = "NullPointerException"; // Fill this in
}
