package sceneitall;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import sceneitall.core.DeviceManager;
import sceneitall.core.InMemoryDeviceRepository;
import sceneitall.models.DeviceType;
import sceneitall.models.IoTDevice;

/** Unit tests for DeviceManager using an in-memory repository. */
class DeviceManagerTest {

  @Test
  void testAddDevice_andGetDevices() {
    InMemoryDeviceRepository repo = new InMemoryDeviceRepository();
    DeviceManager manager = new DeviceManager(repo);

    IoTDevice device =
        new IoTDevice("dev-1", "temp-home", DeviceType.MOTION_SENSOR, "Porch sensor");
    manager.addDevice("home-123", device);

    assertThat(manager.getDevices("home-123")).extracting(IoTDevice::getId).contains("dev-1");
  }

  @Test
  void testCheckHealth_defaultsToOkWhenMissing() {
    InMemoryDeviceRepository repo = new InMemoryDeviceRepository();
    DeviceManager manager = new DeviceManager(repo);

    assertThat(manager.checkHealth("dev-x").isHealthy()).isTrue();
    assertThat(manager.checkHealth("dev-x").getStatusMessage()).isEqualTo("OK");
  }
}
