package net.sceneitall.iot.lab9.gateway;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import net.sceneitall.iot.lab9.model.CommandResult;
import net.sceneitall.iot.lab9.model.DeviceRequest;

/** Client for sending requests to devices over the network. */
public interface NetworkClient {

  /**
   * Sends a request without timeout. Blocks until the device responds or the connection fails. May
   * block indefinitely if the device accepts the connection but never replies.
   */
  CommandResult send(DeviceRequest request) throws DeviceUnreachableException;

  /**
   * Sends a request with a timeout. Throws TimeoutException if the device does not respond within
   * the given duration.
   */
  CommandResult send(DeviceRequest request, Duration timeout)
      throws DeviceUnreachableException, TimeoutException;
}
