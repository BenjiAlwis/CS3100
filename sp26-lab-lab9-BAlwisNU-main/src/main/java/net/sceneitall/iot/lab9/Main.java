package net.sceneitall.iot.lab9;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jline.terminal.TerminalBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.sceneitall.iot.lab9.cli.SceneItAllCLI;
import net.sceneitall.iot.lab9.gateway.DeviceGateway;
import net.sceneitall.iot.lab9.model.DeviceRegistry;
import net.sceneitall.iot.lab9.model.IoTDevice;
import net.sceneitall.iot.lab9.sim.DeviceSimConfig;
import net.sceneitall.iot.lab9.sim.SimulatedNetworkClient;

/** Entry point for the SceneItAll IoT hub CLI. */
public class Main {

  public static void main(String[] args) throws Exception {
    String configPath = "config/scenario-timeout.json";
    for (int i = 0; i < args.length; i++) {
      if ("--config".equals(args[i]) && i + 1 < args.length) {
        configPath = args[i + 1];
        break;
      }
    }

    ObjectMapper mapper = new ObjectMapper();
    DeviceSimConfig config =
        mapper.readValue(Files.readString(Path.of(configPath)), DeviceSimConfig.class);

    List<IoTDevice> devices = new ArrayList<>();
    config.getDevices().forEach((id, entry) -> devices.add(new IoTDevice(id, entry.getType())));

    DeviceRegistry registry = new DeviceRegistry(devices);
    SimulatedNetworkClient networkClient = new SimulatedNetworkClient(config);
    DeviceGateway gateway = new DeviceGateway(networkClient);

    try (var terminal = TerminalBuilder.builder().system(true).jansi(true).jna(true).build()) {
      SceneItAllCLI cli = new SceneItAllCLI(gateway, registry, terminal);
      cli.run();
    }
  }
}
