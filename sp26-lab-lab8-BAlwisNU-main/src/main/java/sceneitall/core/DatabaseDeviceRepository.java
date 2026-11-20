package sceneitall.core;

import java.util.List;

import org.jspecify.annotations.Nullable;

import sceneitall.DatabaseConnection;
import sceneitall.models.DeviceHealth;
import sceneitall.models.IoTDevice;

/**
 * Production DeviceRepository implementation that delegates to DatabaseConnection. Keeps the legacy
 * singleton at the edge of the system (composition root).
 */
public class DatabaseDeviceRepository implements DeviceRepository {

  private final DatabaseConnection db;

  public DatabaseDeviceRepository(DatabaseConnection db) {
    this.db = db;
  }

  @Override
  public void saveDevice(IoTDevice device) {
    db.saveDevice(device);
  }

  @Override
  public List<IoTDevice> loadDevices(String homeId) {
    return db.loadDevices(homeId);
  }

  @Override
  @Nullable
  public DeviceHealth loadDeviceHealth(String deviceId) {
    // DatabaseConnection returns a default OK health if missing
    return db.checkDeviceHealth(deviceId);
  }
}
