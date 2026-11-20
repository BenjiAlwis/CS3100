package app.cookyourbooks.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import app.cookyourbooks.conversion.ConversionRegistry;
import app.cookyourbooks.conversion.ConversionRulePriority;
import app.cookyourbooks.conversion.LayeredConversionRegistry;
import app.cookyourbooks.conversion.StandardConversions;
import app.cookyourbooks.exception.UnsupportedConversionException;

/** Tests for the {@link Recipe} class. */
class RecipeTest {

  @Nested
  @DisplayName("Recipe Transformations")
  class RecipeTransformations {

    /** Helper method to create a standard registry with all standard conversions. */
    private ConversionRegistry standardRegistry() {
      return new LayeredConversionRegistry()
          .withRules(StandardConversions.getAllRules(), ConversionRulePriority.STANDARD);
    }

    @Nested
    @DisplayName("scale()")
    class Scale {

      @Test
      @DisplayName("should scale MeasuredIngredient quantities by the given factor")
      void shouldScaleMeasuredIngredientQuantities() {
        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);
        Ingredient salt = new VagueIngredient("salt", "to taste", null, null);
        Recipe recipe =
            new Recipe("Test Recipe", null, listOf(flour, salt), emptyList(), emptyList());
        ConversionRegistry registry = standardRegistry();

        Recipe scaled = recipe.scale(2.0, registry);

        MeasuredIngredient scaledFlour = (MeasuredIngredient) scaled.getIngredients().get(0);
        assertThat(scaledFlour.getQuantity().getUnit()).isEqualTo(Unit.CUP);
        assertThat(scaledFlour.getQuantity().toDecimal()).isEqualTo(4.0);

        Ingredient scaledSalt = scaled.getIngredients().get(1);
        assertThat(scaledSalt).isInstanceOf(VagueIngredient.class);
        assertThat(scaledSalt).isEqualTo(salt);
      }
    }

    @Nested
    @DisplayName("scaleToTarget()")
    class ScaleToTarget {

      @Test
      @DisplayName("should scale recipe so ingredient reaches target amount")
      void shouldScaleRecipeToTargetAmount() throws UnsupportedConversionException {
        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);
        Recipe recipe = new Recipe("Test Recipe", null, listOf(flour), emptyList(), emptyList());
        ConversionRegistry registry = standardRegistry();

        Quantity targetAmount = new ExactQuantity(4, Unit.CUP);
        Recipe scaled = recipe.scaleToTarget("flour", targetAmount, registry);

        MeasuredIngredient scaledFlour = (MeasuredIngredient) scaled.getIngredients().get(0);
        assertThat(scaledFlour.getQuantity().getUnit()).isEqualTo(Unit.CUP);
        assertThat(scaledFlour.getQuantity().toDecimal()).isEqualTo(4.0);
      }
    }

    @Nested
    @DisplayName("convert()")
    class Convert {

      @Test
      @DisplayName("should convert MeasuredIngredient quantities to target unit")
      void shouldConvertMeasuredIngredientQuantities() throws UnsupportedConversionException {
        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);
        Recipe recipe = new Recipe("Test Recipe", null, listOf(flour), emptyList(), emptyList());
        ConversionRegistry registry = standardRegistry();

        Recipe converted = recipe.convert(Unit.MILLILITER, registry);

        MeasuredIngredient convertedFlour = (MeasuredIngredient) converted.getIngredients().get(0);
        assertThat(convertedFlour.getQuantity().getUnit()).isEqualTo(Unit.MILLILITER);
        assertThat(convertedFlour.getQuantity().toDecimal()).isCloseTo(473.176, within(0.001));
      }
    }
  }

  private static <T> List<T> emptyList() {
    return Objects.requireNonNull(List.of());
  }

  private static <T> List<T> listOf(T item) {
    return Objects.requireNonNull(List.of(item));
  }

  private static <T> List<T> listOf(T first, T second) {
    return Objects.requireNonNull(List.of(first, second));
  }
}
