package net.sceneitall.iot.lab4.part1.strategy;

import net.sceneitall.iot.lab4.domain.IoTDevice;
import net.sceneitall.iot.lab4.domain.User;
import net.sceneitall.iot.lab4.services.PushNotificationService;

/**
 * Strategy that sends immediate push notifications for all events.
 *
 * <p>This strategy always notifies immediately, regardless of time of day or user preferences.
 */
public class ImmediateNotificationStrategy implements NotificationStrategy {
  private final PushNotificationService pushService;

  public ImmediateNotificationStrategy(PushNotificationService pushService) {
    this.pushService = pushService;
  }

  @Override
  public void notify(User user, String message) {
    pushService.send(user, message);
  }

  @Override
  public boolean shouldNotify(IoTDevice device, User user) {
    return true; // Always notify immediately
  }
}
