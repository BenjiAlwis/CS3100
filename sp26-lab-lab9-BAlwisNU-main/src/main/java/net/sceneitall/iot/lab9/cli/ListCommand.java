package net.sceneitall.iot.lab9.cli;

import java.util.List;
import java.util.Locale;

import org.jline.terminal.Terminal;

import net.sceneitall.iot.lab9.model.DeviceRegistry;
import net.sceneitall.iot.lab9.model.IoTDevice;

/** Lists all registered devices. */
public class ListCommand implements Command {

  private final DeviceRegistry registry;

  public ListCommand(DeviceRegistry registry) {
    this.registry = registry;
  }

  @Override
  public String getName() {
    return "list";
  }

  @Override
  public void execute(String[] args, Terminal terminal) {
    List<IoTDevice> devices = registry.getAllDevices();
    if (devices.isEmpty()) {
      terminal.writer().println("No devices registered.");
    } else {
      for (IoTDevice device : devices) {
        terminal
            .writer()
            .println(
                device.getId() + " (" + device.getType().name().toLowerCase(Locale.ROOT) + ")");
      }
    }
    terminal.writer().flush();
  }
}
