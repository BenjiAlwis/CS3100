package app.cookyourbooks.conversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import app.cookyourbooks.exception.UnsupportedConversionException;
import app.cookyourbooks.model.ExactQuantity;
import app.cookyourbooks.model.Quantity;
import app.cookyourbooks.model.Unit;

/**
 * Tests for the {@link ConversionRegistry} interface.
 *
 * <p>These starter tests provide basic sanity checks. You should enhance them with additional
 * assertions to improve fault-finding ability.
 */
class ConversionRegistryTest {

  /** Helper method to create a standard registry with all standard conversions. */
  private ConversionRegistry standardRegistry() {
    return new LayeredConversionRegistry()
        .withRules(StandardConversions.getAllRules(), ConversionRulePriority.STANDARD);
  }

  @Nested
  @DisplayName("convert(Quantity, Unit)")
  class ConvertWithoutIngredient {

    @Test
    @DisplayName("should convert quantity using standard conversion rules")
    void shouldConvertUsingStandardRules() throws UnsupportedConversionException {
      ConversionRegistry registry = standardRegistry();
      Quantity cups = new ExactQuantity(2, Unit.CUP);

      Quantity result = registry.convert(cups, Unit.MILLILITER);

      assertThat(result.getUnit()).isEqualTo(Unit.MILLILITER);
      // TODO: Enhance this test! A good next step would be to verify that
      // the result is the correct number of milliliters.
      assertThat(result.toDecimal()).isCloseTo(473.176, within(0.01));
    }

    @Test
    @DisplayName("should throw when converting across dimensions without ingredient context")
    void shouldThrowAcrossDimensionsWithoutIngredient() {
      ConversionRegistry registry = standardRegistry();
      Quantity cups = new ExactQuantity(2, Unit.CUP);

      assertThatThrownBy(() -> registry.convert(cups, Unit.GRAM))
          .isInstanceOf(UnsupportedConversionException.class);
    }

    @Test
    @DisplayName("should prefer HOUSE over RECIPE over STANDARD for generic conversions")
    void shouldPreferHouseThenRecipeThenStandard_Generic() throws UnsupportedConversionException {
      ConversionRule standard = new ConversionRule(Unit.CUP, Unit.MILLILITER, 100.0, null);
      ConversionRule recipe = new ConversionRule(Unit.CUP, Unit.MILLILITER, 200.0, null);
      ConversionRule house = new ConversionRule(Unit.CUP, Unit.MILLILITER, 300.0, null);

      ConversionRegistry registry =
          new LayeredConversionRegistry()
              .withRule(standard, ConversionRulePriority.STANDARD)
              .withRule(recipe, ConversionRulePriority.RECIPE)
              .withRule(house, ConversionRulePriority.HOUSE);

      Quantity oneCup = new ExactQuantity(1, Unit.CUP);
      Quantity result = registry.convert(oneCup, Unit.MILLILITER);

      assertThat(result.getUnit()).isEqualTo(Unit.MILLILITER);
      assertThat(result.toDecimal()).isCloseTo(300.0, within(1e-9)); // must be HOUSE
    }

    @Test
    @DisplayName("convert(q, unit) must NOT match ingredient-specific rules (generic-only)")
    void convertWithoutIngredientMustIgnoreIngredientSpecificRules() {
      // ingredient-specific conversion exists, but NO generic conversion exists
      ConversionRule flourOnly = new ConversionRule(Unit.CUP, Unit.MILLILITER, 999.0, "flour");

      ConversionRegistry registry =
          new LayeredConversionRegistry().withRule(flourOnly, ConversionRulePriority.STANDARD);

      Quantity oneCup = new ExactQuantity(1, Unit.CUP);

      // Correct behavior: should throw, since convert(q, unit) can only use generic rules.
      assertThatThrownBy(() -> registry.convert(oneCup, Unit.MILLILITER))
          .isInstanceOf(UnsupportedConversionException.class);
    }

