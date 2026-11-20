package net.sceneitall.iot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Tests for the Fan class. */
class FanTest {

  @Nested
  class FanTests {

    @Test
    void constructorStartsOff() {
      Fan fan = new Fan("test-fan");
      assertEquals(FanSpeed.OFF, fan.getSpeed());
      assertFalse(fan.isRunning());
    }

    @Test
    void setSpeedChangesSpeed() {
      Fan fan = new Fan("test-fan");

      fan.setSpeed(FanSpeed.LOW);
      assertEquals(FanSpeed.LOW, fan.getSpeed());
      assertTrue(fan.isRunning());

      fan.setSpeed(FanSpeed.MEDIUM);
      assertEquals(FanSpeed.MEDIUM, fan.getSpeed());

      fan.setSpeed(FanSpeed.HIGH);
      assertEquals(FanSpeed.HIGH, fan.getSpeed());

      fan.setSpeed(FanSpeed.OFF);
      assertEquals(FanSpeed.OFF, fan.getSpeed());
      assertFalse(fan.isRunning());
    }

    @Test
    void turnOffSetsSpeedToOff() {
      Fan fan = new Fan("test-fan");
      fan.setSpeed(FanSpeed.HIGH);
      assertTrue(fan.isRunning());

      fan.turnOff();
      assertEquals(FanSpeed.OFF, fan.getSpeed());
      assertFalse(fan.isRunning());
    }

    @Test
    void getIdReturnsCorrectId() {
      Fan fan = new Fan("ceiling-fan");
      assertEquals("ceiling-fan", fan.getId());
    }

    @Test
    void isAvailableDefaultsToTrue() {
      Fan fan = new Fan("test-fan");
      assertTrue(fan.isAvailable());
    }

    @Test
    void setAvailabilityChangesAvailability() {
      Fan fan = new Fan("test-fan");
      assertTrue(fan.isAvailable());

      fan.setAvailability(false);
      assertFalse(fan.isAvailable());

      fan.setAvailability(true);
      assertTrue(fan.isAvailable());
    }
  }
}
