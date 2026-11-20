package app.cookyourbooks.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import app.cookyourbooks.conversion.ConversionRegistry;
import app.cookyourbooks.conversion.ConversionRule;
import app.cookyourbooks.conversion.ConversionRulePriority;
import app.cookyourbooks.exception.UnsupportedConversionException;

/**
 * Represents a complete recipe.
 *
 * <p>Recipes are immutable value objects. A recipe contains:
 *
 * <ul>
 *   <li>A title (the name of the recipe)
 *   <li>Optional servings (how many portions the recipe makes)
 *   <li>A list of ingredients (both {@link MeasuredIngredient} and {@link VagueIngredient})
 *   <li>A list of instructions (step-by-step directions)
 *   <li>Recipe-specific conversion rules (e.g., density conversions for ingredients in this recipe)
 * </ul>
 *
 * <p><strong>Design Note:</strong> This class provides getters for recipe data. You must design how
 * to implement recipe transformations (scaling, unit conversion) using the {@link
 * ConversionRegistry} service. Consider where transformation logic should live and how to maintain
 * separation of concerns.
 */
public class Recipe {

  private final String title;
  private final @Nullable Quantity servings;
  private final List<Ingredient> ingredients;
  private final List<Instruction> instructions;
  private final List<ConversionRule> conversionRules;

