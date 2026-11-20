package net.sceneitall.iot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Light hierarchy.
 *
 * <p>These tests verify the behavior of SwitchedLight, DimmableLight, and TunableWhiteLight.
 */
class LightTest {

  @Nested
  class SwitchedLightTests {

    @Test
    void turnOnSetsIsOnToTrue() {
      SwitchedLight light = new SwitchedLight("test-light");
      assertFalse(light.isOn(), "Light should start off");

      light.turnOn();
      assertTrue(light.isOn(), "Light should be on after turnOn()");
    }

    @Test
    void turnOffSetsIsOnToFalse() {
      SwitchedLight light = new SwitchedLight("test-light");
      light.turnOn();
      assertTrue(light.isOn(), "Light should be on after turnOn()");

      light.turnOff();
      assertFalse(light.isOn(), "Light should be off after turnOff()");
    }

    @Test
    void getIdReturnsCorrectId() {
      SwitchedLight light = new SwitchedLight("my-switch");
      assertEquals("my-switch", light.getId());
    }

    @Test
    void isAvailableDefaultsToTrue() {
      SwitchedLight light = new SwitchedLight("test-light");
      assertTrue(light.isAvailable(), "Light should be available by default");
    }
  }

  @Nested
  class DimmableLightTests {

    @Test
    void setBrightnessAcceptsValidValues() {
      DimmableLight light = new DimmableLight("test-light", 50);

      light.setBrightness(0);
      assertEquals(0, light.getBrightness());

      light.setBrightness(100);
      assertEquals(100, light.getBrightness());

      light.setBrightness(50);
      assertEquals(50, light.getBrightness());
    }

    @Test
    void setBrightnessRejectsNegativeValues() {
      DimmableLight light = new DimmableLight("test-light", 50);
      assertThrows(IllegalArgumentException.class, () -> light.setBrightness(-1));
    }

    @Test
    void setBrightnessRejectsValuesOver100() {
      DimmableLight light = new DimmableLight("test-light", 50);
      assertThrows(IllegalArgumentException.class, () -> light.setBrightness(101));
    }

    @Test
    void constructorSetsInitialBrightness() {
      DimmableLight light = new DimmableLight("test-light", 75);
      assertEquals(75, light.getBrightness());
    }
  }

  @Nested
  class TunableWhiteLightTests {

    @Test
    void setColorTemperatureAcceptsValidValues() {
      TunableWhiteLight light = new TunableWhiteLight("test-light", 4000, 100);

      light.setColorTemperature(2700);
      assertEquals(2700, light.getColorTemperature());

      light.setColorTemperature(6500);
      assertEquals(6500, light.getColorTemperature());
    }

    @Test
    void setColorTemperatureRejectsValuesBelowMin() {
      TunableWhiteLight light = new TunableWhiteLight("test-light", 4000, 100);
      assertThrows(IllegalArgumentException.class, () -> light.setColorTemperature(2000));
    }

    @Test
    void setColorTemperatureRejectsValuesAboveMax() {
      TunableWhiteLight light = new TunableWhiteLight("test-light", 4000, 100);
      assertThrows(IllegalArgumentException.class, () -> light.setColorTemperature(7000));
    }

    @Test
    void constructorSetsInitialValues() {
      TunableWhiteLight light = new TunableWhiteLight("test-light", 3500, 80);
      assertEquals(3500, light.getColorTemperature());
      assertEquals(80, light.getBrightness());
    }
  }
}
