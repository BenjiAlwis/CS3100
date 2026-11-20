package exercises;

/**
 * Part 1: Reading Assignments Like a Detective
 *
 * <p>Fill in the answers to these questions by reading the Assignment 1 specification and examining
 * the sample instructor tests.
 */
public final class Part1Exercises {

  /** Private constructor to prevent instantiation. */
  private Part1Exercises() {
    // intentionally empty
  }

  // ==================== Exercise 1.1: Anatomy of an Assignment Specification ====================

  /**
   * Question 1: According to the spec, what exception should ExactQuantity's constructor throw if
   * the amount is not positive?
   */
  public static final String Q1_EXCEPTION_TYPE = "IllegalArgumentException"; // Fill this in

  /**
   * Question 2: What is the DECIMAL_PRECISION constant value required for ExactQuantity and
   * RangeQuantity?
   */
  public static final int Q2_DECIMAL_PRECISION = 3; // Fill this in

  /**
   * Question 3: According to the grading rubric, how many points is FractionalQuantityTest worth?
   */
  public static final int Q3_FRACTIONAL_TEST_POINTS = 12; // Fill this in

  /**
   * Question 4: What annotation does the spec say eliminates the need to test for null parameter
   * exceptions?
   */
  public static final String Q4_NULL_ANNOTATION = "@NonNull"; // Fill this in

  /**
   * Question 5: In the toString() for FractionalQuantity, what format is used when whole > 0 AND
   * numerator > 0? (Copy the exact format string from spec)
   */
  public static final String Q5_TOSTRING_FORMAT = "\"{whole} {numerator}/{denominator} {unit.getPluralAbbreviation()}\""; // Fill this in

  // ==================== Exercise 1.2: Reading Instructor Tests ====================

  /**
   * Question 6: Looking at the sample instructor test, what value does
   * testExactQuantityToStringFormatting expect for 2.5 cups?
   */
  public static final String Q6_EXPECTED_OUTPUT = "2.5 cups"; // Fill this in

  /** Question 7: What assertion method is used to verify exceptions are thrown? */
  public static final String Q7_ASSERTION_METHOD = "assertThrows"; // Fill this in

  // ==================== Exercise 1.3: Using Pawtograder Effectively ====================

  /**
   * Question 8: In Pawtograder, what is the maximum number of submissions allowed per day for
   * Assignment 1?
   */
  public static final int Q8_MAX_SUBMISSIONS = 15; // Fill this in

  /**
   * Question 9: True or false: Pawtograder shows you hints about bugs your tests didn't catch (one
   * per submission).
   */
  public static final boolean Q9_HINTS_AVAILABLE = true; // Fill this in
}
