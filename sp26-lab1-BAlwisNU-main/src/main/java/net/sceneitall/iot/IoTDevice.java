package net.sceneitall.iot;

/**
 * Base interface for all IoT devices in the SceneItAll smart home system.
 *
 * <p>Every device in our smart home ecosystem must be identifiable and report its availability
 * status. This interface defines the common contract that all devices must implement.
 */
public interface IoTDevice {

  /**
   * Returns a human-readable name for this device.
   *
   * @return the device name
   */
  String getName();

  /**
   * Triggers an identification action on the device.
   *
   * <p>This is useful for locating a device in your home. For example, a light might flash, or a
   * speaker might beep.
   */
  void identify();

  /**
   * Checks if the device is currently available and responding.
   *
   * @return true if the device is online and ready to receive commands
   */
  boolean isAvailable();
}
