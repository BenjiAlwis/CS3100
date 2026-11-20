package app.cookyourbooks.conversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import app.cookyourbooks.exception.UnsupportedConversionException;
import app.cookyourbooks.model.Quantity;
import app.cookyourbooks.model.Unit;

/**
 * An implementation of {@link ConversionRegistry} using an immutable layered structure.
 *
 * <p>LayeredConversionRegistry organizes conversion rules by priority level ({@code HOUSE}, {@code
 * RECIPE}, {@code STANDARD}) and provides efficient rule lookup during conversions. Each call to
 * {@link #withRule} or {@link #withRules} creates a new layer on top of the existing registry.
 *
 * <p>Rule lookup behavior:
 *
 * <ol>
 *   <li>Search rules in priority order: {@code HOUSE} → {@code RECIPE} → {@code STANDARD}
 *   <li>At each priority level, check ingredient-specific rules before generic rules
 *   <li>Among rules of equal specificity at the same priority, prefer rules added earlier
 * </ol>
 *
 * <p>The "layered" name reflects the immutable layering pattern: each {@code withRule()} call
 * creates a new layer on top of the previous registry state.
 */
public class LayeredConversionRegistry implements ConversionRegistry {

  private final List<ConversionRule> houseRules;
  private final List<ConversionRule> recipeRules;
  private final List<ConversionRule> standardRules;

  /**
   * Creates an empty registry with no rules.
   *
   * <p>To get a registry with standard conversions, use:
   *
   * <pre>{@code
   * new LayeredConversionRegistry()
   *     .withRules(StandardConversions.getAllRules(), ConversionRulePriority.STANDARD)
   * }</pre>
   */
  public LayeredConversionRegistry() {
    // Empty constructor - creates a registry with no rules
    this(List.of(), List.of(), List.of());
  }

  /**
   * Internal constructor used to create a new registry layer.
   *
   * <p>All rule lists are defensively copied to preserve immutability. The resulting registry
   * instance shares no mutable state with the input lists.
   *
   * @param house the list of HOUSE-priority rules
   * @param recipe the list of RECIPE-priority rules
   * @param standard the list of STANDARD-priority rules
   */
  private LayeredConversionRegistry(
      List<ConversionRule> house, List<ConversionRule> recipe, List<ConversionRule> standard) {
    this.houseRules = List.copyOf(house);
    this.recipeRules = List.copyOf(recipe);
    this.standardRules = List.copyOf(standard);
  }

  /**
   * Converts a quantity to the target unit without ingredient context.
   *
   * <p>This method only matches generic rules (rules where {@code ingredientName} is null). Rules
   * are searched in priority order: {@code HOUSE} → {@code RECIPE} → {@code STANDARD}.
   *
   * @param quantity the quantity to convert (must not be null)
   * @param targetUnit the target unit for conversion (must not be null)
   * @return the converted quantity with the target unit
   * @throws UnsupportedConversionException if no applicable rule exists for this conversion
   */
  @Override
  public Quantity convert(Quantity quantity, Unit targetUnit)
      throws UnsupportedConversionException {
    Objects.requireNonNull(quantity, "quantity must not be null");
    Objects.requireNonNull(targetUnit, "targetUnit must not be null");

    Unit from = quantity.getUnit();
    if (from == targetUnit) {
      return quantity;
    }

    ConversionRule rule = findRule(from, targetUnit, null, /*genericOnly*/ true);
    if (rule == null) {
      throw UnsupportedConversionException.forUnits(from, targetUnit);
    }
    return rule.convert(quantity);
  }

  /**
   * Converts a quantity to the target unit with ingredient context.
   *
   * <p>This method can match both ingredient-specific rules and generic rules. At each priority
   * level, ingredient-specific rules are checked before generic rules.
   *
   * <p>Rules are searched in priority order: {@code HOUSE} → {@code RECIPE} → {@code STANDARD}.
   *
   * @param quantity the quantity to convert (must not be null)
   * @param targetUnit the target unit for conversion (must not be null)
   * @param ingredientName the ingredient name for context (may be null for generic conversions)
   * @return the converted quantity with the target unit
   * @throws UnsupportedConversionException if no applicable rule exists for this conversion
   */
  @Override
  public Quantity convert(Quantity quantity, Unit targetUnit, @Nullable String ingredientName)
      throws UnsupportedConversionException {

    Objects.requireNonNull(quantity, "quantity must not be null");
    Objects.requireNonNull(targetUnit, "targetUnit must not be null");

    Unit from = quantity.getUnit();
    if (from == targetUnit) {
      return quantity;
    }

    ConversionRule rule = findRule(from, targetUnit, ingredientName, /*genericOnly*/ false);
    if (rule == null) {
      if (ingredientName == null) {
        throw UnsupportedConversionException.forUnits(from, targetUnit);
      }
      throw UnsupportedConversionException.forIngredient(from, targetUnit, ingredientName);
    }
    return rule.convert(quantity);
  }

