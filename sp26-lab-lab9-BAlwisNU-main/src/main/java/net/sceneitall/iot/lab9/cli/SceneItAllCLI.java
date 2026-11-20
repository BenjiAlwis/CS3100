package net.sceneitall.iot.lab9.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;

import net.sceneitall.iot.lab9.gateway.DeviceGateway;
import net.sceneitall.iot.lab9.model.DeviceRegistry;

/** Interactive CLI for the SceneItAll IoT hub. */
public class SceneItAllCLI {

  private final Map<String, Command> commands;
  private final Terminal terminal;
  private final LineReader reader;

  public SceneItAllCLI(DeviceGateway gateway, DeviceRegistry registry, Terminal terminal) {
    this.terminal = terminal;
    this.commands = new HashMap<>();

    commands.put("list", new ListCommand(registry));
    commands.put("on", new OnOffCommand(gateway, registry, true));
    commands.put("off", new OnOffCommand(gateway, registry, false));
    commands.put("status", new StatusCommand(gateway, registry));
    commands.put("help", new HelpCommand(commands));

    Completer completer = buildCompleter();
    Path historyFile = Paths.get(System.getProperty("user.home"), ".sceneitall_history");

    this.reader =
        LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(completer)
            .history(new DefaultHistory())
            .variable(LineReader.HISTORY_FILE, historyFile)
            .build();
  }

  private Completer buildCompleter() {
    List<Completer> completers = new ArrayList<>();
    for (Command cmd : commands.values()) {
      completers.add(cmd.getCompleter());
    }
    completers.add(new StringsCompleter("quit", "exit"));
    return new AggregateCompleter(completers);
  }

  public void run() {
    terminal.writer().println("SceneItAll IoT Hub CLI. Type 'help' for commands, 'quit' to exit.");
    terminal.writer().flush();

    while (true) {
      String line;
      try {
        line = reader.readLine("sceneitall> ");
      } catch (org.jline.reader.EndOfFileException e) {
        break;
      }
      if (line == null) {
        break;
      }
      String[] parts = line.trim().split("\\s+");
      if (parts.length == 0 || parts[0].isEmpty()) {
        continue;
      }
      String cmdName = parts[0].toLowerCase(Locale.ROOT);
      if (cmdName.equals("quit") || cmdName.equals("exit")) {
        break;
      }
      Command cmd = commands.get(cmdName);
      if (cmd != null) {
        cmd.execute(parts, terminal);
      } else {
        terminal.writer().println("Unknown command: " + cmdName);
        terminal.writer().flush();
      }
    }
  }
}
