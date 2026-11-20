package net.sceneitall.iot;

/**
 * A smart fan device that supports multiple speed settings.
 *
 * <p>Unlike lights which have continuous brightness control, fans typically have discrete speed
 * levels: OFF, LOW, MEDIUM, and HIGH.
 */
public class Fan implements IoTDevice {

  private final String id;
  private FanSpeed speed;
  private boolean isAvailable;

  /**
   * Creates a new fan with the specified ID.
   *
   * <p>The fan starts in the OFF state.
   *
   * @param id the unique identifier for this fan
   */
  public Fan(String id) {
    this.id = id;
    this.speed = FanSpeed.OFF;
    this.isAvailable = true;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public boolean isAvailable() {
    return isAvailable;
  }

  @Override
  public void setAvailability(boolean available) {
    this.isAvailable = available;
  }

  private void wiggle(int times) {
    for (int i = 0; i < times; i++) {
      this.setSpeed(FanSpeed.LOW);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      this.setSpeed(FanSpeed.OFF);
    }
  }

  @Override
  public void identify() {
    this.wiggle(3);
  }

  /**
   * Gets the current speed setting.
   *
   * @return the current fan speed
   */
  public FanSpeed getSpeed() {
    return speed;
  }

  /**
   * Sets the fan speed.
   *
   * @param speed the desired speed setting
   */
  public void setSpeed(FanSpeed speed) {
    this.speed = speed;
    System.out.println("Fan [" + id + "] speed set to " + speed);
  }

  /** Turns the fan off. */
  public void turnOff() {
    setSpeed(FanSpeed.OFF);
  }

  /**
   * Checks whether the fan is currently running (not OFF).
   *
   * @return true if the fan is running at any speed, false if OFF
   */
  public boolean isRunning() {
    return speed != FanSpeed.OFF;
  }
}
