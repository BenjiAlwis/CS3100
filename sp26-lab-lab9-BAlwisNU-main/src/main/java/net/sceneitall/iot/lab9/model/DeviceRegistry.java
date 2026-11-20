package net.sceneitall.iot.lab9.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Registry of all devices known to the hub. */
public class DeviceRegistry {

  private final List<IoTDevice> devices;

  public DeviceRegistry(List<IoTDevice> devices) {
    this.devices = Collections.unmodifiableList(List.copyOf(devices));
  }

  public List<IoTDevice> getAllDevices() {
    return devices;
  }

  public Optional<IoTDevice> getDevice(String deviceId) {
    return devices.stream().filter(d -> d.getId().equals(deviceId)).findFirst();
  }
}
