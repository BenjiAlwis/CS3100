package app.cookyourbooks.conversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import app.cookyourbooks.model.ExactQuantity;
import app.cookyourbooks.model.Quantity;
import app.cookyourbooks.model.Unit;

/**
 * Tests for the {@link ConversionRule} record.
 *
 * <p>These tests verify the core conversion rule functionality including construction validation,
 * rule matching via canConvert(), and quantity conversion.
 */
class ConversionRuleTest {

  /** Provides representative generic conversion rules for testing. */
  static Stream<Arguments> genericConversionRules() {
    return Objects.requireNonNull(
        Stream.of(
            // Volume conversion with typical factor
            Arguments.of(Unit.CUP, Unit.MILLILITER, 236.588, "cups to mL"),
            // Weight conversion
            Arguments.of(Unit.POUND, Unit.GRAM, 453.592, "lb to g"),
            // Small conversion factor (less than 1)
            Arguments.of(Unit.MILLILITER, Unit.LITER, 0.001, "mL to L")));
  }

  /** Provides representative ingredient-specific density conversion rules for testing. */
  static Stream<Arguments> ingredientSpecificRules() {
    return Objects.requireNonNull(
        Stream.of(
            // Single-word ingredient
            Arguments.of(Unit.CUP, Unit.GRAM, 125.0, "flour", "flour density"),
            // Multi-word ingredient name
            Arguments.of(Unit.CUP, Unit.GRAM, 220.0, "brown sugar", "brown sugar density"),
            // Different unit pair
            Arguments.of(Unit.TABLESPOON, Unit.GRAM, 14.0, "butter", "butter per tbsp")));
  }

  /** Provides invalid conversion factors for testing rejection. */
  static Stream<Arguments> invalidFactors() {
    return Objects.requireNonNull(
        Stream.of(
            Arguments.of(0.0, "zero"),
            Arguments.of(-1.0, "negative"),
            Arguments.of(Double.NEGATIVE_INFINITY, "negative infinity")));
  }

  /** Provides case variations for ingredient name matching. */
  static Stream<Arguments> caseVariations() {
    return Objects.requireNonNull(
        Stream.of(
            Arguments.of("flour", "FLOUR", "uppercase query"),
            Arguments.of("flour", "Flour", "title case query"),
            Arguments.of("Brown Sugar", "brown sugar", "multi-word lowercase query")));
  }

  /** Provides conversion test cases with input amounts and expected outputs. */
  static Stream<Arguments> conversionTestCases() {
    return Objects.requireNonNull(
        Stream.of(
            // Unit amount
            Arguments.of(Unit.CUP, Unit.MILLILITER, 236.588, 1.0, 236.588),
            // Multiple units
            Arguments.of(Unit.CUP, Unit.MILLILITER, 236.588, 2.0, 473.176),
            // Fractional amount
            Arguments.of(Unit.TABLESPOON, Unit.TEASPOON, 3.0, 0.5, 1.5),
            // Small factor (less than 1)
            Arguments.of(Unit.GRAM, Unit.KILOGRAM, 0.001, 1000.0, 1.0)));
  }

  @Nested
  @DisplayName("Constructor Validation")
  class ConstructorValidation {

    @ParameterizedTest(name = "{3}")
    @MethodSource("app.cookyourbooks.conversion.ConversionRuleTest#genericConversionRules")
    @DisplayName("should create valid generic rules")
    void shouldCreateValidGenericRules(
        Unit fromUnit, Unit toUnit, double factor, String description) {
      ConversionRule rule = new ConversionRule(fromUnit, toUnit, factor, null);

      assertThat(rule.fromUnit()).isEqualTo(fromUnit);
      assertThat(rule.toUnit()).isEqualTo(toUnit);
      assertThat(rule.factor()).isCloseTo(factor, within(0.001));
      assertThat(rule.ingredientName()).isNull();
    }

    @ParameterizedTest(name = "{4}")
    @MethodSource("app.cookyourbooks.conversion.ConversionRuleTest#ingredientSpecificRules")
    @DisplayName("should create valid ingredient-specific rules")
    void shouldCreateValidIngredientSpecificRules(
        Unit fromUnit, Unit toUnit, double factor, String ingredientName, String description) {
      ConversionRule rule = new ConversionRule(fromUnit, toUnit, factor, ingredientName);

      assertThat(rule.fromUnit()).isEqualTo(fromUnit);
      assertThat(rule.toUnit()).isEqualTo(toUnit);
      assertThat(rule.factor()).isCloseTo(factor, within(0.001));
      assertThat(rule.ingredientName()).isEqualTo(ingredientName);
    }

