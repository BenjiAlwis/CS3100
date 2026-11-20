package net.sceneitall.iot.lab9.cli;

import org.jline.reader.Completer;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;

import net.sceneitall.iot.lab9.gateway.DeviceGateway;
import net.sceneitall.iot.lab9.model.DeviceCommand;
import net.sceneitall.iot.lab9.model.DeviceRegistry;
import net.sceneitall.iot.lab9.model.IoTDevice;

/** Turns a device on or off. */
public class OnOffCommand implements Command {

  private final DeviceGateway gateway;
  private final DeviceRegistry registry;
  private final boolean turnOn;

  public OnOffCommand(DeviceGateway gateway, DeviceRegistry registry, boolean turnOn) {
    this.gateway = gateway;
    this.registry = registry;
    this.turnOn = turnOn;
  }

  @Override
  public String getName() {
    return turnOn ? "on" : "off";
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
    if (args.length < 2) {
      terminal.writer().println("Usage: " + getName() + " <device-id>");
      terminal.writer().flush();
      return;
    }
    String deviceId = args[1];
    if (registry.getDevice(deviceId).isEmpty()) {
      terminal.writer().println("Unknown device: " + deviceId);
      terminal.writer().flush();
      return;
    }
    try {
      DeviceCommand command = turnOn ? DeviceCommand.ON : DeviceCommand.OFF;
      gateway.sendCommand(deviceId, command);
      terminal.writer().println("Command sent to " + deviceId + ".");
    } catch (Exception e) {
      terminal.writer().println("Error: " + e.getMessage());
    }
    terminal.writer().flush();
  }
}
