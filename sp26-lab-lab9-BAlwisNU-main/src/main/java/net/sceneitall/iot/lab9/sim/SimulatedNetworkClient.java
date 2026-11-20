package net.sceneitall.iot.lab9.sim;

import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import net.sceneitall.iot.lab9.gateway.DeviceUnreachableException;
import net.sceneitall.iot.lab9.gateway.NetworkClient;
import net.sceneitall.iot.lab9.model.CommandResult;
import net.sceneitall.iot.lab9.model.DeviceRequest;
import net.sceneitall.iot.lab9.model.DeviceState;

/** Simulates network communication with configurable failure modes per device. */
public class SimulatedNetworkClient implements NetworkClient {

  private final Map<String, DeviceSimConfig.DeviceEntry> deviceConfig;
  private final Random random = new Random();

  public SimulatedNetworkClient(DeviceSimConfig config) {
    this.deviceConfig = config.getDevices();
  }

  @Override
  public CommandResult send(DeviceRequest request) throws DeviceUnreachableException {
    DeviceSimConfig.DeviceEntry entry = deviceConfig.get(request.getDeviceId());
    FailureMode mode = entry != null ? entry.getFailureMode() : FailureMode.NONE;

    switch (mode) {
      case UNREACHABLE:
        throw new DeviceUnreachableException("Device " + request.getDeviceId() + " is unreachable");
      case TIMEOUT:
        // Block forever - device accepts connection but never replies
        while (true) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DeviceUnreachableException("Interrupted while waiting", e);
          }
        }
      case INTERMITTENT:
        if (random.nextDouble() < 0.6) {
          throw new DeviceUnreachableException(
              "Device " + request.getDeviceId() + " did not respond (intermittent failure)");
        }
        return successResult(request.getDeviceId(), request.getCommand());
      case PARTIAL:
        return new CommandResult(new DeviceState(request.getDeviceId(), "unknown"), true);
      case NONE:
      default:
        return successResult(request.getDeviceId(), request.getCommand());
    }
  }

  @Override
  public CommandResult send(DeviceRequest request, Duration timeout)
      throws DeviceUnreachableException, TimeoutException {
    DeviceSimConfig.DeviceEntry entry = deviceConfig.get(request.getDeviceId());
    FailureMode mode = entry != null ? entry.getFailureMode() : FailureMode.NONE;

    switch (mode) {
      case UNREACHABLE:
        throw new DeviceUnreachableException("Device " + request.getDeviceId() + " is unreachable");
      case TIMEOUT:
        try {
          Thread.sleep(timeout.toMillis());
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new DeviceUnreachableException("Interrupted while waiting", e);
        }
        throw new TimeoutException(
            "Device " + request.getDeviceId() + " did not respond within " + timeout);
      case INTERMITTENT:
        if (random.nextDouble() < 0.6) {
          throw new TimeoutException(
              "Device " + request.getDeviceId() + " did not respond (intermittent failure)");
        }
        return successResult(request.getDeviceId(), request.getCommand());
      case PARTIAL:
        return new CommandResult(new DeviceState(request.getDeviceId(), "unknown"), true);
      case NONE:
      default:
        return successResult(request.getDeviceId(), request.getCommand());
    }
  }

  private CommandResult successResult(
      String deviceId, net.sceneitall.iot.lab9.model.DeviceCommand command) {
    String stateValue =
        switch (command) {
          case ON -> "on";
          case OFF -> "off";
          case READ_STATE -> "on"; // Default mock state
        };
    return new CommandResult(new DeviceState(deviceId, stateValue), false);
  }
}