    @ParameterizedTest(name = "should reject {1} factor")
    @MethodSource("app.cookyourbooks.conversion.ConversionRuleTest#invalidFactors")
    @DisplayName("should reject invalid factors")
    void shouldRejectInvalidFactors(double factor, String description) {
      assertThatThrownBy(() -> new ConversionRule(Unit.CUP, Unit.MILLILITER, factor, null))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "should accept factor {0}")
    @ValueSource(doubles = {0.001, 1.0, 1000.0, Double.MAX_VALUE})
    @DisplayName("should accept various positive factors")
    void shouldAcceptPositiveFactors(double factor) {
      ConversionRule rule = new ConversionRule(Unit.CUP, Unit.GRAM, factor, null);
      assertThat(rule.factor()).isCloseTo(factor, within(factor * 0.0001));
    }
  }

  @Nested
  @DisplayName("canConvert()")
  class CanConvert {

    @ParameterizedTest(name = "{3}")
    @MethodSource("app.cookyourbooks.conversion.ConversionRuleTest#genericConversionRules")
    @DisplayName("generic rules should match their units without ingredient")
    void genericRulesShouldMatchWithoutIngredient(
        Unit fromUnit, Unit toUnit, double factor, String description) {
      ConversionRule rule = new ConversionRule(fromUnit, toUnit, factor, null);

      assertThat(rule.canConvert(fromUnit, toUnit, null)).isTrue();
    }

    @ParameterizedTest(name = "generic rule should match with ingredient: {0}")
    @ValueSource(strings = {"flour", "sugar"})
    @DisplayName("generic rules should match with any ingredient")
    void genericRulesShouldMatchWithAnyIngredient(String ingredient) {
      ConversionRule rule = new ConversionRule(Unit.CUP, Unit.MILLILITER, 236.588, null);

      assertThat(rule.canConvert(Unit.CUP, Unit.MILLILITER, ingredient)).isTrue();
    }

    @ParameterizedTest(name = "{4}")
    @MethodSource("app.cookyourbooks.conversion.ConversionRuleTest#ingredientSpecificRules")
    @DisplayName("ingredient-specific rules should match their ingredient")
    void ingredientRulesShouldMatchTheirIngredient(
        Unit fromUnit, Unit toUnit, double factor, String ingredientName, String description) {
      ConversionRule rule = new ConversionRule(fromUnit, toUnit, factor, ingredientName);

      assertThat(rule.canConvert(fromUnit, toUnit, ingredientName)).isTrue();
    }

    @ParameterizedTest(name = "rule ingredient={0}, query ingredient={1} ({2})")
    @MethodSource("app.cookyourbooks.conversion.ConversionRuleTest#caseVariations")
    @DisplayName("ingredient matching should be case-insensitive")
    void ingredientMatchingShouldBeCaseInsensitive(
        String ruleIngredient, String queryIngredient, String description) {
      ConversionRule rule = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, ruleIngredient);

      assertThat(rule.canConvert(Unit.CUP, Unit.GRAM, queryIngredient)).isTrue();
    }

    @ParameterizedTest(name = "flour rule should not match {0}")
    @ValueSource(strings = {"sugar", "butter"})
    @DisplayName("ingredient-specific rules should not match different ingredients")
    void ingredientRulesShouldNotMatchDifferentIngredients(String otherIngredient) {
      ConversionRule rule = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "flour");

