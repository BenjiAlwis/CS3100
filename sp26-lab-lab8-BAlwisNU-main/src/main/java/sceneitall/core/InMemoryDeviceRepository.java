package sceneitall.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import sceneitall.models.DeviceHealth;
import sceneitall.models.IoTDevice;

/** In-memory DeviceRepository for unit tests. */
public class InMemoryDeviceRepository implements DeviceRepository {

  private final Map<String, List<IoTDevice>> devicesByHome = new HashMap<>();
  private final Map<String, DeviceHealth> healthByDeviceId = new HashMap<>();

  @Override
  public void saveDevice(IoTDevice device) {
    String homeId = device.getHomeId();
    if (homeId == null) {
      throw new IllegalArgumentException("device.homeId must not be null");
    }
    devicesByHome.computeIfAbsent(homeId, k -> new ArrayList<>()).add(device);
  }

  @Override
  public List<IoTDevice> loadDevices(String homeId) {
    return new ArrayList<>(devicesByHome.getOrDefault(homeId, List.of()));
  }

  @Override
  @Nullable
  public DeviceHealth loadDeviceHealth(String deviceId) {
    return healthByDeviceId.get(deviceId);
  }

  // Optional helper for tests
  public void saveDeviceHealth(DeviceHealth health) {
    String id = health.getDeviceId();
    if (id == null) {
      throw new IllegalArgumentException("health.deviceId must not be null");
    }
    healthByDeviceId.put(id, health);
  }
}
