package net.sceneitall.iot.lab4.domain;

/** Represents a smart light device. */
public class Light extends IoTDevice {
  private boolean on;

  /**
   * Constructs a new Light device.
   *
   * @param id the unique identifier for this device
   * @param name the human-readable name of the device
   * @param owner the user who owns this device
   * @param available whether the device is currently available/online
   * @param critical whether this is a critical device
   */
  public Light(String id, String name, User owner, boolean available, boolean critical) {
    super(id, name, owner, available, critical);
    this.on = false;
  }

  /** Simplified constructor for non-critical lights. */
  public Light(String id, String name, User owner) {
    this(id, name, owner, true, false);
  }

  public boolean isOn() {
    return on;
  }

  public void turnOn() {
    this.on = true;
  }

  public void turnOff() {
    this.on = false;
  }
}
