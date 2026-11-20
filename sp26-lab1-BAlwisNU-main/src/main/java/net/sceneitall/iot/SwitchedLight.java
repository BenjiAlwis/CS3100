package net.sceneitall.iot;

/**
 * A basic on/off light with no dimming capability.
 *
 * <p>This is the simplest type of smart light - it can only be turned fully on or fully off.
 */
public class SwitchedLight implements Light {

  private final String name;
  private boolean on;

  /**
   * Creates a new switched light with the given name.
   *
   * @param name the display name for this light
   */
  public SwitchedLight(String name) {
    this.name = name;
    this.on = false;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void identify() {
    // Flash the light to help identify it
    System.out.println("Light '" + name + "' is flashing to identify itself!");
  }

  @Override
  public boolean isAvailable() {
    // For this simulation, the light is always available
    return true;
  }

  @Override
  public void turnOn() {
    this.on = true;
  }

  @Override
  public void turnOff() {
    this.on = false;
  }

  @Override
  public boolean isOn() {
    return on;
  }
}
