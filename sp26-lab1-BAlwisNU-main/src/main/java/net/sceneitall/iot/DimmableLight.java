package net.sceneitall.iot;

/**
 * A light that supports brightness control.
 *
 * <p>In addition to on/off control, this light allows you to set the brightness from 0% to 100%.
 */
public class DimmableLight extends SwitchedLight {

  private int brightness; // 0-100 percent

  /**
   * Creates a new dimmable light with the given name.
   *
   * @param name the display name for this light
   */
  public DimmableLight(String name) {
    super(name);
    this.brightness = 100; // default to full brightness
  }

  /**
   * Sets the brightness of the light.
   *
   * @param brightness the brightness level (0-100)
   * @throws IllegalArgumentException if brightness is not between 0 and 100
   */
  public void setBrightness(int brightness) {
    if (brightness < 0 || brightness > 100) {
      throw new IllegalArgumentException(
          "Brightness must be between 0 and 100, got: " + brightness);
    }
    this.brightness = brightness;
  }

  /**
   * Gets the current brightness level.
   *
   * @return the brightness (0-100)
   */
  public int getBrightness() {
    return brightness;
  }

  @Override
  public void turnOn() {
    super.turnOn();
    // If brightness was 0, set it to 100 when turning on
    if (brightness == 0) {
      brightness = 100;
    }
  }
}
