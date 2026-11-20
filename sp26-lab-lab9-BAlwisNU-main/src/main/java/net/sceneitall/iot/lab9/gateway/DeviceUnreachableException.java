package net.sceneitall.iot.lab9.gateway;

/** Thrown when the network cannot reach a device at all. */
public class DeviceUnreachableException extends Exception {

  public DeviceUnreachableException(String message) {
    super(message);
  }

  public DeviceUnreachableException(String message, Throwable cause) {
    super(message, cause);
  }
}
