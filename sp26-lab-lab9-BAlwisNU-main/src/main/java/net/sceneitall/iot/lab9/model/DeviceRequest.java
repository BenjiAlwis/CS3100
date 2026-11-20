package net.sceneitall.iot.lab9.model;

/** A request to send a command to a device over the network. */
public class DeviceRequest {

  private final String deviceId;
  private final DeviceCommand command;

  public DeviceRequest(String deviceId, DeviceCommand command) {
    this.deviceId = deviceId;
    this.command = command;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public DeviceCommand getCommand() {
    return command;
  }
}
