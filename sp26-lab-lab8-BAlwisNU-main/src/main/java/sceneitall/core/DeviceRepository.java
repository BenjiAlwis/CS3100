package sceneitall.core;

import java.util.List;

import org.jspecify.annotations.Nullable;

import sceneitall.models.DeviceHealth;
import sceneitall.models.IoTDevice;

/** Persistence interface for Device Management. Narrow by design: only what DeviceManager needs. */
public interface DeviceRepository {
  void saveDevice(IoTDevice device);

  List<IoTDevice> loadDevices(String homeId);

  @Nullable DeviceHealth loadDeviceHealth(String deviceId);
}
