package net.sceneitall.iot.lab9.sim;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.sceneitall.iot.lab9.model.DeviceType;

/** Configuration for the simulated network. Maps device IDs to failure modes. */
public class DeviceSimConfig {

  private final Map<String, DeviceEntry> devices;

  @JsonCreator
  public DeviceSimConfig(@JsonProperty("devices") Map<String, DeviceEntry> devices) {
    this.devices = devices != null ? Map.copyOf(devices) : Map.of();
  }

  public Map<String, DeviceEntry> getDevices() {
    return devices;
  }

  /** Per-device configuration. */
  public static class DeviceEntry {

    private final DeviceType type;
    private final FailureMode failureMode;

    @JsonCreator
    public DeviceEntry(
        @JsonProperty("type") DeviceType type,
        @JsonProperty("failureMode") FailureMode failureMode) {
      this.type = type;
      this.failureMode = failureMode != null ? failureMode : FailureMode.NONE;
    }

    public DeviceType getType() {
      return type;
    }

    public FailureMode getFailureMode() {
      return failureMode;
    }
  }
}
