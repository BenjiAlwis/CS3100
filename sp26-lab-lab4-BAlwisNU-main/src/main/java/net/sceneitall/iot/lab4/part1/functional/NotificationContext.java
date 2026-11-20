package net.sceneitall.iot.lab4.part1.functional;

import net.sceneitall.iot.lab4.domain.IoTDevice;
import net.sceneitall.iot.lab4.domain.User;

/**
 * Context object that encapsulates information needed for notification decisions.
 *
 * <p>This class is used with predicates in the functional approach to provide all necessary context
 * for deciding whether and how to send notifications.
 *
 * <p>By bundling device and user together, we create a single parameter that predicates can test,
 * making the functional interface cleaner: {@code Predicate<NotificationContext>} instead of {@code
 * BiPredicate<IoTDevice, User>}.
 */
public class NotificationContext {
  private final IoTDevice device;
  private final User user;

  /**
   * Creates a new notification context.
   *
   * @param device the device that triggered the notification
   * @param user the user who owns the device
   */
  public NotificationContext(IoTDevice device, User user) {
    this.device = device;
    this.user = user;
  }

  /**
   * Gets the device that triggered the notification.
   *
   * @return the device
   */
  public IoTDevice getDevice() {
    return device;
  }

  /**
   * Gets the user who owns the device.
   *
   * @return the user
   */
  public User getUser() {
    return user;
  }
}
