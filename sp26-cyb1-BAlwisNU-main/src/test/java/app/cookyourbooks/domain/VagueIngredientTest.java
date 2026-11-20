package app.cookyourbooks.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Comprehensive tests for VagueIngredient. */
class VagueIngredientTest {

  /** Blank names rejected. */
  static Stream<Arguments> blankNames() {
    return Stream.of(Arguments.of(""), Arguments.of("   "), Arguments.of("\n\t "));
  }

  @ParameterizedTest
  @MethodSource("blankNames")
  void constructorRejectsBlankName(String name) {
    assertThrows(
        IllegalArgumentException.class, () -> new VagueIngredient(name, "to taste", null, null));
  }

  /** toString formatting combinations + trimming behavior. */
  static Stream<Arguments> toStringCases() {
    return Stream.of(
        // Name only
        Arguments.of("water", null, null, null, "water"),

        // Name + description
        Arguments.of("salt", "to taste", null, null, "salt (to taste)"),

        // Name + preparation
        Arguments.of("tomatoes", null, "diced", null, "tomatoes, diced"),

        // Name + description + preparation
        Arguments.of(
            "pepper",
            "freshly ground",
            "coarsely chopped",
            null,
            "pepper (freshly ground), coarsely chopped"),

        // Trimming: description/prep should be trimmed; empty-after-trim should be omitted
        Arguments.of("  basil  ", "  ", "  chopped  ", null, "basil, chopped"),
        Arguments.of("  salt  ", "  to taste  ", "  ", null, "salt (to taste)"));
  }

  @ParameterizedTest
  @MethodSource("toStringCases")
  void toStringFormatsCorrectly(
      String name, String description, String preparation, String notes, String expected) {
    VagueIngredient ing = new VagueIngredient(name, description, preparation, notes);
    assertEquals(expected, ing.toString());
  }

  @Test
  void toString_omitsWhitespaceOnlyDescription() {
    VagueIngredient ing = new VagueIngredient("salt", "   ", null, null);
    assertEquals("salt", ing.toString());
  }

  @Test
  void toString_omitsWhitespaceOnlyPreparation() {
    VagueIngredient ing = new VagueIngredient("tomatoes", null, "   ", null);
    assertEquals("tomatoes", ing.toString());
  }

  @Test
  void toString_trimsAndFormatsDescriptionThenPreparation() {
    VagueIngredient ing =
        new VagueIngredient("  pepper  ", "  freshly ground  ", "  diced  ", "note");
    // Notes must not appear; description must be in parentheses; preparation after comma.
    assertEquals("pepper (freshly ground), diced", ing.toString());
  }
}
