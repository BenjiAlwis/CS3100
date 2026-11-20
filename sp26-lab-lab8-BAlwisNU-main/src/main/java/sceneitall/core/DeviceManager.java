package sceneitall.core;

import java.util.List;

import sceneitall.models.DeviceHealth;
import sceneitall.models.IoTDevice;

/**
 * Manages IoT devices for a home. Handles adding devices, listing devices, and checking device
 * health status.
 */
public class DeviceManager {

  private final DeviceRepository deviceRepository;

  public DeviceManager(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
  }

  /**
   * Adds a device to the specified home and persists it.
   *
   * @param homeId the home to add the device to
   * @param device the device to add
   */
  public void addDevice(String homeId, IoTDevice device) {
    device.setHomeId(homeId);
    deviceRepository.saveDevice(device);
  }

  /**
   * Returns all devices in the given home.
   *
   * @param homeId the home identifier
   * @return list of devices in the home
   */
  public List<IoTDevice> getDevices(String homeId) {
    return deviceRepository.loadDevices(homeId);
  }

  /**
   * Checks the health status of a device.
   *
   * @param deviceId the device identifier
   * @return the device health status
   */
  public DeviceHealth checkHealth(String deviceId) {
    DeviceHealth health = deviceRepository.loadDeviceHealth(deviceId);

    // Preserve existing behavior: default OK if missing
    if (health == null) {
      return new DeviceHealth(deviceId, true, "OK", System.currentTimeMillis());
    }
    return health;
  }
}
