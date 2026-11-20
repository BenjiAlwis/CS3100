package net.sceneitall.iot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for the SceneItAll fan device class.
 *
 * <p>These tests verify the basic functionality of our IoT fan implementation.
 */
class FanTest {

  @Nested
  @DisplayName("Fan tests")
  class FanTests {

    private Fan fan = new Fan("Ceiling Fan");

    @BeforeEach
    void setUp() {
      fan = new Fan("Ceiling Fan");
    }

    @Test
    @DisplayName("New fan should be off by default")
    void newFanIsOff() {
      // This test will FAIL until you fix the bug in Fan.java!
      assertFalse(fan.isRunning(), "A new fan should start in the off state");
    }

    @Test
    @DisplayName("Fan should be running after calling turnOn()")
    void turnOnWorks() {
      fan.turnOn();
      // This test will FAIL until you fix the bug in Fan.java!
      assertTrue(fan.isRunning(), "Fan should be running after turnOn()");
    }

    @Test
    @DisplayName("Fan should not be running after calling turnOff()")
    void turnOffWorks() {
      fan.turnOn();
      fan.turnOff();
      // This test will FAIL until you fix the bug in Fan.java!
      assertFalse(fan.isRunning(), "Fan should not be running after turnOff()");
    }

    @Test
    @DisplayName("Fan default speed should be LOW")
    void defaultSpeed() {
      assertEquals(FanSpeed.LOW, fan.getSpeed());
    }

    @Test
    @DisplayName("setSpeed should update fan speed")
    void setSpeedWorks() {
      fan.setSpeed(FanSpeed.HIGH);
      assertEquals(FanSpeed.HIGH, fan.getSpeed());
    }

    @Test
    @DisplayName("setSpeed should work with all speed values")
    void setSpeedWorksWithAllValues() {
      fan.setSpeed(FanSpeed.LOW);
      assertEquals(FanSpeed.LOW, fan.getSpeed());

      fan.setSpeed(FanSpeed.MEDIUM);
      assertEquals(FanSpeed.MEDIUM, fan.getSpeed());

      fan.setSpeed(FanSpeed.HIGH);
      assertEquals(FanSpeed.HIGH, fan.getSpeed());
    }
  }
}
