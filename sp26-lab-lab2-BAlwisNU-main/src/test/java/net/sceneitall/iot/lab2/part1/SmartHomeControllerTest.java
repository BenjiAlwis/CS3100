package net.sceneitall.iot.lab2.part1;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sceneitall.iot.DimmableLight;
import net.sceneitall.iot.Fan;
import net.sceneitall.iot.TunableWhiteLight;

/**
 * Tests for SmartHomeController.
 *
 * <p><b>Lab Exercise 1.2:</b> Write JUnit tests that verify your SmartHomeController implementation
 * works with a mix of device types.
 *
 * <p>This class includes some example tests to get you started. Complete the TODO tests at the
 * bottom of this file.
 */
class SmartHomeControllerTest {

  // ============================================================
  // Example Tests (provided)
  // ============================================================

  @Test
  void identifyAllDevicesWorksWithMixedDeviceTypes() {
    SmartHomeController controller = new SmartHomeController();

    // Add different device types - they should all work!
    controller.addDevice(new Fan("ceiling-fan"));
    controller.addDevice(new DimmableLight("desk-lamp", 75));
    controller.addDevice(new TunableWhiteLight("living-room", 2700, 100));

    // This should not throw any exceptions
    assertDoesNotThrow(() -> controller.identifyAllDevices());
  }

  @Test
  void addDeviceIncreasesDeviceCount() {
    SmartHomeController controller = new SmartHomeController();

    // Initially, no devices
    assertEquals(0, controller.getDeviceCount(), "New controller should have 0 devices");

    // Add one device
    controller.addDevice(new Fan("fan-1"));
    assertEquals(1, controller.getDeviceCount(), "After adding 1 device, count should be 1");

    // Add another device
    controller.addDevice(new DimmableLight("light-1", 50));
    assertEquals(2, controller.getDeviceCount(), "After adding 2 devices, count should be 2");
  }

  @Test
  void countAvailableDevicesOnlyCountsAvailableDevices() {
    SmartHomeController controller = new SmartHomeController();

    // Create devices with different availability states
    Fan fan = new Fan("fan-1");
    DimmableLight light = new DimmableLight("light-1", 100);

    fan.setAvailability(true); // Available
    light.setAvailability(false); // Not available

    controller.addDevice(fan);
    controller.addDevice(light);

    // Only the fan should be counted as available
    assertEquals(1, controller.countAvailableDevices(), "Should only count available devices");
  }

  // ============================================================
  // TODO: Complete these tests
  // ============================================================

  @Test
  void countAvailableDevicesCountsAllDeviceTypes() {
    // TODO: Add at least 3 different device types (Fan, DimmableLight, TunableWhiteLight)
    // Make sure all are available
    // Use assertEquals to verify the count matches the number you added
    SmartHomeController controller = new SmartHomeController();

    Fan fan = new Fan("fan");
    DimmableLight dimmable = new DimmableLight("lamp", 50);
    TunableWhiteLight tunable = new TunableWhiteLight("living-room", 3000, 80);

    // All devices are available by default, but we set explicitly for clarity
    fan.setAvailability(true);
    dimmable.setAvailability(true);
    tunable.setAvailability(true);

    controller.addDevice(fan);
    controller.addDevice(dimmable);
    controller.addDevice(tunable);

    assertEquals(3, controller.countAvailableDevices(), "Should count all available device types");
  }

  @Test
  void countAvailableDevicesReturnsZeroWhenEmpty() {
    // TODO: Test that a new controller returns 0 available devices
    // Use assertEquals to verify this

    SmartHomeController controller = new SmartHomeController();

    assertEquals(
        0, controller.countAvailableDevices(), "Empty controller should have 0 available devices");
  }

  @Test
  void identifyAllDevicesWorksWithEmptyController() {
    // TODO: Test that calling identifyAllDevices on an empty controller doesn't crash
    // Use assertDoesNotThrow

    SmartHomeController controller = new SmartHomeController();

    assertDoesNotThrow(
        () -> controller.identifyAllDevices(),
        "identifyAllDevices should not throw when no devices exist");
  }
}
