package net.sceneitall.iot.lab4.part1.strategy;

import net.sceneitall.iot.lab4.domain.IoTDevice;

/**
 * Context class that uses a NotificationStrategy.
 *
 * <p>This class demonstrates the Strategy pattern's benefits: - The manager doesn't know which
 * strategy it's using - Strategies can be swapped at runtime - Adding new strategies doesn't
 * require changing this class
 *
 * <p>Compare this to the "mud" version: the conditional logic has been moved out into separate
 * strategy classes.
 */
public class NotificationManager {
  private NotificationStrategy strategy;

  /**
   * Sets the notification strategy to use.
   *
   * @param strategy the strategy to use for notifications
   */
  public void setStrategy(NotificationStrategy strategy) {
    this.strategy = strategy;
  }

  /**
   * Notifies the user about a device event using the current strategy.
   *
   * @param device the device that triggered the notification
   * @param message the notification message
   */
  public void notifyUser(IoTDevice device, String message) {
    if (strategy == null) {
      throw new IllegalStateException("No notification strategy set");
    }

    if (strategy.shouldNotify(device, device.getOwner())) {
      strategy.notify(device.getOwner(), message);
    }
  }
}
