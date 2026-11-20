package net.sceneitall.iot.lab4.part2;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.sceneitall.iot.lab4.domain.DimmableLight;
import net.sceneitall.iot.lab4.domain.IoTDevice;
import net.sceneitall.iot.lab4.domain.Light;
import net.sceneitall.iot.lab4.domain.User;

/**
 * Tests for DeviceFilterExercises.
 *
 * <p>These tests verify that your lambda implementations correctly filter IoT devices based on
 * various criteria.
 */
@DisplayName("Device Filter Exercises")
class DeviceFilterExercisesTest {

  private User testUser;
  private IoTDevice availableDevice;
  private IoTDevice unavailableDevice;
  private Light availableLight;
  private Light unavailableLight;
  private DimmableLight brightDimmableLight;
  private DimmableLight dimDimmableLight;
  private DimmableLight unavailableDimmableLight;
  private IoTDevice criticalDevice;

  /** Sets up test fixtures with various device types and states. */
  @BeforeEach
  void setUp() {
    testUser = new User("user1", "Test User", "test@example.com");

    // Basic devices
    availableDevice = new IoTDevice("dev1", "Available Device", testUser, true, false);
    unavailableDevice = new IoTDevice("dev2", "Unavailable Device", testUser, false, false);
    criticalDevice = new IoTDevice("dev3", "Critical Device", testUser, true, true);

    // Lights
    availableLight = new Light("light1", "Available Light", testUser, true, false);
    unavailableLight = new Light("light2", "Unavailable Light", testUser, false, false);

    // Dimmable lights with different brightness levels
    brightDimmableLight = new DimmableLight("dimlight1", "Bright Light", testUser, true, false);
    brightDimmableLight.setBrightness(80);

    dimDimmableLight = new DimmableLight("dimlight2", "Dim Light", testUser, true, false);
    dimDimmableLight.setBrightness(30);

    unavailableDimmableLight =
        new DimmableLight("dimlight3", "Unavailable Dim Light", testUser, false, false);
    unavailableDimmableLight.setBrightness(90);
  }

  @Test
  @DisplayName("availableDevices() returns predicate that identifies available devices")
  void testAvailableDevices() {
    Predicate<IoTDevice> filter = DeviceFilterExercises.availableDevices();

    assertThat(filter.test(availableDevice)).as("Available device should pass filter").isTrue();
    assertThat(filter.test(unavailableDevice))
        .as("Unavailable device should not pass filter")
        .isFalse();
    assertThat(filter.test(availableLight)).as("Available light should pass filter").isTrue();
    assertThat(filter.test(unavailableLight))
        .as("Unavailable light should not pass filter")
        .isFalse();
  }

  @Test
  @DisplayName("lightsOnly() returns predicate that identifies Light devices")
  void testLightsOnly() {
    Predicate<IoTDevice> filter = DeviceFilterExercises.lightsOnly();

    assertThat(filter.test(availableLight)).as("Light should pass filter").isTrue();
    assertThat(filter.test(unavailableLight))
        .as("Light should pass filter regardless of availability")
        .isTrue();
    assertThat(filter.test(brightDimmableLight))
        .as("DimmableLight (subclass of Light) should pass filter")
        .isTrue();
    assertThat(filter.test(availableDevice))
        .as("Non-Light device should not pass filter")
        .isFalse();
  }

  @Test
  @DisplayName("criticalDevices() returns predicate that identifies critical devices")
  void testCriticalDevices() {
    Predicate<IoTDevice> filter = DeviceFilterExercises.criticalDevices();

    assertThat(filter.test(criticalDevice)).as("Critical device should pass filter").isTrue();
    assertThat(filter.test(availableDevice))
        .as("Non-critical device should not pass filter")
        .isFalse();
    assertThat(filter.test(availableLight))
        .as("Non-critical light should not pass filter")
        .isFalse();
  }

