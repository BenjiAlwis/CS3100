package net.sceneitall.iot.lab9.model;

/** The current state of an IoT device (e.g., "on", "off", "60%"). */
public class DeviceState {

  private final String deviceId;
  private final String stateValue;

  public DeviceState(String deviceId, String stateValue) {
    this.deviceId = deviceId;
    this.stateValue = stateValue;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public String getStateValue() {
    return stateValue;
  }
}
