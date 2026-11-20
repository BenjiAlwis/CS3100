package app.cookyourbooks.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import app.cookyourbooks.model.ExactQuantity;
import app.cookyourbooks.model.FractionalQuantity;
import app.cookyourbooks.model.Ingredient;
import app.cookyourbooks.model.MeasuredIngredient;
import app.cookyourbooks.model.RangeQuantity;
import app.cookyourbooks.model.Unit;
import app.cookyourbooks.model.VagueIngredient;

/**
 * Parses a single ingredient line into a domain {@link Ingredient}.
 *
 * <p>Supports exactly the formats required by the assignment specification.
 */
final class IngredientParser {

  IngredientParser() {}

  /**
   * Parses a single ingredient line into an {@link Ingredient}.
   *
   * @param rawLine the raw ingredient text line
   * @return a {@link MeasuredIngredient} or {@link VagueIngredient}
   * @throws ParseException if the line cannot be parsed
   */
  Ingredient parseLine(@NonNull String rawLine) throws ParseException {
    String line = rawLine.trim();
    if (line.isEmpty()) {
      throw new ParseException("Ingredient line is blank");
    }

    // Split preparation after first comma.
    String main = line;
    @Nullable String preparation = null;
    int comma = line.indexOf(',');
    if (comma >= 0) {
      main = line.substring(0, comma).trim();
      preparation = line.substring(comma + 1).trim();
      if (preparation.isEmpty()) {
        preparation = null;
      }
    }

    // Vague ingredient rules: only when the line does NOT start with number or a/an.
    String lowerMain = main.toLowerCase(Locale.ROOT);
    boolean startsMeasured =
        lowerMain.matches("^\\s*(\\d.*|a\\b.*|an\\b.*)$"); // begins with digit or a/an
    if (!startsMeasured) {
      // "salt to taste" -> VagueIngredient(name="salt", description="to taste")
      if (lowerMain.endsWith(" to taste")) {
        String name = main.substring(0, main.length() - "to taste".length()).trim();
        if (name.endsWith(",")) {
          name = name.substring(0, name.length() - 1).trim();
        }
        if (name.isEmpty()) {
          throw new ParseException("Invalid 'to taste' ingredient: " + rawLine);
        }
        return new VagueIngredient(name, "to taste", null, null);
      }
      // "fresh herbs" -> VagueIngredient(name="fresh herbs")
      return new VagueIngredient(main, null, preparation, null);
    }

    List<String> tokens = tokenize(main);
    int i = 0;

    // ---------- Parse quantity ----------
    QuantityParse qty = parseQuantity(tokens, i);
    i = qty.nextIndex();

    // ---------- Parse unit (two-word first) ----------
    Unit unit = Unit.WHOLE;
    int unitTokensConsumed = 0;
    if (i < tokens.size()) {
      // Two-word unit
      if (i + 1 < tokens.size()) {
        String two = tokens.get(i) + " " + tokens.get(i + 1);
        Optional<Unit> u2 = Unit.fromString(two);
        if (u2.isPresent()) {
          unit = u2.get();
          unitTokensConsumed = 2;
        }
      }
      if (unitTokensConsumed == 0) {
        Optional<Unit> u1 = Unit.fromString(tokens.get(i));
        if (u1.isPresent()) {
          unit = u1.get();
          unitTokensConsumed = 1;
        }
      }
    }

    // If unit recognized, consume it; else unit is WHOLE and name includes remaining tokens.
    if (unitTokensConsumed > 0) {
      i += unitTokensConsumed;

      // Strip optional connector "of"
      if (i < tokens.size() && tokens.get(i).equalsIgnoreCase("of")) {
        i++;
      }
    } else {
      unit = Unit.WHOLE;
    }

    // ---------- Name = remaining tokens ----------
    String name = joinTokens(tokens, i);
    if (name.isEmpty()) {
      throw new ParseException("Missing ingredient name: " + rawLine);
    }

    // Build quantity object using the final unit.
    return new MeasuredIngredient(name, qty.toQuantity(unit), preparation, null);
  }

  /**
   * Tokenizes a string by splitting on whitespace.
   *
   * @param s the string to tokenize
   * @return list of non-empty tokens
   */
  private static List<String> tokenize(String s) {
    String trimmed = s.trim();
    List<String> out = new ArrayList<>();
    int n = trimmed.length();
    int i = 0;

    while (i < n) {
      while (i < n && Character.isWhitespace(trimmed.charAt(i))) {
        i++;
      }
      if (i >= n) {
        break;
      }

      int start = i;
      while (i < n && !Character.isWhitespace(trimmed.charAt(i))) {
        i++;
      }
      out.add(trimmed.substring(start, i));
    }

    return out;
  }

