package app.cookyourbooks.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Comprehensive tests for MeasuredIngredient. */
class MeasuredIngredientTest {

  /** Blank names should be rejected (null is handled by NullAway; do not test null). */
  static Stream<Arguments> blankNames() {
    return Stream.of(Arguments.of(""), Arguments.of("   "), Arguments.of("\n\t  "));
  }

  @ParameterizedTest
  @MethodSource("blankNames")
  void constructorRejectsBlankName(String name) {
    Quantity q = new ExactQuantity(1.0, Unit.CUP);
    assertThrows(IllegalArgumentException.class, () -> new MeasuredIngredient(name, q, null, null));
  }

  /** toString cases: with/without preparation and trimming behavior. */
  static Stream<Arguments> toStringCases() {
    return Stream.of(
        Arguments.of(new ExactQuantity(2.5, Unit.CUP), "flour", null, null, "2.5 cups flour"),
        Arguments.of(new FractionalQuantity(0, 1, 2, Unit.CUP), "sugar", "", null, "1/2 cup sugar"),
        // whitespace-only preparation should be trimmed to empty => treated as empty => omit
        Arguments.of(new ExactQuantity(1.0, Unit.CUP), "milk", "   ", null, "1 cup milk"),
        // preparation included when non-empty (after trim)
        Arguments.of(
            new ExactQuantity(2.5, Unit.CUP),
            "  flour  ",
            "  sifted  ",
            null,
            "2.5 cups flour, sifted"),
        // count unit example
        Arguments.of(
            new ExactQuantity(3.0, Unit.WHOLE),
            "eggs",
            "room temperature",
            null,
            "3 whole eggs, room temperature"));
  }

  @ParameterizedTest
  @MethodSource("toStringCases")
  void toStringFormatsCorrectly(
      Quantity quantity, String name, String preparation, String notes, String expected) {
    MeasuredIngredient ing = new MeasuredIngredient(name, quantity, preparation, notes);
    assertEquals(expected, ing.toString());
  }

  @Test
  void equals_isCaseInsensitiveOnName_andIncludesAllFields() {
    Quantity q1 = new ExactQuantity(1.0, Unit.CUP);
    Quantity q2 = new ExactQuantity(1.0, Unit.CUP);

    MeasuredIngredient a = new MeasuredIngredient("Flour", q1, "sifted", "note");
    MeasuredIngredient b = new MeasuredIngredient("flour", q2, "sifted", "note");

    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());

    // Different notes => not equal
    MeasuredIngredient c = new MeasuredIngredient("flour", q2, "sifted", "different");
    assertNotEquals(a, c);

    // Different preparation => not equal
    MeasuredIngredient d = new MeasuredIngredient("flour", q2, "not sifted", "note");
    assertNotEquals(a, d);
  }

  @Test
  void toString_doesNotIncludeNotes() {
    Quantity q = new ExactQuantity(1.0, Unit.CUP);

    // Deliberately choose notes that would be obvious if leaked into output
    MeasuredIngredient ing = new MeasuredIngredient("sugar", q, null, "DO NOT SHOW");

    // Spec: notes should not appear in toString output
    assertEquals("1 cup sugar", ing.toString());
  }

  @Test
  void toString_withPreparationStillDoesNotIncludeNotes() {
    Quantity q = new FractionalQuantity(0, 1, 2, Unit.CUP);
    MeasuredIngredient ing = new MeasuredIngredient("sugar", q, "packed", "SECRET NOTES");

    assertEquals("1/2 cup sugar, packed", ing.toString());
  }
}