  /**
   * Constructs a recipe with the given title, servings, ingredients, instructions, and conversion
   * rules.
   *
   * <p>Creates an immutable recipe with defensive copies of all lists.
   *
   * @param title the title of the recipe (must not be null or blank)
   * @param servings the number of servings (may be null if not specified)
   * @param ingredients the list of ingredients (must not be null, but may be empty)
   * @param instructions the list of instructions (must not be null, but may be empty)
   * @param conversionRules recipe-specific conversion rules (must not be null, but may be empty)
   * @throws IllegalArgumentException if title is blank
   */
  public Recipe(
      String title,
      @Nullable Quantity servings,
      List<Ingredient> ingredients,
      List<Instruction> instructions,
      List<ConversionRule> conversionRules) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("title must not be blank");
    }
    Objects.requireNonNull(ingredients, "ingredients must not be null");
    Objects.requireNonNull(instructions, "instructions must not be null");
    Objects.requireNonNull(conversionRules, "conversionRules must not be null");

    this.title = title.trim();
    this.servings = servings;
    this.ingredients = Collections.unmodifiableList(new ArrayList<>(ingredients));
    this.instructions = Collections.unmodifiableList(new ArrayList<>(instructions));
    this.conversionRules = Collections.unmodifiableList(new ArrayList<>(conversionRules));
  }

  /**
   * Returns the title of this recipe.
   *
   * @return the title (never null or blank)
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the servings quantity for this recipe.
   *
   * @return the servings quantity, or null if not specified
   */
  public @Nullable Quantity getServings() {
    return servings;
  }

  /**
   * Returns an unmodifiable list of ingredients in this recipe.
   *
   * @return an unmodifiable list of ingredients (never null, may be empty)
   */
  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  /**
   * Returns an unmodifiable list of instructions in this recipe.
   *
   * @return an unmodifiable list of instructions (never null, may be empty)
   */
  public List<Instruction> getInstructions() {
    return instructions;
  }

  /**
   * Returns an unmodifiable list of recipe-specific conversion rules.
   *
   * @return an unmodifiable list of conversion rules (never null, may be empty)
   */
  public List<ConversionRule> getConversionRules() {
    return conversionRules;
  }

  /**
   * Scales all measured ingredient quantities by the given factor.
   *
   * <p>This method scales all {@link MeasuredIngredient} quantities by the factor. {@link
   * VagueIngredient}s remain unchanged. If servings are present, they are also scaled. Ingredient
   * references in instructions are also scaled.
   *
   * @param factor the scaling factor (must be positive)
   * @param registry the conversion registry to use (must not be null)
   * @return a new Recipe with scaled quantities
   * @throws IllegalArgumentException if factor is not positive
   */
  public Recipe scale(double factor, ConversionRegistry registry) {
    if (factor <= 0.0) {
      throw new IllegalArgumentException("factor must be positive");
    }
    Objects.requireNonNull(registry, "registry must not be null");

    @Nullable Quantity newServings = (servings == null) ? null : scaleQuantity(servings, factor);

    List<Ingredient> newIngredients = new ArrayList<>(ingredients.size());
    for (Ingredient ing : ingredients) {
      if (ing instanceof MeasuredIngredient mi) {
        Quantity scaled = scaleQuantity(mi.getQuantity(), factor);
        newIngredients.add(
            new MeasuredIngredient(mi.getName(), scaled, mi.getPreparation(), mi.getNotes()));
      } else {
        newIngredients.add(ing);
      }
    }

    List<Instruction> newInstructions = new ArrayList<>(instructions.size());
    for (Instruction instr : instructions) {
      List<IngredientRef> newRefs = new ArrayList<>(instr.getIngredientRefs().size());
      for (IngredientRef ref : instr.getIngredientRefs()) {
        Ingredient refIng = ref.ingredient();
        Quantity newQ =
            (refIng instanceof VagueIngredient)
                ? ref.quantity() // keep unchanged
                : scaleQuantity(ref.quantity(), factor);

        newRefs.add(new IngredientRef(refIng, newQ));
      }

      newInstructions.add(new Instruction(instr.getStepNumber(), instr.getText(), newRefs));
    }

    return new Recipe(title, newServings, newIngredients, newInstructions, conversionRules);
  }

  /**
   * Scales the recipe so that the named ingredient reaches the target amount.
   *
   * <p>This method finds the first {@link MeasuredIngredient} matching the ingredient name
   * (case-insensitive), converts its quantity to match the target amount's unit if needed, then
   * calculates the scaling factor and scales the entire recipe accordingly.
   *
   * <p>The registry is enhanced with this recipe's conversion rules at {@link
   * app.cookyourbooks.conversion.ConversionRulePriority#RECIPE} priority for the conversion.
   *
   * @param ingredientName the name of the ingredient to scale to (must not be null or blank)
   * @param targetAmount the target quantity for the ingredient (must not be null)
   * @param registry the conversion registry to use (must not be null)
   * @return a new Recipe scaled so the ingredient reaches the target amount
   * @throws UnsupportedConversionException if the ingredient is not found, or if conversion between
   *     units is not supported
   * @throws IllegalArgumentException if ingredientName is blank
   */
  public Recipe scaleToTarget(
      String ingredientName, Quantity targetAmount, ConversionRegistry registry)
      throws UnsupportedConversionException {
    if (ingredientName == null || ingredientName.isBlank()) {
      throw new IllegalArgumentException("ingredientName must not be blank");
    }
    Objects.requireNonNull(targetAmount, "targetAmount must not be null");
    Objects.requireNonNull(registry, "registry must not be null");

    ConversionRegistry enhanced =
        registry.withRules(conversionRules, ConversionRulePriority.RECIPE);

    // Find first measured ingredient match
    MeasuredIngredient match = null;
    for (Ingredient ing : ingredients) {
      if (ing instanceof MeasuredIngredient mi && mi.getName().equalsIgnoreCase(ingredientName)) {
        match = mi;
        break;
      }
    }
    if (match == null) {
      throw UnsupportedConversionException.ingredientNotFound(ingredientName);
    }

    // Convert current ingredient amount into target unit (if needed) to compute factor
    Quantity current = match.getQuantity();
    Quantity currentInTargetUnit =
        (current.getUnit() == targetAmount.getUnit())
            ? current
            : enhanced.convert(current, targetAmount.getUnit(), match.getName());

    double factor = targetAmount.toDecimal() / currentInTargetUnit.toDecimal();

    // First: scale everything
    Recipe scaled = scale(factor, enhanced);

    // Then: ensure target ingredient ends up exactly in target unit (and equals targetAmount),
    // and convert other measured ingredients to target unit when possible.
    Unit targetUnit = targetAmount.getUnit();

    List<Ingredient> newIngredients = new ArrayList<>(scaled.ingredients.size());
    for (Ingredient ing : scaled.ingredients) {
      if (ing instanceof MeasuredIngredient mi) {
        if (mi.getName().equalsIgnoreCase(ingredientName)) {
          // Force exact target
          newIngredients.add(
              new MeasuredIngredient(
                  mi.getName(), targetAmount, mi.getPreparation(), mi.getNotes()));
        } else {
          Quantity q = mi.getQuantity();
          Quantity convertedOrSame = tryConvert(enhanced, q, targetUnit, mi.getName());
          newIngredients.add(
              new MeasuredIngredient(
                  mi.getName(), convertedOrSame, mi.getPreparation(), mi.getNotes()));
        }
      } else {
        newIngredients.add(ing);
      }
    }

    List<Instruction> newInstructions = new ArrayList<>(scaled.instructions.size());
    for (Instruction instr : scaled.instructions) {
      List<IngredientRef> newRefs = new ArrayList<>(instr.getIngredientRefs().size());
      for (IngredientRef ref : instr.getIngredientRefs()) {
        Ingredient refIng = ref.ingredient();
        Quantity q = ref.quantity();

        if (refIng.getName().equalsIgnoreCase(ingredientName)) {
          // ScaleToTarget: for refs of the target ingredient, convert to target unit when possible,
          // but keep proportional quantity (don’t overwrite with total targetAmount).
          Quantity converted = tryConvert(enhanced, q, targetUnit, refIng.getName());
          newRefs.add(new IngredientRef(refIng, converted));
        } else {
          Quantity converted = tryConvert(enhanced, q, targetUnit, refIng.getName());
          newRefs.add(new IngredientRef(refIng, converted));
        }
      }
      newInstructions.add(new Instruction(instr.getStepNumber(), instr.getText(), newRefs));
    }

    return new Recipe(title, scaled.servings, newIngredients, newInstructions, conversionRules);
  }

  /**
   * Converts all measured ingredient quantities to the target unit.
   *
   * <p>This method converts all {@link MeasuredIngredient} quantities to the target unit. {@link
   * VagueIngredient}s remain unchanged. Servings are never converted. Ingredient references in
   * instructions are also converted.
   *
   * <p>The registry is enhanced with this recipe's conversion rules at {@link
   * app.cookyourbooks.conversion.ConversionRulePriority#RECIPE} priority for the conversion.
   *
   * @param targetUnit the target unit for conversion (must not be null)
   * @param registry the conversion registry to use (must not be null)
   * @return a new Recipe with all quantities converted to the target unit
   * @throws UnsupportedConversionException if any conversion is not supported
   */
  public Recipe convert(Unit targetUnit, ConversionRegistry registry)
      throws UnsupportedConversionException {
    Objects.requireNonNull(targetUnit, "targetUnit must not be null");
    Objects.requireNonNull(registry, "registry must not be null");

    ConversionRegistry enhanced =
        registry.withRules(conversionRules, ConversionRulePriority.RECIPE);

    List<Ingredient> newIngredients = new ArrayList<>(ingredients.size());
    for (Ingredient ing : ingredients) {
      if (ing instanceof MeasuredIngredient mi) {
        Quantity q = mi.getQuantity();
        Quantity converted =
            (q.getUnit() == targetUnit) ? q : enhanced.convert(q, targetUnit, mi.getName());
        newIngredients.add(
            new MeasuredIngredient(mi.getName(), converted, mi.getPreparation(), mi.getNotes()));
      } else {
        newIngredients.add(ing);
      }
    }

    List<Instruction> newInstructions = new ArrayList<>(instructions.size());
    for (Instruction instr : instructions) {
      List<IngredientRef> newRefs = new ArrayList<>(instr.getIngredientRefs().size());
      for (IngredientRef ref : instr.getIngredientRefs()) {
        Quantity q = ref.quantity();
        Quantity converted =
            (q.getUnit() == targetUnit)
                ? q
                : enhanced.convert(q, targetUnit, ref.ingredient().getName());
        newRefs.add(new IngredientRef(ref.ingredient(), converted));
      }
      newInstructions.add(new Instruction(instr.getStepNumber(), instr.getText(), newRefs));
    }

    // Servings are never converted
    return new Recipe(title, servings, newIngredients, newInstructions, conversionRules);
  }

  /**
   * Returns a new quantity scaled by the given factor.
   *
   * <p>This method preserves the structural type of the quantity where possible:
   *
   * <ul>
   *   <li>{@link RangeQuantity} remains a RangeQuantity with both bounds scaled
   *   <li>All other quantity types are converted to an {@link ExactQuantity} using their decimal
   *       value
   * </ul>
   *
   * <p>The original quantity object is never modified.
   *
   * @param q the quantity to scale (must not be null)
   * @param factor the scaling factor (must be positive)
   * @return a new Quantity representing {@code q × factor} in the same unit
   * @throws NullPointerException if q is null
   */
  private static Quantity scaleQuantity(Quantity q, double factor) {
    Objects.requireNonNull(q, "q must not be null");
    if (q instanceof RangeQuantity rq) {
      return new RangeQuantity(rq.getMin() * factor, rq.getMax() * factor, q.getUnit());
    }
    // ExactQuantity / FractionalQuantity -> ExactQuantity after scaling
    return new ExactQuantity(q.toDecimal() * factor, q.getUnit());
  }

  /**
   * Attempts to convert a quantity to a target unit, falling back to the original quantity if
   * conversion is not supported.
   *
   * <p>This method is used in transformations where conversion is optional for non-target
   * ingredients. If the registry cannot perform the conversion, the original quantity is retained.
   *
   * <p>This ensures partial conversions do not cause transformation failure when some ingredients
   * lack applicable conversion rules.
   *
   * @param registry the conversion registry to use
   * @param q the quantity to convert
   * @param targetUnit the desired target unit
   * @param ingredientName the ingredient context for conversion
   * @return the converted quantity if supported, otherwise the original quantity
   */
  private static Quantity tryConvert(
      ConversionRegistry registry, Quantity q, Unit targetUnit, String ingredientName) {
    if (q.getUnit() == targetUnit) {
      return q;
    }
    try {
      return registry.convert(q, targetUnit, ingredientName);
    } catch (UnsupportedConversionException e) {
      // “Convert others when possible” => keep as-is if not convertible.
      return q;
    }
  }

  /**
   * Compares this recipe with the specified object for equality.
   *
   * <p>Two Recipe objects are equal if they have the same title, servings, ingredients (in order),
   * instructions (in order), and conversion rules (in order).
   *
   * @param o the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) return true;
    if (!(o instanceof Recipe that)) return false;
    return title.equals(that.title)
        && Objects.equals(servings, that.servings)
        && ingredients.equals(that.ingredients)
        && instructions.equals(that.instructions)
        && conversionRules.equals(that.conversionRules);
  }

  /**
   * Returns a hash code value for this recipe.
   *
   * @return a hash code value
   */
  @Override
  public int hashCode() {
    return Objects.hash(title, servings, ingredients, instructions, conversionRules);
  }
}
