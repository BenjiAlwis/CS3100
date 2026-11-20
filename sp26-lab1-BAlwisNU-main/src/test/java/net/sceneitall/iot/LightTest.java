package net.sceneitall.iot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for the SceneItAll light device classes.
 *
 * <p>These tests verify the basic functionality of our IoT light implementations.
 */
class LightTest {

  @Nested
  @DisplayName("SwitchedLight tests")
  class SwitchedLightTests {

    private SwitchedLight light = new SwitchedLight("Living Room Light");

    @BeforeEach
    void setUp() {
      light = new SwitchedLight("Living Room Light");
    }

    @Test
    @DisplayName("New light should be off by default")
    void newLightIsOff() {
      assertFalse(light.isOn(), "A new light should start in the off state");
    }

    @Test
    @DisplayName("Light should be on after calling turnOn()")
    void turnOnWorks() {
      light.turnOn();
      assertTrue(light.isOn(), "Light should be on after turnOn()");
    }

    @Test
    @DisplayName("Light should be off after calling turnOff()")
    void turnOffWorks() {
      light.turnOn();
      light.turnOff();
      assertFalse(light.isOn(), "Light should be off after turnOff()");
    }

    @Test
    @DisplayName("Light should return its name")
    void getNameWorks() {
      assertEquals("Living Room Light", light.getName());
    }

    @Test
    @DisplayName("Light should always be available")
    void isAvailableWorks() {
      assertTrue(light.isAvailable());
    }
  }

  @Nested
  @DisplayName("DimmableLight tests")
  class DimmableLightTests {

    private DimmableLight light = new DimmableLight("Bedroom Light");

    @BeforeEach
    void setUp() {
      light = new DimmableLight("Bedroom Light");
    }

    @Test
    @DisplayName("New dimmable light should have 100% brightness")
    void defaultBrightness() {
      assertEquals(100, light.getBrightness());
    }

    @Test
    @DisplayName("setBrightness should update brightness level")
    void setBrightnessWorks() {
      light.setBrightness(50);
      assertEquals(50, light.getBrightness());
    }

    @Test
    @DisplayName("setBrightness(0) should set brightness to 0")
    void canSetBrightnessToZero() {
      light.setBrightness(0);
      assertEquals(0, light.getBrightness());
    }

    @Test
    @DisplayName("turnOn should restore brightness to 100 if it was 0")
    void turnOnRestoresBrightness() {
      light.setBrightness(0);
      light.turnOn();
      assertEquals(100, light.getBrightness());
    }

    // =========================================================================
    // TODO: Add your test here! (Task 3)
    //
    // Write a test that verifies setBrightness() throws IllegalArgumentException
    // when given invalid values like -1 or 101.
    //
    // HINT: Use assertThrows() like this:
    // assertThrows(IllegalArgumentException.class, () -> {
    // // code that should throw
    // });
    // =========================================================================

    @Test
    @DisplayName("setBrightness should reject values outside 0–100")
    void setBrightnessRejectsInvalidValues() {
      assertThrows(IllegalArgumentException.class, () -> light.setBrightness(-1));
      assertThrows(IllegalArgumentException.class, () -> light.setBrightness(101));
    }
  }

  @Nested
  @DisplayName("TunableWhiteLight tests")
  class TunableWhiteLightTests {

    private TunableWhiteLight light = new TunableWhiteLight("Office Light");

    @BeforeEach
    void setUp() {
      light = new TunableWhiteLight("Office Light");
    }

    @Test
    @DisplayName("New tunable light should default to 2700K")
    void defaultColorTemperature() {
      assertEquals(2700, light.getColorTemperature());
    }

    @Test
    @DisplayName("Constructor with custom temperature should work")
    void customStartupTemperature() {
      TunableWhiteLight customLight = new TunableWhiteLight("Custom Light", 4000);
      assertEquals(4000, customLight.getColorTemperature());
    }

    @Test
    @DisplayName("setColorTemperature should update temperature")
    void setColorTemperatureWorks() {
      light.setColorTemperature(5000);
      assertEquals(5000, light.getColorTemperature());
    }

    @Test
    @DisplayName("setColorTemperature should accept minimum value (2700K)")
    void setColorTemperatureMinimum() {
      light.setColorTemperature(2700);
      assertEquals(2700, light.getColorTemperature());
    }

    @Test
    @DisplayName("setColorTemperature should accept maximum value (6500K)")
    void setColorTemperatureMaximum() {
      light.setColorTemperature(6500);
      assertEquals(6500, light.getColorTemperature());
    }

    @Test
    @DisplayName("setColorTemperature should reject values below 2700K")
    void setColorTemperatureRejectsTooLow() {
      assertThrows(
          IllegalArgumentException.class,
          () -> light.setColorTemperature(2699),
          "Should throw for temperature below 2700K");
    }

    @Test
    @DisplayName("setColorTemperature should reject values above 6500K")
    void setColorTemperatureRejectsTooHigh() {
      assertThrows(
          IllegalArgumentException.class,
          () -> light.setColorTemperature(6501),
          "Should throw for temperature above 6500K");
    }
  }
}
