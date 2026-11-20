package net.sceneitall.iot;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

/**
 * Manages a collection of IoT devices.
 *
 * <p>This class contains some code quality issues that Error Prone and NullAway will detect. Your
 * task is to understand and fix these warnings!
 */
public class DeviceManager {

  private final List<IoTDevice> devices;
  private @Nullable String managerName;

  /** Creates a new DeviceManager with an empty device list. */
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
   * Finds a device by name.
   *
   * <p>WARNING: This method has issues that NullAway will detect!
   *
   * @param name the name to search for
   * @return the device, or null if not found
   */
  public @Nullable IoTDevice findDeviceByName(String name) {
    for (IoTDevice device : devices) {
      // BUG: Using == instead of .equals() for String comparison!
      // Error Prone's StringEquality check will catch this.
      if (device.getName().equals(name)) {
        return device;
      }
    }
    return null;
  }

  /**
   * Gets the total number of devices.
   *
   * @return the device count
   */
  public int getDeviceCount() {
    return devices.size();
  }

  /**
   * Turns on all lights in the system.
   *
   * <p>WARNING: This method has a potential null issue!
   */
  public void turnOnAllLights() {
    for (IoTDevice device : devices) {
      if (device instanceof Light light) {
        light.turnOn();
      }
    }
  }

  /**
   * Gets the manager name length.
   *
   * <p>WARNING: This method has a NullAway issue - it dereferences a nullable field!
   *
   * @return the length of the manager name
   */
  public int getManagerNameLength() {
    // BUG: managerName could be null! NullAway will warn about this.
    if (managerName == null) {
      return 0;
    }
    return managerName.length();
  }

  /**
   * Sets the manager name.
   *
   * @param name the new manager name (can be null)
   */
  public void setManagerName(@Nullable String name) {
    this.managerName = name;
  }

  /**
   * Checks if two device names match.
   *
   * <p>WARNING: This method uses reference equality instead of value equality!
   *
   * @param name1 first name
   * @param name2 second name
   * @return true if names match
   */
  public boolean namesMatch(String name1, String name2) {
    // BUG: Using == instead of .equals()!
    // Error Prone's ReferenceEquality check will catch this.
    return name1.equals(name2);
  }
}
