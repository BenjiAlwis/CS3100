package net.sceneitall.iot;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a collection of IoT devices in the smart home system.
 *
 * <p>This class provides basic device management functionality: adding devices, retrieving devices
 * by ID, and listing all devices.
 */
public class DeviceManager {

  private final List<IoTDevice> devices;

  /** Creates a new empty device manager. */
  public DeviceManager() {
    this.devices = new ArrayList<>();
  }

  /**
   * Adds a device to the manager.
   *
   * @param device the device to add
   */
  public void addDevice(IoTDevice device) {
    devices.add(device);
  }

  /**
   * Gets all managed devices.
   *
   * @return a list of all devices (defensive copy)
   */
  public List<IoTDevice> getAllDevices() {
    return new ArrayList<>(devices);
  }

  /**
   * Finds a device by its ID.
   *
   * @param id the device ID to search for
   * @return the device with the matching ID, or null if not found
   */
  public IoTDevice findById(String id) {
    for (IoTDevice device : devices) {
      if (device.getId().equals(id)) {
        return device;
      }
    }
    return null;
  }

  /**
   * Gets the number of devices being managed.
   *
   * @return the device count
   */
  public int getDeviceCount() {
    return devices.size();
  }
}
