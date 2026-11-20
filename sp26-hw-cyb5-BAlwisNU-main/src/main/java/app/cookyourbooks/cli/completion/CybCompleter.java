package app.cookyourbooks.cli.completion;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import app.cookyourbooks.CybLibrary;
import app.cookyourbooks.cli.QuotedTokenizer;
import app.cookyourbooks.model.Recipe;
import app.cookyourbooks.model.RecipeCollection;
import app.cookyourbooks.model.Unit;

/**
 * JLine tab-completion provider for CookYourBooks CLI commands.
 *
 * <p>Suggestions are generated from command grammar, collection and recipe names in the current
 * library, and active cook-mode state.
 */
public final class CybCompleter implements Completer {

  private static final List<String> COMMANDS =
      List.of(
          "help",
          "quit",
          "exit",
          "collections",
          "collection create",
          "recipes",
          "conversions",
          "conversion add",
          "conversion remove",
          "show",
          "search",
          "import json",
          "delete",
          "scale",
          "convert",
          "shopping-list",
          "export",
          "cook");

  private final CybLibrary library;
  private final CookModeHolder cookModeHolder;

  /**
   * Creates a completer backed by the current library and cook-mode state.
   *
   * @param library the library used to source recipe and collection suggestions
   * @param cookModeHolder the holder tracking whether cook mode is active
   */
  public CybCompleter(CybLibrary library, CookModeHolder cookModeHolder) {
    this.library = library;
    this.cookModeHolder = cookModeHolder;
  }

  /**
   * Populates completion candidates for the current input line.
   *
   * @param reader the active JLine reader
   * @param line the parsed line state at the current cursor position
   * @param candidates the mutable list of candidate completions to append to
   */
  @Override
  public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
    String raw = line.line();
    String lower = raw.toLowerCase(Locale.ROOT);

    if (cookModeHolder.isInCookMode()) {
      completeCookMode(raw, candidates);
      return;
    }

    List<String> tokens = QuotedTokenizer.tokenize(raw);
    boolean trailingSpace = raw.endsWith(" ");

    if (tokens.isEmpty() || (tokens.size() == 1 && !trailingSpace)) {
      String prefix = tokens.isEmpty() ? "" : tokens.get(0).toLowerCase(Locale.ROOT);
      addMatching(candidates, COMMANDS, prefix);
      return;
    }

    // Recipes Commands
    if (lower.startsWith("show ")
        || lower.startsWith("scale ")
        || lower.startsWith("delete ")
        || lower.startsWith("cook ")
        || lower.startsWith("shopping-list ")) {
      completeRecipes(lastToken(raw, tokens, trailingSpace), candidates);
      return;
    }

    // Convert Command
    if (lower.startsWith("convert ")) {
      if (tokens.size() <= 2 && !trailingSpace) {
        completeRecipes(lastToken(raw, tokens, trailingSpace), candidates);
      } else {
        completeUnits(lastToken(raw, tokens, trailingSpace), candidates);
      }
      return;
    }

    // Recipes Collection
    if (lower.startsWith("recipes ")) {
      completeCollections(lastToken(raw, tokens, trailingSpace), candidates);
      return;
    }

    // Import Command
    if (lower.startsWith("import json ")) {
      // import json <file> <collection>
      if (tokens.size() >= 4 || (tokens.size() == 3 && trailingSpace)) {
        completeCollections(lastToken(raw, tokens, trailingSpace), candidates);
      }
      return;
    }

    // Export Command
    if (lower.startsWith("export ")) {
      // export <recipe> <file>
      if (tokens.size() <= 2 && !trailingSpace) {
        completeRecipes(lastToken(raw, tokens, trailingSpace), candidates);
      }
    }
  }

  /**
   * Completes commands available while cook mode is active.
   *
   * @param raw the raw user input
   * @param candidates the candidate list to update
   */
  private void completeCookMode(String raw, List<Candidate> candidates) {
    List<String> cmds = List.of("next", "prev", "ingredients", "quit");
    String prefix = raw.trim().toLowerCase(Locale.ROOT);
    addMatching(candidates, cmds, prefix);
  }

  /**
   * Completes recipe titles from all recipes in the library.
   *
   * @param prefix the lower-cased completion prefix
   * @param candidates the candidate list to update
   */
  private void completeRecipes(String prefix, List<Candidate> candidates) {
    Set<String> titles = new LinkedHashSet<>();
    for (Recipe r : library.getRecipeRepository().findAll()) {
      titles.add(QuotedTokenizer.quoteIfNeeded(r.getTitle()));
    }
    addMatching(candidates, new ArrayList<>(titles), prefix);
  }

  /**
   * Completes collection titles from the library.
   *
   * @param prefix the lower-cased completion prefix
   * @param candidates the candidate list to update
   */
  private void completeCollections(String prefix, List<Candidate> candidates) {
    Set<String> titles = new LinkedHashSet<>();
    for (RecipeCollection c : library.getCollectionRepository().findAll()) {
      titles.add(QuotedTokenizer.quoteIfNeeded(c.getTitle()));
    }
    addMatching(candidates, new ArrayList<>(titles), prefix);
  }

  /**
   * Completes supported unit names and abbreviations.
   *
   * @param prefix the lower-cased completion prefix
   * @param candidates the candidate list to update
   */
  private void completeUnits(String prefix, List<Candidate> candidates) {
    addMatching(candidates, List.of(Unit.getParseableNames()), prefix);
  }

  /**
   * Returns the token currently being completed.
   *
   * @param raw the raw user input
   * @param tokens tokenized input values
   * @param trailingSpace whether the input currently ends with whitespace
   * @return the lower-cased token prefix to match against
   */
  private static String lastToken(String raw, List<String> tokens, boolean trailingSpace) {
    if (trailingSpace) {
      return "";
    }

    int lastQuote = raw.lastIndexOf('"');
    if (lastQuote >= 0) {
      String afterQuote = raw.substring(lastQuote + 1).trim().toLowerCase(Locale.ROOT);
      if (!afterQuote.isEmpty()) {
        return afterQuote;
      }
    }

    return tokens.isEmpty() ? "" : tokens.get(tokens.size() - 1).toLowerCase(Locale.ROOT);
  }

  /**
   * Adds options that start with the provided prefix to the candidate list.
   *
   * @param candidates the candidate list to update
   * @param options available options to consider
   * @param lowerPrefix user-typed prefix in lower-case form
   */
  private static void addMatching(
      List<Candidate> candidates, List<String> options, String lowerPrefix) {
    String prefix = normalizeForMatch(lowerPrefix);
    for (String option : options) {
      String normalizedOption = normalizeForMatch(option);
      if (prefix.isEmpty() || normalizedOption.startsWith(prefix)) {
        candidates.add(new Candidate(option));
      }
    }
  }

  /**
   * Normalizes a value for case-insensitive matching and strips edge quotes.
   *
   * @param s the value to normalize
   * @return normalized value for prefix matching
   */
  private static String normalizeForMatch(String s) {
    String value = s == null ? "" : s.toLowerCase(Locale.ROOT);
    if (value.startsWith("\"")) {
      value = value.substring(1);
    }
    if (value.endsWith("\"")) {
      value = value.substring(0, value.length() - 1);
    }
    return value;
  }
}
