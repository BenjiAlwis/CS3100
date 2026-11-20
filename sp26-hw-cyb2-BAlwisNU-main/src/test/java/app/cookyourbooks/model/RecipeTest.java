package app.cookyourbooks.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import app.cookyourbooks.conversion.ConversionRegistry;
import app.cookyourbooks.conversion.ConversionRule;
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
        Recipe recipe = new Recipe("Test Recipe", null, List.of(flour), List.of(), List.of());
        ConversionRegistry registry = standardRegistry();

        Recipe scaled = recipe.scale(2.0, registry);

        MeasuredIngredient scaledFlour = (MeasuredIngredient) scaled.getIngredients().get(0);
        assertThat(scaledFlour.getQuantity().getUnit()).isEqualTo(Unit.CUP);

        // TODO: Enhance this test! A good next step would be to verify that
        // VagueIngredients remain unchanged after scaling. What else should you test?
        assertThat(scaledFlour.getQuantity().toDecimal()).isCloseTo(4.0, within(1e-9));
      }

      @Test
      @DisplayName("should NOT modify VagueIngredients when scaling")
      void shouldNotModifyVagueIngredients() {
        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);
        Ingredient salt = new VagueIngredient("salt", "to taste", null, null);

        Recipe recipe = new Recipe("Test Recipe", null, List.of(flour, salt), List.of(), List.of());

        Recipe scaled = recipe.scale(3.0, standardRegistry());

        // MeasuredIngredient scales
        MeasuredIngredient scaledFlour = (MeasuredIngredient) scaled.getIngredients().get(0);
        assertThat(scaledFlour.getQuantity().getUnit()).isEqualTo(Unit.CUP);
        assertThat(scaledFlour.getQuantity().toDecimal()).isCloseTo(6.0, within(1e-9));

        // VagueIngredient remains unchanged (deep equals)
        assertThat(scaled.getIngredients().get(1)).isEqualTo(salt);
      }

      @Test
      @DisplayName("should scale IngredientRefs inside instructions")
      void shouldScaleInstructionIngredientRefs() {
        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);

        IngredientRef ref = new IngredientRef(flour, new ExactQuantity(1, Unit.CUP));
        Instruction step = new Instruction(1, "Add flour", List.of(ref));

        Recipe recipe = new Recipe("Test Recipe", null, List.of(flour), List.of(step), List.of());

        Recipe scaled = recipe.scale(2.0, standardRegistry());

        Instruction scaledStep = scaled.getInstructions().get(0);
        IngredientRef scaledRef = scaledStep.getIngredientRefs().get(0);

        assertThat(scaledRef.quantity().getUnit()).isEqualTo(Unit.CUP);
        assertThat(scaledRef.quantity().toDecimal()).isCloseTo(2.0, within(1e-9));
      }

      @Test
      @DisplayName("should scale servings when present")
      void shouldScaleServingsWhenPresent() {
        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);
        Quantity servings = new ExactQuantity(4, Unit.WHOLE);

        Recipe recipe = new Recipe("Test Recipe", servings, List.of(flour), List.of(), List.of());

        Recipe scaled = recipe.scale(0.5, standardRegistry());

        Quantity scaledServings = scaled.getServings();
        assertThat(scaledServings).isNotNull();

        // NullAway needs an explicit guard it understands:
        if (scaledServings == null) {
          throw new AssertionError("Expected servings to be non-null after scaling");
        }

        assertThat(scaledServings.getUnit()).isEqualTo(Unit.WHOLE);
        assertThat(scaledServings.toDecimal()).isCloseTo(2.0, within(1e-9));
      }

      @Test
      @DisplayName("scale() should not mutate the original recipe")
      void scaleShouldNotMutateOriginal() {
        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);

        IngredientRef ref = new IngredientRef(flour, new ExactQuantity(1, Unit.CUP));
        Instruction step = new Instruction(1, "Add flour", List.of(ref));

        Recipe recipe = new Recipe("Test Recipe", null, List.of(flour), List.of(step), List.of());

        Recipe scaled = recipe.scale(2.0, standardRegistry());

        // Original unchanged
        MeasuredIngredient originalFlour = (MeasuredIngredient) recipe.getIngredients().get(0);
        assertThat(originalFlour.getQuantity().getUnit()).isEqualTo(Unit.CUP);
        assertThat(originalFlour.getQuantity().toDecimal()).isCloseTo(2.0, within(1e-9));

        IngredientRef originalRef = recipe.getInstructions().get(0).getIngredientRefs().get(0);
        assertThat(originalRef.quantity().getUnit()).isEqualTo(Unit.CUP);
        assertThat(originalRef.quantity().toDecimal()).isCloseTo(1.0, within(1e-9));

        // Sanity: scaled is different
        MeasuredIngredient scaledFlour = (MeasuredIngredient) scaled.getIngredients().get(0);
        assertThat(scaledFlour.getQuantity().toDecimal()).isCloseTo(4.0, within(1e-9));
      }

      @Test
      @DisplayName("should preserve MeasuredIngredient metadata (preparation, notes) when scaling")
      void shouldPreserveMeasuredIngredientMetadataWhenScaling() {
        Ingredient flour =
            new MeasuredIngredient(
                "flour", new ExactQuantity(2, Unit.CUP), "sifted", "use bread flour");

        Recipe recipe = new Recipe("Test", null, List.of(flour), List.of(), List.of());

        Recipe scaled = recipe.scale(2.0, standardRegistry());

        MeasuredIngredient scaledFlour = (MeasuredIngredient) scaled.getIngredients().get(0);

        assertThat(scaledFlour.getName()).isEqualTo("flour");
        assertThat(scaledFlour.getPreparation()).isEqualTo("sifted");
        assertThat(scaledFlour.getNotes()).isEqualTo("use bread flour");

        assertThat(scaledFlour.getQuantity().getUnit()).isEqualTo(Unit.CUP);
        assertThat(scaledFlour.getQuantity().toDecimal()).isCloseTo(4.0, within(1e-9));
      }

      @Test
      @DisplayName("should keep RangeQuantity as RangeQuantity when scaling (min and max scaled)")
      void shouldPreserveRangeQuantityStructureWhenScaling() {
        Quantity range = new RangeQuantity(1.0, 2.0, Unit.CUP); // min=1, max=2
        Ingredient flour = new MeasuredIngredient("flour", range, null, null);

        Recipe recipe = new Recipe("Test", null, List.of(flour), List.of(), List.of());

        Recipe scaled = recipe.scale(3.0, standardRegistry());

        MeasuredIngredient scaledFlour = (MeasuredIngredient) scaled.getIngredients().get(0);

        assertThat(scaledFlour.getQuantity()).isInstanceOf(RangeQuantity.class);
        RangeQuantity scaledRange = (RangeQuantity) scaledFlour.getQuantity();

        assertThat(scaledRange.getUnit()).isEqualTo(Unit.CUP);
        assertThat(scaledRange.toDecimal()).isCloseTo(4.5, within(1e-9)); // midpoint of 3..6 is 4.5

        assertThat(scaledRange.getMin()).isCloseTo(3.0, within(1e-9));
        assertThat(scaledRange.getMax()).isCloseTo(6.0, within(1e-9));
      }

      @Test
      @DisplayName("scale() should NOT scale IngredientRefs that point to VagueIngredients")
      void scaleShouldNotModifyVagueIngredientRefs() {
        Ingredient salt = new VagueIngredient("salt", "to taste", null, null);

        IngredientRef saltRef = new IngredientRef(salt, new ExactQuantity(1.0, Unit.TEASPOON));
        Instruction step = new Instruction(1, "Add salt", List.of(saltRef));

        Recipe recipe = new Recipe("Test", null, List.of(salt), List.of(step), List.of());

        Recipe scaled = recipe.scale(2.0, standardRegistry());

        IngredientRef scaledRef = scaled.getInstructions().get(0).getIngredientRefs().get(0);
        assertThat(scaledRef.quantity().getUnit()).isEqualTo(Unit.TEASPOON);
        assertThat(scaledRef.quantity().toDecimal()).isCloseTo(1.0, within(1e-9)); // unchanged
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
        Recipe recipe = new Recipe("Test Recipe", null, List.of(flour), List.of(), List.of());
        ConversionRegistry registry = standardRegistry();

        Quantity targetAmount = new ExactQuantity(4, Unit.CUP);
        Recipe scaled = recipe.scaleToTarget("flour", targetAmount, registry);

        MeasuredIngredient scaledFlour = (MeasuredIngredient) scaled.getIngredients().get(0);
        assertThat(scaledFlour.getQuantity().getUnit()).isEqualTo(Unit.CUP);

        // TODO: Enhance this test! A good next step would be to verify that
        // the recipe is scaled to the target amount.
        assertThat(scaledFlour.getQuantity().toDecimal()).isCloseTo(4.0, within(1e-9));
      }

      @Test
      @DisplayName(
          "should use recipe-specific conversion rules (RECIPE priority) to reach target unit")
      void shouldUseRecipeSpecificRuleToReachTargetUnit() throws UnsupportedConversionException {
        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);

        // Recipe-specific density: 1 CUP flour = 125 GRAM
        ConversionRule flourDensity = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "flour");

        Recipe recipe =
            new Recipe("Test Recipe", null, List.of(flour), List.of(), List.of(flourDensity));

        Quantity targetAmount = new ExactQuantity(500, Unit.GRAM);

        Recipe scaled = recipe.scaleToTarget("FLOUR", targetAmount, standardRegistry());

        MeasuredIngredient scaledFlour = (MeasuredIngredient) scaled.getIngredients().get(0);

        // Must end in the target unit
        assertThat(scaledFlour.getQuantity().getUnit()).isEqualTo(Unit.GRAM);

        // 2 cups -> 250g. target 500g => factor 2.0 => 500g.
        assertThat(scaledFlour.getQuantity().toDecimal()).isCloseTo(500.0, within(0.01));
      }

      @Test
      @DisplayName("should throw if target ingredient name is not found")
      void shouldThrowIfIngredientNotFound() {
        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);
        Recipe recipe = new Recipe("Test Recipe", null, List.of(flour), List.of(), List.of());

        Quantity targetAmount = new ExactQuantity(4, Unit.CUP);

        assertThatThrownBy(() -> recipe.scaleToTarget("sugar", targetAmount, standardRegistry()))
            .isInstanceOf(UnsupportedConversionException.class);
      }

      @Test
      @DisplayName(
          "scaleToTarget must prefer RECIPE rules over STANDARD rules (detect wrong priority)")
      void scaleToTargetShouldPreferRecipeRulesOverStandardRules()
          throws UnsupportedConversionException {

        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);

        // Second ingredient to observe the scale factor.
        Ingredient water =
            new MeasuredIngredient("water", new ExactQuantity(1, Unit.CUP), null, null);

        // Recipe rule: 1 CUP flour = 125g
        ConversionRule recipeRule = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "flour");

        // Conflicting STANDARD rule in the registry: 1 CUP flour = 100g
        ConversionRule standardRule = new ConversionRule(Unit.CUP, Unit.GRAM, 100.0, "flour");

        ConversionRegistry registry =
            new LayeredConversionRegistry()
                .withRules(StandardConversions.getAllRules(), ConversionRulePriority.STANDARD)
                .withRule(standardRule, ConversionRulePriority.STANDARD);

        Recipe recipe =
            new Recipe("Test", null, List.of(flour, water), List.of(), List.of(recipeRule));

        // target 500g flour
        Recipe scaled = recipe.scaleToTarget("flour", new ExactQuantity(500, Unit.GRAM), registry);

        // Flour ends at 500g either way; that's NOT enough to detect the bug.
        MeasuredIngredient scaledFlour = (MeasuredIngredient) scaled.getIngredients().get(0);
        assertThat(scaledFlour.getQuantity().getUnit()).isEqualTo(Unit.GRAM);
        assertThat(scaledFlour.getQuantity().toDecimal()).isCloseTo(500.0, within(0.01));

        // But the scale factor differs:
        // - If recipe rule used: 2 cups=250g => factor=2.0 => water 1 cup -> 2 cups
        // - If STANDARD rule used: 2 cups=200g => factor=2.5 => water 1 cup -> 2.5 cups
        MeasuredIngredient scaledWater = (MeasuredIngredient) scaled.getIngredients().get(1);
        assertThat(scaledWater.getQuantity().getUnit()).isEqualTo(Unit.CUP);
        assertThat(scaledWater.getQuantity().toDecimal()).isCloseTo(2.0, within(1e-9));
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
        Recipe recipe = new Recipe("Test Recipe", null, List.of(flour), List.of(), List.of());
        ConversionRegistry registry = standardRegistry();

        Recipe converted = recipe.convert(Unit.MILLILITER, registry);

        MeasuredIngredient convertedFlour = (MeasuredIngredient) converted.getIngredients().get(0);
        assertThat(convertedFlour.getQuantity().getUnit()).isEqualTo(Unit.MILLILITER);

        // Enhanced: verify numeric correctness (2 CUP -> 473.176 mL)
        assertThat(convertedFlour.getQuantity().toDecimal()).isCloseTo(473.176, within(0.01));
      }

      @Test
      @DisplayName("should NOT modify VagueIngredients when converting")
      void shouldNotModifyVagueIngredientsOnConvert() throws UnsupportedConversionException {
        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);
        Ingredient salt = new VagueIngredient("salt", "to taste", null, null);

        Recipe recipe = new Recipe("Test Recipe", null, List.of(flour, salt), List.of(), List.of());

        Recipe converted = recipe.convert(Unit.MILLILITER, standardRegistry());

        assertThat(converted.getIngredients().get(1)).isEqualTo(salt);
      }

      @Test
      @DisplayName("should convert IngredientRefs in instructions")
      void shouldConvertInstructionIngredientRefs() throws UnsupportedConversionException {
        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);

        IngredientRef ref = new IngredientRef(flour, new ExactQuantity(1, Unit.CUP));
        Instruction step = new Instruction(1, "Add flour", List.of(ref));

        Recipe recipe = new Recipe("Test Recipe", null, List.of(flour), List.of(step), List.of());

        Recipe converted = recipe.convert(Unit.MILLILITER, standardRegistry());

        IngredientRef convertedRef = converted.getInstructions().get(0).getIngredientRefs().get(0);
        assertThat(convertedRef.quantity().getUnit()).isEqualTo(Unit.MILLILITER);
        assertThat(convertedRef.quantity().toDecimal()).isCloseTo(236.588, within(0.01));
      }

      @Test
      @DisplayName("should throw if any MeasuredIngredient cannot be converted")
      void shouldThrowIfAnyMeasuredIngredientCannotConvert() {
        Ingredient eggs =
            new MeasuredIngredient("eggs", new ExactQuantity(2, Unit.WHOLE), null, null);

        Recipe recipe = new Recipe("Test Recipe", null, List.of(eggs), List.of(), List.of());

        assertThatThrownBy(() -> recipe.convert(Unit.GRAM, standardRegistry()))
            .isInstanceOf(UnsupportedConversionException.class);
      }

      @Test
      @DisplayName("should use recipe-specific conversion rules (RECIPE priority) when converting")
      void shouldUseRecipeSpecificRulesOnConvert() throws UnsupportedConversionException {
        Ingredient flour =
            new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), null, null);

        // Recipe rule: 1 CUP flour = 125 GRAM
        ConversionRule flourDensity = new ConversionRule(Unit.CUP, Unit.GRAM, 125.0, "flour");
        Recipe recipe =
            new Recipe("Test Recipe", null, List.of(flour), List.of(), List.of(flourDensity));

        Recipe converted = recipe.convert(Unit.GRAM, standardRegistry());

        MeasuredIngredient convertedFlour = (MeasuredIngredient) converted.getIngredients().get(0);
        assertThat(convertedFlour.getQuantity().getUnit()).isEqualTo(Unit.GRAM);
        assertThat(convertedFlour.getQuantity().toDecimal()).isCloseTo(250.0, within(0.01));
      }
    }
  }
}
