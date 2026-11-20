package net.sceneitall.iot.lab9.model;

/** A registered IoT device in the SceneItAll hub. */
public class IoTDevice {

  private final String id;
  private final DeviceType type;

  public IoTDevice(String id, DeviceType type) {
    this.id = id;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public DeviceType getType() {
    return type;
  }
}
