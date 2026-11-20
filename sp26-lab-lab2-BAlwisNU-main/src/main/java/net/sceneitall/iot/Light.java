package net.sceneitall.iot;

/**
 * Abstract base class for all light devices in the smart home system.
 *
 * <p>This class provides common functionality for lights, including: - Power state management
 * (on/off) - Availability tracking - Device identification
 *
 * <p>Subclasses extend this to add specific features like dimming or color temperature control.
 */
public abstract class Light implements IoTDevice {

  private final String id;
  private boolean isOn;
  private boolean isAvailable;

  /**
   * Creates a new light with the specified ID.
   *
   * @param id the unique identifier for this light
   */
  protected Light(String id) {
    this.id = id;
    this.isOn = false;
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
  }

  private void blink(int times) {
    for (int i = 0; i < times; i++) {
      this.turnOn();
      try {
        // Wait for 1 second
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // We might be interrupted while waiting, if so, we print an error message
        e.printStackTrace();
      }
      this.turnOff();
    }
  }

  @Override
  public void identify() {
    this.blink(3);
  }

  /**
   * Turns this light on.
   *
   * <p>Subclasses may override this to add additional behavior (e.g., restoring previous brightness
   * level).
   */
  public void turnOn() {
    this.isOn = true;
    System.out.println("Light [" + id + "] turned ON");
  }

  /**
   * Turns this light off.
   *
   * <p>Subclasses may override this to add additional behavior (e.g., saving current state).
   */
  public void turnOff() {
    this.isOn = false;
    System.out.println("Light [" + id + "] turned OFF");
  }

  /**
   * Checks whether this light is currently on.
   *
   * @return true if the light is on, false otherwise
   */
  public boolean isOn() {
    return isOn;
  }

  /**
   * Sets the power state of this light directly.
   *
   * @param on true to turn on, false to turn off
   */
  protected void setOn(boolean on) {
    this.isOn = on;
  }
}