    @Test
    @DisplayName("withRule() must not mutate the original registry")
    void withRuleMustNotMutateOriginalRegistry() throws UnsupportedConversionException {
      ConversionRegistry r1 = new LayeredConversionRegistry();

      ConversionRegistry r2 =
          r1.withRule(
              new ConversionRule(Unit.CUP, Unit.MILLILITER, 999.0, null),
              ConversionRulePriority.STANDARD);

      Quantity oneCup = new ExactQuantity(1, Unit.CUP);

      // If r1 was mutated incorrectly, this would succeed (bug).
      assertThatThrownBy(() -> r1.convert(oneCup, Unit.MILLILITER))
          .isInstanceOf(UnsupportedConversionException.class);

      // r2 should work.
      Quantity out = r2.convert(oneCup, Unit.MILLILITER);
      assertThat(out.toDecimal()).isCloseTo(999.0, within(1e-9));
    }
  }

  @Nested
  @DisplayName("convert(Quantity, Unit, String)")
  class ConvertWithIngredient {

    @Test
    @DisplayName("should convert quantity with ingredient context")
    void shouldConvertWithIngredientContext() throws UnsupportedConversionException {
      ConversionRule flourRule = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "flour");
      ConversionRegistry registry =
          new LayeredConversionRegistry()
              .withRules(StandardConversions.getAllRules(), ConversionRulePriority.STANDARD)
              .withRule(flourRule, ConversionRulePriority.STANDARD);
      Quantity cups = new ExactQuantity(2, Unit.CUP);

      Quantity result = registry.convert(cups, Unit.GRAM, "flour");

      assertThat(result.getUnit()).isEqualTo(Unit.GRAM);
      // TODO: Enhance this test! A good next step would be to verify that
      // the result is the correct number of grams.
      assertThat(result.toDecimal()).isCloseTo(250.0, within(0.01));
    }

    @Test
    @DisplayName("should use house conversion rule for flour volume to weight")
    void shouldUseHouseRuleForFlourVolumeToWeight() throws UnsupportedConversionException {
      ConversionRule houseFlourRule = new ConversionRule(Unit.CUP, Unit.GRAM, 120.0, "flour");
      ConversionRegistry registry =
          new LayeredConversionRegistry()
              .withRules(StandardConversions.getAllRules(), ConversionRulePriority.STANDARD)
              .withRule(houseFlourRule, ConversionRulePriority.HOUSE);
      Quantity cups = new ExactQuantity(2, Unit.CUP);

      Quantity result = registry.convert(cups, Unit.GRAM, "flour");

      assertThat(result.getUnit()).isEqualTo(Unit.GRAM);
      // TODO: Enhance this test! A good next step would be to verify that
      // the result is the correct number of grams.
      assertThat(result.toDecimal()).isCloseTo(240.0, within(0.01)); // 2 * 120 = 240
    }

    @Test
    @DisplayName("should prefer ingredient-specific rule over generic rule at same priority")
    void shouldPreferIngredientSpecificOverGenericSamePriority()
        throws UnsupportedConversionException {
      ConversionRule genericCupToGram = new ConversionRule(Unit.CUP, Unit.GRAM, 200.0, null);
      ConversionRule flourSpecific = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "flour");

      ConversionRegistry registry =
          new LayeredConversionRegistry()
              .withRule(genericCupToGram, ConversionRulePriority.STANDARD)
              .withRule(flourSpecific, ConversionRulePriority.STANDARD);

      Quantity cups = new ExactQuantity(2, Unit.CUP);
      Quantity result = registry.convert(cups, Unit.GRAM, "flour");

