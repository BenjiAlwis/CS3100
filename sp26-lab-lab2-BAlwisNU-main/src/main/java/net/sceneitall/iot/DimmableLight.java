package net.sceneitall.iot;

/**
 * A light that supports brightness control (dimming).
 *
 * <p>Dimmable lights can be set to any brightness level from 0 to 100 percent. A brightness of 0
 * effectively turns the light off, while 100 is full brightness.
 */
public class DimmableLight extends Light {

  /** Minimum brightness level (0%). */
  public static final int MIN_BRIGHTNESS = 0;

  /** Maximum brightness level (100%). */
  public static final int MAX_BRIGHTNESS = 100;

  private int brightness;

  /**
   * Creates a new dimmable light with the specified ID and startup brightness.
   *
   * @param id the unique identifier for this light
   * @param startupBrightness brightness level (0-100) to use when the light starts up
   * @throws IllegalArgumentException if brightness is outside valid range
   */
  public DimmableLight(String id, int startupBrightness) {
    super(id);
    setBrightness(startupBrightness);
  }

  /**
   * Gets the current brightness level.
   *
   * @return brightness level from 0 to 100
   */
  public int getBrightness() {
    return brightness;
  }

  /**
   * Sets the brightness level.
   *
   * @param brightness brightness level from 0 to 100
   * @throws IllegalArgumentException if brightness is outside valid range
   */
  public void setBrightness(int brightness) {
    if (brightness < MIN_BRIGHTNESS || brightness > MAX_BRIGHTNESS) {
      throw new IllegalArgumentException(
          "Brightness must be between " + MIN_BRIGHTNESS + " and " + MAX_BRIGHTNESS);
    }
    this.brightness = brightness;
    System.out.println("DimmableLight [" + getId() + "] brightness set to " + brightness + "%");
  }

  @Override
  public void identify() {
    System.out.println("DimmableLight [" + getId() + "] is pulsing brightness to identify itself!");
  }

  @Override
  public void turnOn() {
    super.turnOn();
    // If brightness was 0, set to a reasonable default
    if (brightness == 0) {
      brightness = MAX_BRIGHTNESS;
    }
  }
}
