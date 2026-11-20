package net.sceneitall.iot.lab9.gateway;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import net.sceneitall.iot.lab9.model.CommandResult;
import net.sceneitall.iot.lab9.model.DeviceCommand;
import net.sceneitall.iot.lab9.model.DeviceRequest;
import net.sceneitall.iot.lab9.model.DeviceState;

/**
 * Gateway for communicating with IoT devices. Assumes the network is reliable—students fix this.
 */
public class DeviceGateway {

  private static final Duration REQUEST_TIMEOUT = Objects.requireNonNull(Duration.ofSeconds(5));
  private static final int SEND_COMMAND_ATTEMPTS = 1;
  private static final int READ_STATE_ATTEMPTS = 2;

  private final NetworkClient networkClient;

  public DeviceGateway(NetworkClient networkClient) {
    this.networkClient = networkClient;
  }

  public CommandResult sendCommand(String deviceId, DeviceCommand command)
      throws DeviceUnreachableException {
    DeviceRequest request = new DeviceRequest(deviceId, command);
    return sendWithRetry(request, deviceId, SEND_COMMAND_ATTEMPTS);
  }

  public DeviceState readState(String deviceId) throws DeviceUnreachableException {
    DeviceRequest request = new DeviceRequest(deviceId, DeviceCommand.READ_STATE);
    return sendWithRetry(request, deviceId, READ_STATE_ATTEMPTS).getState();
  }

  private CommandResult sendWithRetry(DeviceRequest request, String deviceId, int maxAttempts)
      throws DeviceUnreachableException {
    int attempts = 0;
    while (attempts < maxAttempts) {
      try {
        return networkClient.send(request, REQUEST_TIMEOUT);
      } catch (TimeoutException | DeviceUnreachableException e) {
        attempts++;
        if (attempts >= maxAttempts) {
          throw new DeviceUnreachableException(
              "Device "
                  + deviceId
                  + " unreachable after "
                  + maxAttempts
                  + " attempt(s) with "
                  + REQUEST_TIMEOUT.toSeconds()
                  + "s timeout",
              e);
        }

        long waitMs = (long) Math.pow(2, attempts) * 1000;
        try {
          Thread.sleep(waitMs);
        } catch (InterruptedException interruptedException) {
          Thread.currentThread().interrupt();
          throw new DeviceUnreachableException(
              "Retry interrupted while contacting device " + deviceId, interruptedException);
        }
      }
    }

    throw new DeviceUnreachableException(
        "Unexpected retry loop termination for device " + deviceId);
  }
}

