package sceneitall.model;

/** A smart fan with on/off control and adjustable speed. */
public final class Fan implements Device {

  private final String name;
  private boolean on;
  private FanSpeed speed;

  /**
   * Creates a new fan that is off at low speed.
   *
   * @param name the display name for this fan
   */
  public Fan(String name) {
    this.name = name;
    this.on = false;
    this.speed = FanSpeed.LOW;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public DeviceType getType() {
    return DeviceType.FAN;
  }

  /** Returns true if the fan is on. */
  public boolean isOn() {
    return on;
  }

  /** Turns the fan on or off. */
  public void setOn(boolean on) {
    this.on = on;
  }

  /** Returns the current speed setting. */
  public FanSpeed getSpeed() {
    return speed;
  }

  /** Sets the fan speed. */
  public void setSpeed(FanSpeed speed) {
    this.speed = speed;
  }

  @Override
  public String toString() {
    return "Fan[" + name + ", on=" + on + ", speed=" + speed + "]";
  }
}
