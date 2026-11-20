package app.cookyourbooks.domain;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Abstract base class for all recipe ingredients.
 *
 * <p>Every ingredient has a name and may optionally include a preparation and notes. Subclasses
 * determine how the ingredient is measured or described.
 */
public abstract class Ingredient {

  private final @NonNull String name;
  private final @Nullable String preparation;
  private final @Nullable String notes;

  /**
   * Constructs an ingredient with the given name, preparation, and notes.
   *
   * <p>Preconditions:
   *
   * <ul>
   *   <li>{@code name} must be non-null (enforced by NullAway) and not blank
   *   <li>{@code preparation} may be null
   *   <li>{@code notes} may be null
   * </ul>
   *
   * <p>All non-null strings are trimmed of leading and trailing whitespace.
   *
   * @param name the ingredient name (non-null; must not be blank)
   * @param preparation optional preparation text (may be null; trimmed if non-null)
   * @param notes optional notes text (may be null; trimmed if non-null)
   * @throws IllegalArgumentException if {@code name} is blank (null is prevented by NullAway)
   */
  protected Ingredient(@NonNull String name, @Nullable String preparation, @Nullable String notes) {
    String trimmedName = name.trim();
    if (trimmedName.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }

    this.name = trimmedName;
    this.preparation = preparation == null ? null : preparation.trim();
    this.notes = notes == null ? null : notes.trim();
  }

  /**
   * Returns the ingredient name.
   *
   * @return the non-null name of the ingredient
   */
  public final @NonNull String getName() {
    return name;
  }

  /**
   * Returns the preparation for this ingredient, if any.
   *
   * @return the preparation, or {@code null} if none was provided
   */
  public final @Nullable String getPreparation() {
    return preparation;
  }

  /**
   * Returns the notes for this ingredient, if any.
   *
   * @return the notes, or {@code null} if none were provided
   */
  public final @Nullable String getNotes() {
    return notes;
  }

  /**
   * Returns a human-readable string representation of this ingredient.
   *
   * @return the formatted string representation
   */
  @Override
  public abstract @NonNull String toString();
}
