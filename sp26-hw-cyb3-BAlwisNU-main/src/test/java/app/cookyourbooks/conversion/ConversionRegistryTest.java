package app.cookyourbooks.conversion;

import static org.assertj.core.api.Assertions.assertThat;
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
      assertThat(result.toDecimal()).isCloseTo(473.176, within(0.001));
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
      assertThat(result.toDecimal()).isCloseTo(250.0, within(0.0001));
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
      assertThat(result.toDecimal()).isCloseTo(240.0, within(0.0001));
    }
  }
}
