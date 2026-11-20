package app.cookyourbooks.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * Represents a single step in a recipe.
 *
 * <p>Instructions are immutable. When recipe transformations occur (scaling, converting), new
 * {@code Instruction} objects are created with updated {@link IngredientRef}s while preserving the
 * step number and text.
 *
 * <p>Each instruction has:
 *
 * <ul>
 *   <li>A step number (positive integer, typically sequential within a recipe)
 *   <li>Instruction text describing what to do
 *   <li>A list of ingredient references indicating which ingredients are used in this step and in
 *       what quantities
 * </ul>
 *
 * <p>Example: Step 2 might be "Mix flour and sugar" with references to the flour and sugar
 * ingredients.
 */
public class Instruction {

  private final int stepNumber;
  private final String text;
  private final List<IngredientRef> ingredientRefs;

  /**
   * Constructs an instruction with the given step number, text, and ingredient references.
   *
   * <p>Creates an immutable instruction with a defensive copy of the ingredient references list.
   *
   * @param stepNumber the step number (must be positive, greater than 0)
   * @param text the instruction text (must not be null or blank)
   * @param ingredientRefs the list of ingredient references used in this step (must not be null,
   *     but may be empty)
   * @throws IllegalArgumentException if stepNumber is not positive or text is blank
   */
  public Instruction(int stepNumber, String text, List<IngredientRef> ingredientRefs) {
    if (stepNumber <= 0) {
      throw new IllegalArgumentException("stepNumber must be positive");
    }
    if (text == null || text.isBlank()) {
      throw new IllegalArgumentException("text must not be blank");
    }
    Objects.requireNonNull(ingredientRefs, "ingredientRefs must not be null");

    this.stepNumber = stepNumber;
    this.text = text.trim();
    this.ingredientRefs = Collections.unmodifiableList(new ArrayList<>(ingredientRefs));
  }

  /**
   * Returns the step number of this instruction.
   *
   * @return the step number (always positive)
   */
  public int getStepNumber() {
    return stepNumber;
  }

  /**
   * Returns the instruction text.
   *
   * @return the instruction text (never null or blank)
   */
  public String getText() {
    return text;
  }

  /**
   * Returns an unmodifiable list of ingredient references used in this step.
   *
   * @return an unmodifiable list of ingredient references (never null, may be empty)
   */
  public List<IngredientRef> getIngredientRefs() {
    return ingredientRefs;
  }

  /**
   * Returns a formatted string representation of this instruction.
   *
   * <p>Format: "{stepNumber}. {text}"
   *
   * <p>Example: "2. Mix flour and sugar"
   *
   * @return a formatted string representation
   */
  @Override
  public String toString() {
    return stepNumber + ". " + text;
  }

  /**
   * Compares this instruction with the specified object for equality.
   *
   * <p>Two Instruction objects are equal if they have the same step number, text, and ingredient
   * references (in the same order).
   *
   * @param o the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) return true;
    if (!(o instanceof Instruction that)) return false;
    return stepNumber == that.stepNumber
        && text.equals(that.text)
        && ingredientRefs.equals(that.ingredientRefs);
  }

  /**
   * Returns a hash code value for this instruction.
   *
   * @return a hash code value
   */
  @Override
  public int hashCode() {
    return Objects.hash(stepNumber, text, ingredientRefs);
  }
}
