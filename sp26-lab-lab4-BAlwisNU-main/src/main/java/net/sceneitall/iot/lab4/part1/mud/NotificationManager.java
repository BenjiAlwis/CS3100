package net.sceneitall.iot.lab4.part1.mud;

import net.sceneitall.iot.lab4.domain.IoTDevice;
import net.sceneitall.iot.lab4.domain.User;
import net.sceneitall.iot.lab4.services.DigestQueueService;
import net.sceneitall.iot.lab4.services.EmailNotificationService;
import net.sceneitall.iot.lab4.services.PushNotificationService;

/**
 * "Big ball of mud" implementation: all notification logic in one class with conditionals.
 *
 * <p>This class demonstrates control coupling (depends on NotificationType enum) and low cohesion
 * (handles multiple concerns: routing, timing logic, and delivery).
 */
public class NotificationManager {
  private final PushNotificationService pushService;
  private final EmailNotificationService emailService;
  private final DigestQueueService digestService;

  public NotificationManager(
      PushNotificationService pushService,
      EmailNotificationService emailService,
      DigestQueueService digestService) {
    this.pushService = pushService;
    this.emailService = emailService;
    this.digestService = digestService;
  }

  /**
   * Notifies a user about a device issue using the specified notification type.
   *
   * <p>This method demonstrates how control coupling makes code harder to change: - Adding a new
   * notification type requires modifying this method - Different notification types are coupled
   * through the shared conditional logic - Testing one type requires understanding all types
   *
   * @param device the device that triggered the notification
   * @param message the notification message
   * @param type the type of notification to send
   */
  public void notifyUser(IoTDevice device, String message, NotificationType type) {
    User owner = device.getOwner();

    if (type == NotificationType.IMMEDIATE) {
      // Send immediately via push notification
      sendPushNotification(owner, message);
    } else if (type == NotificationType.DAILY_DIGEST) {
      // Queue for daily digest
      addToDigestQueue(owner, message);
    } else if (type == NotificationType.SMART) {
      // Check time and preferences
      if (isQuietHours(owner)) {
        addToDigestQueue(owner, message);
      } else if (device.isCritical()) {
        sendPushNotification(owner, message);
      } else {
        sendEmailNotification(owner, message);
      }
    }
  }

  private void sendPushNotification(User user, String message) {
    pushService.send(user, message);
  }

  private void sendEmailNotification(User user, String message) {
    emailService.send(user, message);
  }

  private void addToDigestQueue(User user, String message) {
    digestService.addToQueue(user, message);
  }

  private boolean isQuietHours(User user) {
    return user.isInQuietHours();
  }
}