      assertThat(result.getUnit()).isEqualTo(Unit.GRAM);
      assertThat(result.toDecimal()).isCloseTo(250.0, within(0.01)); // must use 125, not 200
    }

    @Test
    @DisplayName(
        "should prefer the first-added rule when multiple rules match at same priority/specifity")
    void shouldPreferFirstAddedWhenTie() throws UnsupportedConversionException {
      ConversionRule flourA = new ConversionRule(Unit.CUP, Unit.GRAM, 111.0, "flour");
      ConversionRule flourB = new ConversionRule(Unit.CUP, Unit.GRAM, 222.0, "flour");

      ConversionRegistry registry =
          new LayeredConversionRegistry()
              .withRule(flourA, ConversionRulePriority.STANDARD) // added first
              .withRule(flourB, ConversionRulePriority.STANDARD);

      Quantity cups = new ExactQuantity(2, Unit.CUP);
      Quantity result =
          registry.convert(cups, Unit.GRAM, "FLOUR"); // case-insensitive should still match

      assertThat(result.getUnit()).isEqualTo(Unit.GRAM);
      assertThat(result.toDecimal()).isCloseTo(222.0, within(0.01)); // 2 * 111 = 222
    }

    @Test
    @DisplayName(
        "ingredient mismatch should not use a different ingredient's specific rule (should throw)")
    void ingredientMismatchShouldThrow() {
      ConversionRule flourSpecific = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "flour");

      ConversionRegistry registry =
          new LayeredConversionRegistry().withRule(flourSpecific, ConversionRulePriority.STANDARD);

      Quantity cups = new ExactQuantity(2, Unit.CUP);

      assertThatThrownBy(() -> registry.convert(cups, Unit.GRAM, "sugar"))
          .isInstanceOf(UnsupportedConversionException.class);
    }

    @Test
    @DisplayName("should prefer HOUSE over RECIPE over STANDARD with ingredient context")
    void shouldPreferHouseThenRecipeThenStandard_WithIngredient()
        throws UnsupportedConversionException {
      ConversionRule standard = new ConversionRule(Unit.CUP, Unit.GRAM, 100.0, "flour");
      ConversionRule recipe = new ConversionRule(Unit.CUP, Unit.GRAM, 200.0, "flour");
      ConversionRule house = new ConversionRule(Unit.CUP, Unit.GRAM, 300.0, "flour");

      ConversionRegistry registry =
          new LayeredConversionRegistry()
              .withRule(standard, ConversionRulePriority.STANDARD)
              .withRule(recipe, ConversionRulePriority.RECIPE)
              .withRule(house, ConversionRulePriority.HOUSE);

      Quantity oneCup = new ExactQuantity(1, Unit.CUP);
      Quantity result = registry.convert(oneCup, Unit.GRAM, "flour");

      assertThat(result.getUnit()).isEqualTo(Unit.GRAM);
      assertThat(result.toDecimal()).isCloseTo(300.0, within(1e-9)); // must be HOUSE
    }

    @Test
    @DisplayName(
        "when conversion fails with ingredientName, exception must include ingredient context")
    void failureWithIngredientMustUseIngredientException() {
      ConversionRegistry registry = new LayeredConversionRegistry(); // no rules
      Quantity cups = new ExactQuantity(1, Unit.CUP);

      assertThatThrownBy(() -> registry.convert(cups, Unit.GRAM, "flour"))
          .isInstanceOf(UnsupportedConversionException.class)
          // Detect wrong factory: forIngredient vs forUnits
          .hasMessageContaining("flour");
    }

    @Test
    @DisplayName("priority with ingredient must be per-level: HOUSE generic beats RECIPE specific")
    void priorityWithIngredientMustBePerLevel() throws UnsupportedConversionException {
      ConversionRule houseGeneric = new ConversionRule(Unit.CUP, Unit.GRAM, 999.0, null);
      ConversionRule recipeSpecific = new ConversionRule(Unit.CUP, Unit.GRAM, 111.0, "flour");

      ConversionRegistry registry =
          new LayeredConversionRegistry()
              .withRule(recipeSpecific, ConversionRulePriority.RECIPE)
              .withRule(houseGeneric, ConversionRulePriority.HOUSE);

      Quantity oneCup = new ExactQuantity(1, Unit.CUP);
      Quantity out = registry.convert(oneCup, Unit.GRAM, "flour");

      // Correct: HOUSE is higher priority even though generic.
      assertThat(out.toDecimal()).isCloseTo(999.0, within(1e-9));
    }
  }
}
