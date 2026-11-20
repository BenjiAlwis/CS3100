package net.sceneitall.iot.lab9.cli;

import org.jline.reader.Completer;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;

/** A CLI command that can be invoked by the user. */
public interface Command {

  /** The word the user types, e.g. "list". */
  String getName();

  /** Called when the user runs the command. */
  void execute(String[] args, Terminal terminal);

  /**
   * Returns a completer for this command. The default implementation provides completion for the
   * command name only. Commands that accept arguments should override this to provide
   * argument-aware completion.
   */
  default Completer getCompleter() {
    return new StringsCompleter(getName());
  }
}
