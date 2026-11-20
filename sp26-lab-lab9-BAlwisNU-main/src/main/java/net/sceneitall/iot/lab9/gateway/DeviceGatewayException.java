package net.sceneitall.iot.lab9.gateway;

/** Thrown when the gateway exhausts retries or encounters an unrecoverable error. */
public class DeviceGatewayException extends RuntimeException {

  public DeviceGatewayException(String message) {
    super(message);
  }

  public DeviceGatewayException(String message, Throwable cause) {
    super(message, cause);
  }
}
