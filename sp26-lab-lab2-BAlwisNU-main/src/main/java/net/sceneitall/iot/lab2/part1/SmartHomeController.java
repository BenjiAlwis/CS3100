package net.sceneitall.iot.lab2.part1;

import java.util.ArrayList;
import java.util.List;

import net.sceneitall.iot.IoTDevice;

/**
 * Controller for managing IoT devices in a smart home.
 *
 * <p>This class demonstrates the power of polymorphism: it operates on devices through the
 * IoTDevice interface without knowing their concrete types. The same code works with lights, fans,
 * thermostats, or any future device type that implements IoTDevice.
 *
 * <p><b>Lab Exercise 1.1:</b> Implement the methods below using polymorphism. Your code should NOT
 * use any {@code instanceof} checks - let the JVM's dynamic dispatch handle the different device
 * types!
 */
public class SmartHomeController {

  private List<IoTDevice> devices;

  /** Creates a new SmartHomeController with an empty device list. */
  public SmartHomeController() {
    this.devices = new ArrayList<>();
  }

  /**
   * Adds any IoT device to the controller.
   *
   * <p><b>TODO:</b> Implement this method.
   *
   * @param device the device to add
   */
  public void addDevice(IoTDevice device) {
    // TODO: Implement this
    devices.add(device);
  }

  /**
   * Calls identify() on ALL devices in the home.
   *
   * <p>Each device type will identify itself differently (lights flash, fans spin, etc.). The
   * beauty of polymorphism is that this method doesn't need to know what type each device is!
   *
   * <p><b>TODO:</b> Implement this method. Hint: You don't need to know what type each device is!
   */
  public void identifyAllDevices() {
    // TODO: Implement this
    // Hint: You don't need to know what type each device is!
    for (IoTDevice device : devices) {
      device.identify();
    }
  }

  /**
   * Returns a count of how many devices are currently available (connected).
   *
   * <p><b>TODO:</b> Implement this method using the isAvailable() method from IoTDevice.
   *
   * @return the number of available devices
   */
  public int countAvailableDevices() {
    // TODO: Implement this
    // Use the isAvailable() method from IoTDevice
    int count = 0;

    for (IoTDevice device : devices) {
      if (device.isAvailable()) {
        count++;
      }
    }

    return count;
  }

  /**
   * Returns the number of devices in the controller.
   *
   * @return the number of devices in the controller
   */
  public int getDeviceCount() {
    // TODO: Implement this
    return devices.size();
  }
}