  /**
   * Returns a new registry with the given rule added at the specified priority level.
   *
   * <p>The original registry is not modified (registries are immutable).
   *
   * @param rule the rule to add (must not be null)
   * @param priority the priority level for this rule (must not be null)
   * @return a new LayeredConversionRegistry containing all existing rules plus the new rule
   */
  @Override
  public ConversionRegistry withRule(ConversionRule rule, ConversionRulePriority priority) {
    Objects.requireNonNull(rule, "rule must not be null");
    Objects.requireNonNull(priority, "priority must not be null");
    return withRules(List.of(rule), priority);
  }

  /**
   * Returns a new registry with the given rules added at the specified priority level.
   *
   * <p>The original registry is not modified (registries are immutable).
   *
   * <p>Rules in the collection maintain their relative order (first in collection = added earlier).
   * Rules added via this call are considered "later" than rules already in the registry at the same
   * priority level.
   *
   * @param rules the rules to add (must not be null, but may be empty)
   * @param priority the priority level for these rules (must not be null)
   * @return a new LayeredConversionRegistry containing all existing rules plus the new rules
   */
  @Override
  public ConversionRegistry withRules(
      Collection<ConversionRule> rules, ConversionRulePriority priority) {
    Objects.requireNonNull(rules, "rules must not be null");
    Objects.requireNonNull(priority, "priority must not be null");

    List<ConversionRule> toAdd = new ArrayList<>(rules);
    // defensive: ensure no null rules
    for (ConversionRule r : toAdd) {
      Objects.requireNonNull(r, "rules must not contain null");
    }

    List<ConversionRule> newHouse = new ArrayList<>(houseRules);
    List<ConversionRule> newRecipe = new ArrayList<>(recipeRules);
    List<ConversionRule> newStandard = new ArrayList<>(standardRules);

    switch (priority) {
      case HOUSE -> newHouse.addAll(toAdd);
      case RECIPE -> newRecipe.addAll(toAdd);
      case STANDARD -> newStandard.addAll(toAdd);
    }

    return new LayeredConversionRegistry(newHouse, newRecipe, newStandard);
  }

  /**
   * Finds the highest-priority conversion rule matching the given criteria.
   *
   * <p>Rules are searched in priority order: HOUSE → RECIPE → STANDARD. Within a priority level:
   *
   * <ul>
   *   <li>If {@code genericOnly} is false and an ingredient name is provided, ingredient-specific
   *       rules are checked first.
   *   <li>Generic rules (ingredientName == null) are then checked.
   * </ul>
   *
   * @param from the source unit
   * @param to the target unit
   * @param ingredientName the ingredient context, or null if none
   * @param genericOnly if true, only generic rules are considered
   * @return the first matching rule, or null if none exists
   */
  private @Nullable ConversionRule findRule(
      Unit from, Unit to, @Nullable String ingredientName, boolean genericOnly) {

    // Priority order: HOUSE -> RECIPE -> STANDARD
    ConversionRule r;

    r = findRuleInLevel(houseRules, from, to, ingredientName, genericOnly);
    if (r != null) return r;

    r = findRuleInLevel(recipeRules, from, to, ingredientName, genericOnly);
    if (r != null) return r;

    return findRuleInLevel(standardRules, from, to, ingredientName, genericOnly);
  }

  /**
   * Searches a single priority level for a matching conversion rule.
   *
   * <p>This method enforces specificity ordering within the level:
   *
   * <ol>
   *   <li>Ingredient-specific rules (if allowed)
   *   <li>Generic rules
   * </ol>
   *
   * <p>The list order is respected — earlier-added rules have precedence.
   *
   * @param rules the rules at a specific priority level
   * @param from the source unit
   * @param to the target unit
   * @param ingredientName the ingredient context, or null
   * @param genericOnly if true, ingredient-specific rules are ignored
   * @return the first matching rule in this level, or null
   */
  private static @Nullable ConversionRule findRuleInLevel(
      List<ConversionRule> rules,
      Unit from,
      Unit to,
      @Nullable String ingredientName,
      boolean genericOnly) {

    if (!genericOnly && ingredientName != null) {
      for (ConversionRule rule : rules) {
        if (rule.ingredientName() != null && rule.canConvert(from, to, ingredientName)) {
          return rule;
        }
      }
    }

    for (ConversionRule rule : rules) {
      if (rule.ingredientName() == null && rule.canConvert(from, to, null)) {
        return rule;
      }
    }

    return null;
  }
}
