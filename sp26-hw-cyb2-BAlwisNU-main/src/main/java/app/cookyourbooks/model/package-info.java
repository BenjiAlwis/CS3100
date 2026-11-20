/**
 * Core domain model classes for the CookYourBooks application.
 *
 * <p>This package contains the fundamental entities of the recipe domain:
 *
 * <ul>
 *   <li>{@link app.cookyourbooks.model.Unit} - Units of measurement (cups, grams, etc.)
 *   <li>{@link app.cookyourbooks.model.UnitSystem} - Measurement systems (Imperial, Metric, House)
 *   <li>{@link app.cookyourbooks.model.UnitDimension} - Physical dimensions (Weight, Volume, Count)
 *   <li>{@link app.cookyourbooks.model.Quantity} - Abstract base for quantities
 *   <li>{@link app.cookyourbooks.model.ExactQuantity} - Precise decimal quantities
 *   <li>{@link app.cookyourbooks.model.FractionalQuantity} - Fractional quantities (e.g., 1/2 cup)
 *   <li>{@link app.cookyourbooks.model.RangeQuantity} - Range quantities (e.g., 2-3 cups)
 *   <li>{@link app.cookyourbooks.model.Ingredient} - Abstract base for ingredients
 *   <li>{@link app.cookyourbooks.model.MeasuredIngredient} - Ingredients with precise measurements
 *   <li>{@link app.cookyourbooks.model.VagueIngredient} - Ingredients without precise measurements
 *   <li>{@link app.cookyourbooks.model.IngredientRef} - Reference to an ingredient with quantity
 *   <li>{@link app.cookyourbooks.model.Instruction} - A single step in a recipe
 *   <li>{@link app.cookyourbooks.model.Recipe} - A complete recipe with ingredients, instructions,
 *       and conversion rules
 * </ul>
 */
@org.jspecify.annotations.NullMarked
package app.cookyourbooks.model;
