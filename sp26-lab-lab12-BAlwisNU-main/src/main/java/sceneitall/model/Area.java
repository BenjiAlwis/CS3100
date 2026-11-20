package sceneitall.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** A room or zone in the home, containing devices and scenes. */
public final class Area {

  private final String name;
  private final List<Device> devices;
  private final List<Scene> scenes;

  /**
   * Creates a new area with no devices or scenes.
   *
   * @param name the display name for this area (e.g., "Living Room")
   */
  public Area(String name) {
    this.name = name;
    this.devices = new ArrayList<>();
    this.scenes = new ArrayList<>();
  }

  /** Returns the area name. */
  public String getName() {
    return name;
  }

  /** Returns an unmodifiable view of the devices in this area. */
  public List<Device> getDevices() {
    return Collections.unmodifiableList(devices);
  }

  /** Adds a device to this area. */
  public void addDevice(Device device) {
    devices.add(device);
  }

  /** Returns an unmodifiable view of the scenes in this area. */
  public List<Scene> getScenes() {
    return Collections.unmodifiableList(scenes);
  }

  /** Adds a scene to this area. */
  public void addScene(Scene scene) {
    scenes.add(scene);
  }

  /**
   * Finds a device by name.
   *
   * @param deviceName the name to search for
   * @return the device, or empty if not found
   */
  public Optional<Device> findDevice(String deviceName) {
    return devices.stream().filter(d -> d.getName().equals(deviceName)).findFirst();
  }

  @Override
  public String toString() {
    return "Area[" + name + ", devices=" + devices.size() + ", scenes=" + scenes.size() + "]";
  }
}
