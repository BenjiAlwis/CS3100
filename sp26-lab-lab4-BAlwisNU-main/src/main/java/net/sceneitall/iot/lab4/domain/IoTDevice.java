package net.sceneitall.iot.lab4.domain;

/** Base class for all IoT devices in the SceneItAll system. */
public class IoTDevice {
  private final String id;
  private final String name;
  private final User owner;
  private boolean available;
  private boolean critical;

  /**
   * Constructs a new IoT device.
   *
   * @param id the unique identifier for this device
   * @param name the human-readable name of the device
   * @param owner the user who owns this device
   * @param available whether the device is currently available/online
   * @param critical whether this is a critical device (security, safety, etc.)
   */
  public IoTDevice(String id, String name, User owner, boolean available, boolean critical) {
    this.id = id;
    this.name = name;
    this.owner = owner;
    this.available = available;
    this.critical = critical;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public User getOwner() {
    return owner;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public boolean isCritical() {
    return critical;
  }

  public void setCritical(boolean critical) {
    this.critical = critical;
  }
}
