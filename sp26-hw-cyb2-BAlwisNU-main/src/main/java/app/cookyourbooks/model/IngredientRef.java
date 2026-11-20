package app.cookyourbooks.model;

import java.util.Objects;

/**
 * A record representing a reference to an ingredient within an instruction, with its quantity.
 *
 * <p>IngredientRef is used within {@link Instruction} to track which ingredients are used at each
 * step and in what quantity. The referenced ingredient does not need to be the same object instance
 * as one in {@link Recipe#getIngredients()}—it just needs to represent the same ingredient
 * (typically matched by {@code equals()}).
 *
 * <p>The quantity in an IngredientRef may differ from the ingredient's total quantity in the
 * recipe. For example, an instruction might say "add half the flour now", where the IngredientRef
 * quantity is half of the total flour quantity in the recipe.
 *
 * @param ingredient the referenced ingredient (never null)
 * @param quantity the quantity used in this instruction step (never null)
 */
public record IngredientRef(Ingredient ingredient, Quantity quantity) {

  /** Compact constructor that validates the record components. */
  public IngredientRef {
    Objects.requireNonNull(ingredient, "ingredient must not be null");
    Objects.requireNonNull(quantity, "quantity must not be null");
  }
}
