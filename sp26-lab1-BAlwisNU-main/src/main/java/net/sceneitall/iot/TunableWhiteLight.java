package net.sceneitall.iot;

/**
 * A light that supports both dimming and color temperature adjustment.
 *
 * <p>Color temperature is measured in Kelvin (K):
 *
 * <ul>
 *   <li>2700K = Warm white (like incandescent bulbs)
 *   <li>4000K = Neutral white
 *   <li>6500K = Cool white (like daylight)
 * </ul>
 */
public class TunableWhiteLight extends DimmableLight {

  /** The minimum supported color temperature in Kelvin. */
  public static final int MIN_COLOR_TEMP = 2700;

  /** The maximum supported color temperature in Kelvin. */
  public static final int MAX_COLOR_TEMP = 6500;

  private int colorTemperature; // in Kelvin

  /**
   * Creates a new tunable white light with the given name.
   *
   * @param name the display name for this light
   */
  public TunableWhiteLight(String name) {
    super(name);
    this.colorTemperature = 2700; // default to warm white
  }

  /**
   * Creates a new tunable white light with a custom startup color temperature.
   *
   * @param name the display name for this light
   * @param startupColorTemperature the initial color temperature in Kelvin
   * @throws IllegalArgumentException if the temperature is outside the valid range
   */
  public TunableWhiteLight(String name, int startupColorTemperature) {
    super(name);
    // Validate and set the startup temperature
    if (startupColorTemperature < MIN_COLOR_TEMP || startupColorTemperature > MAX_COLOR_TEMP) {
      throw new IllegalArgumentException(
          "Color temperature must be between "
              + MIN_COLOR_TEMP
              + "K and "
              + MAX_COLOR_TEMP
              + "K, got: "
              + startupColorTemperature
              + "K");
    }
    this.colorTemperature = startupColorTemperature;
  }

  /**
   * Sets the color temperature of the light.
   *
   * <p>TODO: Implement this method!
   *
   * <p>Requirements:
   *
   * <ul>
   *   <li>Validate that kelvin is between MIN_COLOR_TEMP (2700) and MAX_COLOR_TEMP (6500)
   *   <li>If invalid, throw an IllegalArgumentException with a helpful message
   *   <li>If valid, store the new color temperature
   * </ul>
   *
   * @param kelvin the color temperature in Kelvin (2700-6500)
   * @throws IllegalArgumentException if the temperature is outside the valid range
   */
  public void setColorTemperature(int kelvin) {
    // TODO: Implement this method!
    // HINT: Look at the constructor above for an example of validation
    if (kelvin < MIN_COLOR_TEMP || kelvin > MAX_COLOR_TEMP) {
      throw new IllegalArgumentException(
          "Color temperature must be between "
              + MIN_COLOR_TEMP
              + "K and "
              + MAX_COLOR_TEMP
              + "K, got: "
              + kelvin
              + "K");
    }
    this.colorTemperature = kelvin;
  }

  /**
   * Gets the current color temperature.
   *
   * @return the color temperature in Kelvin
   */
  public int getColorTemperature() {
    return colorTemperature;
  }

  /**
   * Sets the light to "warm" mode (2700K).
   *
   * <p>This is a convenience method for setting a cozy, relaxing atmosphere.
   */
  public void setWarmMode() {
    setColorTemperature(MIN_COLOR_TEMP);
  }

  /**
   * Sets the light to "daylight" mode (6500K).
   *
   * <p>This is a convenience method for setting an energizing, productive atmosphere.
   */
  public void setDaylightMode() {
    setColorTemperature(MAX_COLOR_TEMP);
  }
}