      assertThat(rule.canConvert(Unit.CUP, Unit.GRAM, otherIngredient)).isFalse();
    }

    @Test
    @DisplayName("ingredient-specific rule should not match null ingredient")
    void ingredientRuleShouldNotMatchNullIngredient() {
      ConversionRule rule = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "flour");

      assertThat(rule.canConvert(Unit.CUP, Unit.GRAM, null)).isFalse();
    }

    @ParameterizedTest(name = "CUP->ML rule should not match {0}->ML")
    @CsvSource({"TABLESPOON", "GRAM"})
    @DisplayName("rules should not match wrong fromUnit")
    void rulesShouldNotMatchWrongFromUnit(Unit wrongFromUnit) {
      ConversionRule rule = new ConversionRule(Unit.CUP, Unit.MILLILITER, 236.588, null);

      assertThat(rule.canConvert(wrongFromUnit, Unit.MILLILITER, null)).isFalse();
    }

    @ParameterizedTest(name = "CUP->ML rule should not match CUP->{0}")
    @CsvSource({"LITER", "GRAM"})
    @DisplayName("rules should not match wrong toUnit")
    void rulesShouldNotMatchWrongToUnit(Unit wrongToUnit) {
      ConversionRule rule = new ConversionRule(Unit.CUP, Unit.MILLILITER, 236.588, null);

      assertThat(rule.canConvert(Unit.CUP, wrongToUnit, null)).isFalse();
    }
  }

  @Nested
  @DisplayName("convert()")
  class Convert {

    @ParameterizedTest(name = "{0} amount={3} -> {4}")
    @MethodSource("app.cookyourbooks.conversion.ConversionRuleTest#conversionTestCases")
    @DisplayName("should correctly convert quantities")
    void shouldCorrectlyConvertQuantities(
        Unit fromUnit, Unit toUnit, double factor, double inputAmount, double expectedOutput) {
      ConversionRule rule = new ConversionRule(fromUnit, toUnit, factor, null);
      Quantity input = new ExactQuantity(inputAmount, fromUnit);

      Quantity result = rule.convert(input);

      assertThat(result.getUnit()).isEqualTo(toUnit);
      assertThat(result.toDecimal()).isCloseTo(expectedOutput, within(0.01));
    }

    @ParameterizedTest(name = "{4}")
    @MethodSource("app.cookyourbooks.conversion.ConversionRuleTest#ingredientSpecificRules")
    @DisplayName("should convert using ingredient-specific factors")
    void shouldConvertWithIngredientFactors(
        Unit fromUnit, Unit toUnit, double factor, String ingredientName, String description) {
      ConversionRule rule = new ConversionRule(fromUnit, toUnit, factor, ingredientName);
      Quantity input = new ExactQuantity(1.0, fromUnit);

      Quantity result = rule.convert(input);

      assertThat(result.getUnit()).isEqualTo(toUnit);
      assertThat(result.toDecimal()).isCloseTo(factor, within(0.01));
    }

    @ParameterizedTest(name = "CUP->ML rule should reject {0} input")
    @CsvSource({"TABLESPOON", "GRAM"})
    @DisplayName("should reject quantity with wrong unit")
    void shouldRejectWrongUnit(Unit wrongUnit) {
      ConversionRule rule = new ConversionRule(Unit.CUP, Unit.MILLILITER, 236.588, null);
      Quantity input = new ExactQuantity(1.0, wrongUnit);

      assertThatThrownBy(() -> rule.convert(input)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should return ExactQuantity")
    void shouldReturnExactQuantity() {
      ConversionRule rule = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "flour");
      Quantity input = new ExactQuantity(1.0, Unit.CUP);

      Quantity result = rule.convert(input);

      assertThat(result).isInstanceOf(ExactQuantity.class);
    }
  }

  @Nested
  @DisplayName("equals() and hashCode()")
  class Equality {

    @ParameterizedTest(name = "{3}")
    @MethodSource("app.cookyourbooks.conversion.ConversionRuleTest#genericConversionRules")
    @DisplayName("identical generic rules should be equal")
    void identicalGenericRulesShouldBeEqual(
        Unit fromUnit, Unit toUnit, double factor, String description) {
      ConversionRule rule1 = new ConversionRule(fromUnit, toUnit, factor, null);
      ConversionRule rule2 = new ConversionRule(fromUnit, toUnit, factor, null);

      assertThat(rule1).isEqualTo(rule2);
      assertThat(rule1.hashCode()).isEqualTo(rule2.hashCode());
    }

    @ParameterizedTest(name = "{4}")
    @MethodSource("app.cookyourbooks.conversion.ConversionRuleTest#ingredientSpecificRules")
    @DisplayName("identical ingredient-specific rules should be equal")
    void identicalIngredientRulesShouldBeEqual(
        Unit fromUnit, Unit toUnit, double factor, String ingredientName, String description) {
      ConversionRule rule1 = new ConversionRule(fromUnit, toUnit, factor, ingredientName);
      ConversionRule rule2 = new ConversionRule(fromUnit, toUnit, factor, ingredientName);

      assertThat(rule1).isEqualTo(rule2);
      assertThat(rule1.hashCode()).isEqualTo(rule2.hashCode());
    }

    @Test
    @DisplayName("rules with different ingredients should not be equal")
    void differentIngredientsShouldNotBeEqual() {
      ConversionRule flourRule = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "flour");
      ConversionRule sugarRule = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "sugar");

      assertThat(flourRule).isNotEqualTo(sugarRule);
    }

    @Test
    @DisplayName("rules with different factors should not be equal")
    void differentFactorsShouldNotBeEqual() {
      ConversionRule rule1 = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "flour");
      ConversionRule rule2 = new ConversionRule(Unit.CUP, Unit.GRAM, 130.0, "flour");

      assertThat(rule1).isNotEqualTo(rule2);
    }

    @Test
    @DisplayName("rules with different units should not be equal")
    void differentUnitsShouldNotBeEqual() {
      ConversionRule rule1 = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, null);
      ConversionRule rule2 = new ConversionRule(Unit.CUP, Unit.KILOGRAM, 125.0, null);
      ConversionRule rule3 = new ConversionRule(Unit.TABLESPOON, Unit.GRAM, 125.0, null);

      assertThat(rule1).isNotEqualTo(rule2);
      assertThat(rule1).isNotEqualTo(rule3);
    }

    @Test
    @DisplayName("generic rule should not equal ingredient-specific rule with same units/factor")
    void genericRuleShouldNotEqualIngredientSpecificRule() {
      ConversionRule generic = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, null);
      ConversionRule specific = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "flour");

      assertThat(generic).isNotEqualTo(specific);
    }
  }
}
