package net.sceneitall.iot;

/**
 * Interface representing any IoT (Internet of Things) device in a smart home system.
 *
 * <p>All IoT devices share common capabilities: they can be identified, checked for availability,
 * and have their availability status changed. Different device types (lights, fans, thermostats,
 * etc.) implement this interface and add their own specific behaviors.
 *
 * <p>This interface enables polymorphic code that can operate on any device type without knowing
 * the concrete implementation.
 */
public interface IoTDevice {

  /**
   * Returns the unique identifier for this device.
   *
   * @return the device's unique ID string
   */
  String getId();

  /**
   * Triggers an identification routine for this device.
   *
   * <p>Each device type implements this differently: - Lights might flash - Fans might spin briefly
   * - Speakers might play a tone
   *
   * <p>This helps users locate a specific device in their home.
   */
  void identify();

  /**
   * Checks whether this device is currently available (connected and responsive).
   *
   * @return true if the device is available, false otherwise
   */
  boolean isAvailable();

  /**
   * Sets the availability status of this device.
   *
   * @param available true to mark the device as available, false otherwise
   */
  void setAvailability(boolean available);
}
