package net.sceneitall.iot.lab2.stretch;

import net.sceneitall.iot.DimmableLight;
import net.sceneitall.iot.Fan;
import net.sceneitall.iot.FanSpeed;
import net.sceneitall.iot.IoTDevice;

/**
 * A combination device that includes both a ceiling fan and a light.
 *
 * <p>This class demonstrates the <b>composition</b> design pattern: rather than trying to inherit
 * from both Fan and Light (which Java doesn't allow), this device <i>has</i> a Fan and <i>has</i> a
 * Light as separate components.
 *
 * <p><b>Stretch Goal 2:</b> Analyze this class and discuss the tradeoffs between composition and
 * inheritance.
 *
 * <p>Key questions to consider:
 *
 * <ul>
 *   <li>Why doesn't this class extend both Light and Fan?
 *   <li>What's the advantage of the composition approach?
 *   <li>What's a disadvantage?
 *   <li>When would inheritance be a better choice?
 * </ul>
 */
public class CeilingFanWithLight implements IoTDevice {

  private final String id;
  private final Fan fan;
  private final DimmableLight light;
  private boolean isAvailable;

  /**
   * Creates a new ceiling fan with integrated light.
   *
   * @param id the unique identifier for this device
   */
  public CeilingFanWithLight(String id) {
    this.id = id;
    // Create the component devices with derived IDs
    this.fan = new Fan(id + "-fan");
    this.light = new DimmableLight(id + "-light", 100);
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
    // Propagate to components
    fan.setAvailability(available);
    light.setAvailability(available);
  }

  @Override
  public void identify() {
    System.out.println(
        "CeilingFanWithLight [" + id + "] is spinning fan and flashing light to identify itself!");
    fan.identify();
    light.identify();
  }

  // ============ Fan-related methods (delegation) ============

  /**
   * Gets the current fan speed.
   *
   * @return the fan speed
   */
  public FanSpeed getFanSpeed() {
    return fan.getSpeed();
  }

  /**
   * Sets the fan speed.
   *
   * @param speed the desired speed
   */
  public void setFanSpeed(FanSpeed speed) {
    fan.setSpeed(speed);
  }

  /**
   * Checks if the fan is running.
   *
   * @return true if the fan is running
   */
  public boolean isFanRunning() {
    return fan.isRunning();
  }

  // ============ Light-related methods (delegation) ============

  /** Turns the light on. */
  public void turnLightOn() {
    light.turnOn();
  }

  /** Turns the light off. */
  public void turnLightOff() {
    light.turnOff();
  }

  /**
   * Gets the light brightness.
   *
   * @return brightness from 0 to 100
   */
  public int getLightBrightness() {
    return light.getBrightness();
  }

  /**
   * Sets the light brightness.
   *
   * @param brightness brightness from 0 to 100
   */
  public void setLightBrightness(int brightness) {
    light.setBrightness(brightness);
  }

  /**
   * Checks if the light is on.
   *
   * @return true if the light is on
   */
  public boolean isLightOn() {
    return light.isOn();
  }

  // ============ Convenience methods ============

  /**
   * Gets the underlying fan component.
   *
   * <p>This exposes the component for direct access if needed, though using the delegation methods
   * above is preferred.
   *
   * @return the fan component
   */
  public Fan getFan() {
    return fan;
  }

  /**
   * Gets the underlying light component.
   *
   * <p>This exposes the component for direct access if needed, though using the delegation methods
   * above is preferred.
   *
   * @return the light component
   */
  public DimmableLight getLight() {
    return light;
  }
}
