package app.cookyourbooks.services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import app.cookyourbooks.model.Ingredient;
import app.cookyourbooks.model.Instruction;
import app.cookyourbooks.model.Recipe;
import app.cookyourbooks.model.Servings;

/**
 * Parses plain text recipes into {@link Recipe} according to the assignment specification.
 *
 * <p>Responsibilities: title, optional servings, ingredient lines, instruction lines.
 * IngredientRefs are left empty; conversionRules are empty for text-imported recipes.
 */
final class RecipeTextParser {

  private static final Pattern SERVINGS =
      Pattern.compile(
          "^\\s*(makes|serves)\\s*:??\\s*(\\d+)\\s*(.*)\\s*$", Pattern.CASE_INSENSITIVE);

  private static final Pattern ING_HEADER =
      Pattern.compile("^\\s*ingredients\\s*:??\\s*$", Pattern.CASE_INSENSITIVE);

  private static final Pattern INST_HEADER =
      Pattern.compile("^\\s*(instructions|directions|steps)\\s*:??\\s*$", Pattern.CASE_INSENSITIVE);

  private static final Pattern INST_PREFIX = Pattern.compile("^\\s*\\d+\\s*[\\.|\\)]\\s*");

  private final IngredientParser ingredientParser = new IngredientParser();

  RecipeTextParser() {}

  /**
   * Parses recipe text into a {@link Recipe}.
   *
   * <p>Extracts title (first non-blank line), optional servings, ingredients, and instructions.
   *
   * @param recipeText the plain text recipe
   * @return a parsed {@link Recipe}
   * @throws ParseException if the text cannot be parsed
   */
  Recipe parse(@NonNull String recipeText) throws ParseException {
    String text = recipeText == null ? "" : recipeText;
    String[] lines = text.split("\\R", -1);

    String title = firstNonBlank(lines);
    if (title == null) {
      throw new ParseException("Recipe text is empty or blank");
    }

    @Nullable Servings servings = null;
    List<Ingredient> ingredients = new ArrayList<>();
    List<Instruction> instructions = new ArrayList<>();

    enum State {
      NONE,
      INGREDIENTS,
      INSTRUCTIONS
    }
    State state = State.NONE;
    boolean titleConsumed = false;

    for (String raw : lines) {
      String line = raw.trim();
      if (line.isEmpty()) {
        continue;
      }

      // Skip only the first encountered title line.
      if (!titleConsumed && line.equals(title)) {
        titleConsumed = true;
        continue;
      }

      // headers
      if (ING_HEADER.matcher(line).matches()) {
        state = State.INGREDIENTS;
        continue;
      }
      if (INST_HEADER.matcher(line).matches()) {
        state = State.INSTRUCTIONS;
        continue;
      }

      // servings can appear anywhere outside strict spec; safe to detect in NONE/ING too.

      Matcher m = SERVINGS.matcher(line);
      if (m.matches()) {
        int amount = Integer.parseInt(m.group(2));

        // SPEC: ignore non-positive servings (treat as "no servings info")
        if (amount <= 0) {
          continue;
        }

        String desc = m.group(3) == null ? "" : m.group(3).trim();
        servings = desc.isEmpty() ? new Servings(amount) : new Servings(amount, desc);
        continue;
      }

      if (state == State.INGREDIENTS) {
        ingredients.add(ingredientParser.parseLine(line));
      } else if (state == State.INSTRUCTIONS) {
        String stripped = INST_PREFIX.matcher(raw).replaceFirst("").trim();
        if (!stripped.isEmpty()) {
          instructions.add(new Instruction(instructions.size() + 1, stripped, List.of()));
        }
      }
    }

    return new Recipe(
        title, servings, List.copyOf(ingredients), List.copyOf(instructions), List.of());
  }

  /**
   * Finds the first non-blank line in an array.
   *
   * @param lines the array of lines
   * @return the first non-blank line, or null if all lines are blank
   */
  private static @Nullable String firstNonBlank(String[] lines) {
    for (String l : lines) {
      if (l != null && !l.trim().isEmpty()) {
        return l.trim();
      }
    }
    return null;
  }
}
