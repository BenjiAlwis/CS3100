package net.sceneitall.iot;

/**
 * Interface for light devices in the SceneItAll smart home system.
 *
 * <p>All lights can be turned on and off, and can report whether they are currently on. More
 * advanced lights (like dimmable or tunable lights) extend this basic functionality.
 */
public interface Light extends IoTDevice {

  /** Turns the light on. */
  void turnOn();

  /** Turns the light off. */
  void turnOff();

  /**
   * Checks if the light is currently on.
   *
   * @return true if the light is on, false otherwise
   */
  boolean isOn();
}
