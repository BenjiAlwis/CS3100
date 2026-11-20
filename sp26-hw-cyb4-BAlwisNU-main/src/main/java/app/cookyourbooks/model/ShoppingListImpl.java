package app.cookyourbooks.model;

import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Implementation of {@link ShoppingList}.
 *
 * <p>This is a stub for the handout. Replace this with your implementation.
 */
public final class ShoppingListImpl implements ShoppingList {

  private final @NonNull List<ShoppingItem> items;
  private final @NonNull List<String> uncountableItems;

  /**
   * Constructs a shopping list with the given measurable items and uncountable ingredient names.
   *
   * @param items the list of shopping items (must not be null, may be empty)
   * @param uncountableItems the names of vague/uncountable ingredients (must not be null, may be
   *     empty)
   */
  public ShoppingListImpl(
      @NonNull List<ShoppingItem> items, @NonNull List<String> uncountableItems) {
    this.items = Objects.requireNonNull(List.copyOf(items), "items must not be null");
    this.uncountableItems =
        Objects.requireNonNull(List.copyOf(uncountableItems), "uncountableItems must not be null");
  }

  @Override
  public @NonNull List<ShoppingItem> getItems() {
    return items;
  }

  @Override
  public @NonNull List<String> getUncountableItems() {
    return uncountableItems;
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ShoppingListImpl that)) {
      return false;
    }
    return items.equals(that.items) && uncountableItems.equals(that.uncountableItems);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(items, uncountableItems);
  }

  @Override
  public String toString() {
    return "ShoppingList{items=" + items + ", uncountable=" + uncountableItems + "}";
  }
}
