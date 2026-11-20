package exercises;

/**
 * Part 2: Terminal Mastery
 *
 * <p>Fill in the answers to these questions based on terminal commands and git concepts.
 */
public final class Part2Exercises {

  /** Private constructor to prevent instantiation. */
  private Part2Exercises() {
    // intentionally empty
  }

  // ==================== Exercise 2.1: Navigation Challenge ====================

  /** Question 1: What command shows your current directory? */
  public static final String Q1_PWD_COMMAND = "pwd"; // Fill this in

  /** Question 2: What does the .git folder indicate about a directory? */
  public static final String Q2_GIT_FOLDER_MEANING = "It indicates the directory is the root of a Git repository"; // Fill this in

  /** Question 3: What command navigates up two directory levels? */
  public static final String Q3_UP_TWO_LEVELS = "cd ../.."; // Fill this in

  // ==================== Exercise 2.3: Dealing with Git Rejections ====================

  /** Question 4: What command downloads changes from GitHub without merging? */
  public static final String Q4_FETCH_COMMAND = "git fetch"; // Fill this in

  /** Question 5: What TWO commands can fix a rejected push? (separate with comma) */
  public static final String Q5_FIX_REJECTION = "git pull, git push"; // Fill this in

  // ==================== Exercise 2.4: Finding Your Java Version ====================

  /** Question 6: What Java version is this course using? (Just the major version number) */
  public static final int Q6_JAVA_VERSION = 21; // Fill this in

  /** Question 7: What environment variable points to your Java installation? */
  public static final String Q7_JAVA_ENV_VAR = "JAVA_HOME"; // Fill this in
}
