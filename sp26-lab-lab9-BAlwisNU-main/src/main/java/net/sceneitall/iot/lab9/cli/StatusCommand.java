package net.sceneitall.iot.lab9.cli;

import java.util.List;
import java.util.Locale;

import org.jline.reader.Completer;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;

import net.sceneitall.iot.lab9.gateway.DeviceGateway;
import net.sceneitall.iot.lab9.gateway.DeviceGatewayException;
import net.sceneitall.iot.lab9.gateway.DeviceUnreachableException;
import net.sceneitall.iot.lab9.model.DeviceRegistry;
import net.sceneitall.iot.lab9.model.DeviceState;
import net.sceneitall.iot.lab9.model.IoTDevice;

/** Shows all devices with connection status. */
public class StatusCommand implements Command {

  private final DeviceGateway gateway;
  private final DeviceRegistry registry;

  public StatusCommand(DeviceGateway gateway, DeviceRegistry registry) {
    this.gateway = gateway;
    this.registry = registry;
  }

  @Override
  public String getName() {
    return "status";
  }

  @Override
  public Completer getCompleter() {
    return new ArgumentCompleter(
        new StringsCompleter(getName()),
        new StringsCompleter(
            () -> registry.getAllDevices().stream().map(IoTDevice::getId).toList()));
  }

  @Override
  public void execute(String[] args, Terminal terminal) {
    List<IoTDevice> devices = registry.getAllDevices();
    if (devices.isEmpty()) {
      terminal.writer().println("No devices registered.");
      terminal.writer().flush();
      return;
    }

    terminal.writer().printf("%-18s [%-12s] %-11s %s%n", "DEVICE", "TYPE", "STATUS", "STATE");
    terminal.writer().println("----------------------------------------------------------------");

    for (IoTDevice device : devices) {
      String typeName = device.getType().name().toLowerCase(Locale.ROOT);

      try {
        DeviceState state = gateway.readState(device.getId());
        String stateValue = state.getStateValue();
        if (stateValue == null || stateValue.isBlank()) {
          terminal
              .writer()
              .printf("%-18s [%-12s] PARTIAL      - state unknown%n", device.getId(), typeName);
        } else {
          terminal
              .writer()
              .printf("%-18s [%-12s] REACHABLE    - %s%n", device.getId(), typeName, stateValue);
        }
      } catch (DeviceUnreachableException | DeviceGatewayException e) {
        terminal.writer().printf("%-18s [%-12s] UNREACHABLE%n", device.getId(), typeName);
      }
    }

    terminal.writer().flush();
  }
}
