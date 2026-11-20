package net.sceneitall.iot;

/**
 * A light that supports both brightness control and color temperature adjustment.
 *
 * <p>Tunable white lights can adjust their color temperature from warm (yellowish, like candlelight
 * or incandescent bulbs) to cool (bluish, like daylight). Color temperature is measured in Kelvin
 * (K):
 *
 * <ul>
 *   <li>2700K - Warm white (similar to incandescent bulbs)
 *   <li>3000K - Soft white
 *   <li>4000K - Neutral white
 *   <li>5000K - Daylight
 *   <li>6500K - Cool daylight
 * </ul>
 */
public class TunableWhiteLight extends DimmableLight {

  /** Minimum color temperature in Kelvin (warm). */
  public static final int MIN_COLOR_TEMP = 2700;

  /** Maximum color temperature in Kelvin (cool). */
  public static final int MAX_COLOR_TEMP = 6500;

  private int colorTemperature;

  /**
   * Creates a new tunable white light with the specified ID, color temperature, and brightness.
   *
   * @param id the unique identifier for this light
   * @param startupColorTemperature color temperature in Kelvin (2700-6500) to use at startup
   * @param startupBrightness brightness level (0-100) to use when the light starts up
   * @throws IllegalArgumentException if color temperature or brightness is outside valid range
   */
  public TunableWhiteLight(String id, int startupColorTemperature, int startupBrightness) {
    super(id, startupBrightness);
    setColorTemperature(startupColorTemperature);
  }

  /**
   * Gets the current color temperature.
   *
   * @return color temperature in Kelvin
   */
  public int getColorTemperature() {
    return colorTemperature;
  }

  /**
   * Sets the color temperature.
   *
   * @param colorTemperature color temperature in Kelvin (2700-6500)
   * @throws IllegalArgumentException if color temperature is outside valid range
   */
  public void setColorTemperature(int colorTemperature) {
    if (colorTemperature < MIN_COLOR_TEMP || colorTemperature > MAX_COLOR_TEMP) {
      throw new IllegalArgumentException(
          "Color temperature must be between " + MIN_COLOR_TEMP + "K and " + MAX_COLOR_TEMP + "K");
    }
    this.colorTemperature = colorTemperature;
    System.out.println(
        "TunableWhiteLight [" + getId() + "] color temperature set to " + colorTemperature + "K");
  }

  @Override
  public void identify() {
    System.out.println(
        "TunableWhiteLight ["
            + getId()
            + "] is cycling through color temperatures to identify itself!");
  }
}
