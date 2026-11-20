package net.sceneitall.iot.lab4.part1.strategy;

import net.sceneitall.iot.lab4.domain.IoTDevice;
import net.sceneitall.iot.lab4.domain.User;

/** Strategy interface for different notification approaches. */
public interface NotificationStrategy {
  /**
   * Sends a notification to the user.
   *
   * @param user the user to notify
   * @param message the notification message
   */
  void notify(User user, String message);

  /**
   * Determines whether a notification should be sent for this device and user.
   *
   * @param device the device that triggered the notification
   * @param user the user who owns the device
   * @return true if a notification should be sent
   */
  boolean shouldNotify(IoTDevice device, User user);
}
