package net.sceneitall.iot.lab4.domain;

/** Represents a smart light that supports dimming (brightness control). */
public class DimmableLight extends Light {
  private int brightness; // 0-100

  /**
   * Constructs a new DimmableLight device.
   *
   * @param id the unique identifier for this device
   * @param name the human-readable name of the device
   * @param owner the user who owns this device
   * @param available whether the device is currently available/online
   * @param critical whether this is a critical device
   */
  public DimmableLight(String id, String name, User owner, boolean available, boolean critical) {
    super(id, name, owner, available, critical);
    this.brightness = 100; // Default to full brightness
  }

  /** Simplified constructor for non-critical dimmable lights. */
  public DimmableLight(String id, String name, User owner) {
    this(id, name, owner, true, false);
  }

  /**
   * Gets the current brightness level.
   *
   * @return the brightness level (0-100)
   */
  public int getBrightness() {
    return brightness;
  }

  /**
   * Sets the brightness level.
   *
   * @param brightness the brightness level (0-100)
   * @throws IllegalArgumentException if brightness is not in range [0, 100]
   */
  public void setBrightness(int brightness) {
    if (brightness < 0 || brightness > 100) {
      throw new IllegalArgumentException("Brightness must be between 0 and 100");
    }
    this.brightness = brightness;
  }
}
