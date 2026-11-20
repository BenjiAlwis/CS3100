package sceneitall.model;

/** A smart light with on/off control and adjustable brightness. */
public final class Light implements Device {

  private final String name;
  private boolean on;
  private int brightness; // 0-100

  /**
   * Creates a new light that is off with full brightness.
   *
   * @param name the display name for this light
   */
  public Light(String name) {
    this.name = name;
    this.on = false;
    this.brightness = 100;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public DeviceType getType() {
    return DeviceType.LIGHT;
  }

  /** Returns true if the light is on. */
  public boolean isOn() {
    return on;
  }

  /** Turns the light on or off. */
  public void setOn(boolean on) {
    this.on = on;
  }

  /** Returns the brightness level (0-100). */
  public int getBrightness() {
    return brightness;
  }

  /**
   * Sets the brightness level.
   *
   * @param brightness 0-100
   * @throws IllegalArgumentException if brightness is out of range
   */
  public void setBrightness(int brightness) {
    if (brightness < 0 || brightness > 100) {
      throw new IllegalArgumentException(
          "Brightness must be between 0 and 100, got: " + brightness);
    }
    this.brightness = brightness;
  }

  @Override
  public String toString() {
    return "Light[" + name + ", on=" + on + ", brightness=" + brightness + "]";
  }
}
