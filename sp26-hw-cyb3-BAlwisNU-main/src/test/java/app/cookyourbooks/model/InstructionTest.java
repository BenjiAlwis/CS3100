package app.cookyourbooks.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for the {@link Instruction} class.
 *
 * <p>These tests verify instruction construction, validation, and basic behavior.
 */
class InstructionTest {

  /** Provides diverse instruction texts for testing. */
  static Stream<Arguments> validInstructionTexts() {
    return Objects.requireNonNull(
        Stream.of(
            Arguments.of(1, "Preheat oven to 350°F", "basic preheat"),
            Arguments.of(2, "Mix flour and sugar in a large bowl", "mixing step"),
            Arguments.of(
                3, "Add eggs one at a time, beating well after each addition", "detailed step"),
            Arguments.of(4, "Fold in chocolate chips gently", "folding step"),
            Arguments.of(5, "Pour batter into prepared pan", "pouring step"),
            Arguments.of(
                6, "Bake for 25-30 minutes or until golden brown", "baking with time range"),
            Arguments.of(7, "Let cool in pan for 10 minutes before removing", "cooling step"),
            Arguments.of(8, "Serve warm with vanilla ice cream", "serving suggestion"),
            Arguments.of(9, "Store in airtight container for up to 3 days", "storage instructions"),
            Arguments.of(10, "Garnish with fresh mint leaves", "garnishing step")));
  }

  /** Provides step number and text combinations for toString() testing. */
  static Stream<Arguments> toStringTestCases() {
    return Objects.requireNonNull(
        Stream.of(
            Arguments.of(1, "Preheat oven", "1. Preheat oven"),
            Arguments.of(2, "Mix ingredients", "2. Mix ingredients"),
            Arguments.of(5, "Bake until golden", "5. Bake until golden"),
            Arguments.of(10, "Serve warm", "10. Serve warm"),
            Arguments.of(15, "Let cool completely", "15. Let cool completely"),
            Arguments.of(99, "Final step", "99. Final step"),
            Arguments.of(100, "Bonus tip", "100. Bonus tip")));
  }

  @Nested
  @DisplayName("toString()")
  class ToStringTests {

    @ParameterizedTest(name = "step {0}: \"{1}\" -> \"{2}\"")
    @MethodSource("app.cookyourbooks.model.InstructionTest#toStringTestCases")
    @DisplayName("should format as stepNumber. text")
    void shouldFormatCorrectly(int stepNumber, String text, String expected) {
      Instruction instruction = new Instruction(stepNumber, text, List.of());

      assertThat(instruction.toString()).isEqualTo(expected);
    }

    @ParameterizedTest(name = "step {0}: {2}")
    @MethodSource("app.cookyourbooks.model.InstructionTest#validInstructionTexts")
    @DisplayName("toString should start with step number followed by period and space")
    void toStringShouldHaveCorrectFormat(int stepNumber, String text, String description) {
      Instruction instruction = new Instruction(stepNumber, text, List.of());

      String result = instruction.toString();

      assertThat(result).startsWith(stepNumber + ". ");
      assertThat(result).endsWith(text);
    }
  }
}
