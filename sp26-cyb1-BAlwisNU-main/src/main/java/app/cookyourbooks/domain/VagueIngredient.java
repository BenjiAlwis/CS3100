package app.cookyourbooks.domain;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * An {@link Ingredient} that does not have a precise quantity.
 *
 * <p>Vague ingredients may include an optional description (such as "to taste") and/or an optional
 * preparation (such as "diced"). Notes are part of the ingredient's identity but are not included
 * in the string representation.
 */
public final class VagueIngredient extends Ingredient {

  private final @Nullable String description;

  /**
   * Constructs a vague ingredient.
   *
   * <p>Preconditions:
   *
   * <ul>
   *   <li>{@code name} must be non-null (enforced by NullAway) and not blank
   *   <li>{@code description} may be null
   *   <li>{@code preparation} may be null
   *   <li>{@code notes} may be null
   * </ul>
   *
   * @param name the ingredient name (non-null; must not be blank)
   * @param description optional description such as "to taste" (may be null; trimmed if non-null)
   * @param preparation optional preparation such as "diced" (may be null; trimmed if non-null)
   * @param notes optional notes for the recipe (may be null; trimmed if non-null)
   * @throws IllegalArgumentException if {@code name} is blank (null is prevented by NullAway)
   */
  public VagueIngredient(
      @NonNull String name,
      @Nullable String description,
      @Nullable String preparation,
      @Nullable String notes) {
    super(name, preparation, notes);
    this.description = description == null ? null : description.trim();
  }

  /**
   * Returns the optional description of this ingredient.
   *
   * @return the description, or {@code null} if none was provided
   */
  public @Nullable String getDescription() {
    return description;
  }

  /**
   * Returns a human-readable string representation of this vague ingredient.
   *
   * <p>Formatting rules:
   *
   * <ul>
   *   <li>Start with the ingredient name
   *   <li>If description is non-null and non-empty, append {@code " (description)"}
   *   <li>If preparation is non-null and non-empty, append {@code ", preparation"}
   * </ul>
   *
   * <p>Notes are not included in the returned string.
   *
   * @return the formatted string representation
   */
  @Override
  public @NonNull String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getName());

    if (description != null && !description.isEmpty()) {
      sb.append(" (").append(description).append(")");
    }

    String prep = getPreparation();
    if (prep != null && !prep.isEmpty()) {
      sb.append(", ").append(prep);
    }

    @NonNull String result = sb.toString();
    return result;
  }
}
