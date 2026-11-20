package net.sceneitall.iot.lab9.cli;

import java.util.Map;

import org.jline.terminal.Terminal;

/** Shows available commands. */
public class HelpCommand implements Command {

  private final Map<String, Command> commands;

  public HelpCommand(Map<String, Command> commands) {
    this.commands = Map.copyOf(commands);
  }

  @Override
  public String getName() {
    return "help";
  }

  @Override
  public void execute(String[] args, Terminal terminal) {
    terminal.writer().println("Available commands:");
    for (Command cmd : commands.values()) {
      terminal.writer().println("  " + cmd.getName());
    }
    terminal.writer().println("  quit, exit");
    terminal.writer().flush();
  }
}
