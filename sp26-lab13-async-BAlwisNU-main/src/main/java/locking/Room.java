package locking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A room containing hub devices. Also used as the fine-grained lock in {@link HubFineLock} — {@code
 * synchronized (room) { ... }}.
 */
public final class Room {

  private final int id;
  private final List<Device> devices;

  public Room(int id) {
    this.id = id;
    this.devices = new ArrayList<>();
  }

  public int getId() {
    return id;
  }

  public void addDevice(Device device) {
    devices.add(device);
  }

  public List<Device> getDevices() {
    return Collections.unmodifiableList(devices);
  }
}
