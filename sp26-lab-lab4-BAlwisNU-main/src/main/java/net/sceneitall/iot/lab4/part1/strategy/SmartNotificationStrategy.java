package net.sceneitall.iot.lab4.part1.strategy;

import net.sceneitall.iot.lab4.domain.IoTDevice;
import net.sceneitall.iot.lab4.domain.User;
import net.sceneitall.iot.lab4.services.DigestQueueService;
import net.sceneitall.iot.lab4.services.EmailNotificationService;
import net.sceneitall.iot.lab4.services.PushNotificationService;

/**
 * Strategy that intelligently chooses notification method based on context.
 *
 * <p>Rules: - During quiet hours: queue for digest - Critical devices: push notification - Normal
 * devices: email
 */
public class SmartNotificationStrategy implements NotificationStrategy {
  private final PushNotificationService pushService;
  private final EmailNotificationService emailService;
  private final DigestQueueService digestService;

  public SmartNotificationStrategy(
      PushNotificationService pushService,
      EmailNotificationService emailService,
      DigestQueueService digestService) {
    this.pushService = pushService;
    this.emailService = emailService;
    this.digestService = digestService;
  }

  @Override
  public void notify(User user, String message) {
    // This method is called after shouldNotify() returns true
    // The actual routing logic happens here
    if (user.isInQuietHours()) {
      digestService.addToQueue(user, message);
    } else {
      // Note: We don't have access to the device here, so we default to email
      // In a real implementation, we might pass more context
      emailService.send(user, message);
    }
  }

  /**
   * Alternative notify method that takes the device for better decision-making.
   *
   * @param device the device that triggered the notification
   * @param user the user to notify
   * @param message the message to send
   */
  public void notifyWithDevice(IoTDevice device, User user, String message) {
    if (user.isInQuietHours()) {
      digestService.addToQueue(user, message);
    } else if (device.isCritical()) {
      pushService.send(user, message);
    } else {
      emailService.send(user, message);
    }
  }

  @Override
  public boolean shouldNotify(IoTDevice device, User user) {
    return true; // Always notify, but routing varies
  }
}