  /**
   * Joins tokens from the given start index with spaces.
   *
   * @param tokens the list of tokens
   * @param start the starting index
   * @return joined string
   */
  private static String joinTokens(List<String> tokens, int start) {
    if (start >= tokens.size()) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = start; i < tokens.size(); i++) {
      if (sb.length() > 0) {
        sb.append(' ');
      }
      sb.append(tokens.get(i));
    }
    return sb.toString().trim();
  }

  /**
   * Parses quantity from tokens starting at the given index.
   *
   * <p>Supports: exact (2), fractional (1/2, 2 1/2), range (2-3), and articles (a/an).
   *
   * @param tokens the list of tokens
   * @param start the starting index
   * @return a {@link QuantityParse} with the parsed quantity and next token index
   * @throws ParseException if the quantity cannot be parsed
   */
  private static QuantityParse parseQuantity(List<String> tokens, int start) throws ParseException {
    if (start >= tokens.size()) {
      throw new ParseException("Missing quantity");
    }
    String t0 = tokens.get(start);

    // a/an -> 1
    if (t0.equalsIgnoreCase("a") || t0.equalsIgnoreCase("an")) {
      return QuantityParse.exact(1.0, start + 1);
    }

    // range: 2-3 or 1-2 (ints required by spec; accept decimals safely)
    if (t0.matches("^\\d+(?:\\.\\d+)?-\\d+(?:\\.\\d+)?$")) {
      String[] parts = t0.split("-", 2);
      double min = parseDouble(parts[0], "range min");
      double max = parseDouble(parts[1], "range max");
      return QuantityParse.range(min, max, start + 1);
    }

    // fraction: 1/2
    if (t0.matches("^\\d+/\\d+$")) {
      String[] parts = t0.split("/", 2);
      int num = parseInt(parts[0], "fraction numerator");
      int den = parseInt(parts[1], "fraction denominator");
      return QuantityParse.fractional(0, num, den, start + 1);
    }

    // decimal or integer
    if (t0.matches("^\\d+(?:\\.\\d+)?$")) {
      // mixed fraction: 2 1/2 (only if first token is integer without dot)
      if (!t0.contains(".")
          && start + 1 < tokens.size()
          && tokens.get(start + 1).matches("^\\d+/\\d+$")) {
        int whole = parseInt(t0, "mixed whole");
        String[] parts = tokens.get(start + 1).split("/", 2);
        int num = parseInt(parts[0], "mixed numerator");
        int den = parseInt(parts[1], "mixed denominator");
        return QuantityParse.fractional(whole, num, den, start + 2);
      }
      double val = parseDouble(t0, "quantity");
      return QuantityParse.exact(val, start + 1);
    }

    throw new ParseException("Unrecognized quantity token: " + t0);
  }

  /**
   * Parses an integer from a string.
   *
   * @param s the string to parse
   * @param what description of what is being parsed (for error messages)
   * @return the parsed integer
   * @throws ParseException if parsing fails
   */
  private static int parseInt(String s, String what) throws ParseException {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      throw new ParseException("Invalid " + what + ": " + s, e);
    }
  }

  /**
   * Parses a double from a string.
   *
   * @param s the string to parse
   * @param what description of what is being parsed (for error messages)
   * @return the parsed double
   * @throws ParseException if parsing fails
   */
  private static double parseDouble(String s, String what) throws ParseException {
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      throw new ParseException("Invalid " + what + ": " + s, e);
    }
  }

  /** Internal representation of a parsed quantity before conversion to domain model. */
  private sealed interface QuantityParse {
    int nextIndex();

    app.cookyourbooks.model.Quantity toQuantity(Unit unit);

    static QuantityParse exact(double value, int nextIndex) {
      return new Exact(value, nextIndex);
    }

    static QuantityParse fractional(int whole, int num, int den, int nextIndex) {
      return new Fractional(whole, num, den, nextIndex);
    }

    static QuantityParse range(double min, double max, int nextIndex) {
      return new Range(min, max, nextIndex);
    }
  }

  private record Exact(double value, int nextIndex) implements QuantityParse {
    @Override
    public app.cookyourbooks.model.Quantity toQuantity(Unit unit) {
      return new ExactQuantity(value, unit);
    }
  }

  private record Fractional(int whole, int num, int den, int nextIndex) implements QuantityParse {
    @Override
    public app.cookyourbooks.model.Quantity toQuantity(Unit unit) {
      return new FractionalQuantity(whole, num, den, unit);
    }
  }

  private record Range(double min, double max, int nextIndex) implements QuantityParse {
    @Override
    public app.cookyourbooks.model.Quantity toQuantity(Unit unit) {
      return new RangeQuantity(min, max, unit);
    }
  }
}
