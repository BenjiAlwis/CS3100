package app.cookyourbooks.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Quote-aware tokenization helpers for CLI commands and completion.
 *
 * <p>Supports parsing quoted tokens and formatting tokens for safe command-line round trips.
 */
public final class QuotedTokenizer {

  private static final Pattern TOKEN = Pattern.compile("\"([^\"]*)\"|(\\S+)");

  /** Prevents instantiation of this utility class. */
  private QuotedTokenizer() {}

  /**
   * Splits a command line into tokens while preserving quoted segments.
   *
   * @param input the raw command-line input
   * @return the parsed token list
   */
  public static List<String> tokenize(String input) {
    List<String> result = new ArrayList<>();
    Matcher m = TOKEN.matcher(input == null ? "" : input);
    while (m.find()) {
      if (m.group(1) != null) {
        result.add(m.group(1));
      } else if (m.group(2) != null) {
        result.add(m.group(2));
      }
    }
    return result;
  }

  /**
   * Wraps a token in double quotes when it contains whitespace.
   *
   * @param s the token to format
   * @return the token, quoted if needed for CLI parsing
   */
  public static String quoteIfNeeded(String s) {
    return s.contains(" ") ? "\"" + s + "\"" : s;
  }
}
