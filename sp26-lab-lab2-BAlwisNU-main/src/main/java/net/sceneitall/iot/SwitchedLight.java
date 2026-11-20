package net.sceneitall.iot;

/**
 * A simple on/off light with no dimming capability.
 *
 * <p>This is the most basic type of smart light - it can only be turned on or off, with no
 * brightness control. Think of it like a traditional light bulb with a smart switch.
 */
public class SwitchedLight extends Light {

  /**
   * Creates a new switched light with the specified ID.
   *
   * @param id the unique identifier for this light
   */
  public SwitchedLight(String id) {
    super(id);
  }
}
