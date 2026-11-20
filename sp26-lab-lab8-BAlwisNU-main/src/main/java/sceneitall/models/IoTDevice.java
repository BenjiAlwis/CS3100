package sceneitall.models;

import org.jspecify.annotations.Nullable;

/**
 * Represents an IoT device such as a camera, thermostat, light, or sensor. Tracks the device's
 * home, type, name, and online status.
 */
public class IoTDevice {
  @Nullable private String id;
  @Nullable private String homeId;
  @Nullable private DeviceType type;
  @Nullable private String name;
  private boolean online;

  /** Creates an empty IoT device. */
  public IoTDevice() {}

  /**
   * Creates an IoT device with the given fields.
   *
   * @param id the device identifier
   * @param homeId the home this device belongs to
   * @param type the device type
   * @param name the device name
   */
  public IoTDevice(String id, String homeId, DeviceType type, String name) {
    this.id = id;
    this.homeId = homeId;
    this.type = type;
    this.name = name;
    this.online = true;
  }

  /** Returns the device identifier. */
  @Nullable
  public String getId() {
    return id;
  }

  /** Sets the device identifier. */
  public void setId(String id) {
    this.id = id;
  }

  /** Returns the home this device belongs to. */
  @Nullable
  public String getHomeId() {
    return homeId;
  }

  /** Sets the home ID. */
  public void setHomeId(String homeId) {
    this.homeId = homeId;
  }

  /** Returns the device type. */
  @Nullable
  public DeviceType getType() {
    return type;
  }

  /** Sets the device type. */
  public void setType(DeviceType type) {
    this.type = type;
  }

  /** Returns the device name. */
  @Nullable
  public String getName() {
    return name;
  }

  /** Sets the device name. */
  public void setName(String name) {
    this.name = name;
  }

  /** Returns true if the device is online. */
  public boolean isOnline() {
    return online;
  }

  /** Sets whether the device is online. */
  public void setOnline(boolean online) {
    this.online = online;
  }
}
