package net.sceneitall.iot;

/**
 * A simple smart fan device.
 *
 * <p>This fan can be turned on and off, and supports multiple speed settings.
 */
public class Fan implements IoTDevice {

  private final String name;
  private boolean running;
  private FanSpeed speed;

  /**
   * Creates a new fan with the given name.
   *
   * @param name the display name for this fan
   */
  public Fan(String name) {
    this.name = name;
    this.running = false;
    this.speed = FanSpeed.LOW; // default to low speed
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void identify() {
    // In a real device, this might cause the fan to briefly spin
    System.out.println("Fan '" + name + "' is identifying itself!");
  }

  @Override
  public boolean isAvailable() {
    // For this simulation, the fan is always available
    return true;
  }

  /** Turns the fan on at the current speed setting. */
  public void turnOn() {
    this.running = true;
  }

  /** Turns the fan off. */
  public void turnOff() {
    this.running = false;
  }

  /**
   * Checks if the fan is currently running.
   *
   * <p>BUG: There's something wrong with this method! Can you find and fix it?
   *
   * @return true if the fan is running, false otherwise
   */
  public boolean isRunning() {
    // HINT: This method should return whether the fan is running...
    // but something isn't quite right here!
    return running; // <-- BUG: This logic is inverted!
  }

  /**
   * Sets the fan speed.
   *
   * @param speed the speed setting
   */
  public void setSpeed(FanSpeed speed) {
    this.speed = speed;
  }

  /**
   * Gets the current fan speed.
   *
   * @return the current speed setting
   */
  public FanSpeed getSpeed() {
    return speed;
  }
}