  @Test
  @DisplayName("brightnessAbove() returns predicate that checks DimmableLight brightness")
  void testBrightnessAbove() {
    Predicate<IoTDevice> filter = DeviceFilterExercises.brightnessAbove(50);

    assertThat(filter.test(brightDimmableLight))
        .as("DimmableLight with brightness 80 should pass filter for threshold 50")
        .isTrue();
    assertThat(filter.test(dimDimmableLight))
        .as("DimmableLight with brightness 30 should not pass filter for threshold 50")
        .isFalse();
    assertThat(filter.test(availableLight))
        .as("Regular Light (not dimmable) should not pass filter")
        .isFalse();
    assertThat(filter.test(availableDevice))
        .as("Non-Light device should not pass filter")
        .isFalse();
  }

  @Test
  @DisplayName("brightnessAbove() edge case: exactly at threshold")
  void testBrightnessAboveEdgeCase() {
    DimmableLight edgeLight = new DimmableLight("edge", "Edge Light", testUser, true, false);
    edgeLight.setBrightness(50);

    Predicate<IoTDevice> filter = DeviceFilterExercises.brightnessAbove(50);

    assertThat(filter.test(edgeLight))
        .as("DimmableLight with brightness exactly at threshold should not pass (> not >=)")
        .isFalse();
  }

  @Test
  @DisplayName("availableLights() uses predicate composition with and()")
  void testAvailableLights() {
    Predicate<IoTDevice> filter = DeviceFilterExercises.availableLights();

    assertThat(filter.test(availableLight)).as("Available light should pass filter").isTrue();
    assertThat(filter.test(brightDimmableLight))
        .as("Available DimmableLight should pass filter")
        .isTrue();
    assertThat(filter.test(unavailableLight))
        .as("Unavailable light should not pass filter")
        .isFalse();
    assertThat(filter.test(availableDevice))
        .as("Available non-Light device should not pass filter")
        .isFalse();
  }

  @Test
  @DisplayName("needsAttention() uses predicate composition with or() and negate()")
  void testNeedsAttention() {
    Predicate<IoTDevice> filter = DeviceFilterExercises.needsAttention();

    assertThat(filter.test(unavailableDevice)).as("Unavailable device needs attention").isTrue();
    assertThat(filter.test(criticalDevice)).as("Critical device needs attention").isTrue();
    assertThat(filter.test(availableDevice))
        .as("Available non-critical device does not need attention")
        .isFalse();

    // Edge case: unavailable AND critical
    IoTDevice unavailableCritical =
        new IoTDevice("dev4", "Unavailable Critical", testUser, false, true);
    assertThat(filter.test(unavailableCritical))
        .as("Unavailable critical device needs attention")
        .isTrue();
  }

  @Test
  @DisplayName("findBrightLights() filters for available dimmable lights with brightness > 50")
  void testFindBrightLights() {
    List<IoTDevice> devices =
        Arrays.asList(
            availableDevice,
            unavailableDevice,
            availableLight,
            unavailableLight,
            brightDimmableLight, // Should be included: available, dimmable, brightness 80
            dimDimmableLight, // Should NOT be included: brightness only 30
            unavailableDimmableLight, // Should NOT be included: unavailable
            criticalDevice);

    List<IoTDevice> result = DeviceFilterExercises.findBrightLights(devices);

    assertThat(result)
        .as("Should find only available dimmable lights with brightness > 50")
        .hasSize(1)
        .containsExactly(brightDimmableLight);
  }

  @Test
  @DisplayName("findBrightLights() returns empty list when no devices match")
  void testFindBrightLightsEmpty() {
    List<IoTDevice> devices = Arrays.asList(availableDevice, dimDimmableLight, unavailableLight);

    List<IoTDevice> result = DeviceFilterExercises.findBrightLights(devices);

    assertThat(result).as("Should return empty list when no devices match").isEmpty();
  }

  @Test
  @DisplayName("findBrightLights() handles multiple matching devices")
  void testFindBrightLightsMultiple() {
    DimmableLight anotherBright = new DimmableLight("dimlight4", "Another Bright", testUser);
    anotherBright.setBrightness(75);

    List<IoTDevice> devices = Arrays.asList(brightDimmableLight, dimDimmableLight, anotherBright);

    List<IoTDevice> result = DeviceFilterExercises.findBrightLights(devices);

    assertThat(result)
        .as("Should find all matching devices")
        .hasSize(2)
        .containsExactlyInAnyOrder(brightDimmableLight, anotherBright);
  }
}
