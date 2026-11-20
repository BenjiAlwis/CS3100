package net.sceneitall.iot.lab4.part1.strategy;

import net.sceneitall.iot.lab4.domain.IoTDevice;
import net.sceneitall.iot.lab4.domain.User;
import net.sceneitall.iot.lab4.services.DigestQueueService;

/**
 * Strategy that queues all notifications for daily digest delivery.
 *
 * <p>This strategy is appropriate for non-urgent updates that can wait for batch delivery.
 */
public class DailyDigestNotificationStrategy implements NotificationStrategy {
  private final DigestQueueService digestService;

  public DailyDigestNotificationStrategy(DigestQueueService digestService) {
    this.digestService = digestService;
  }

  @Override
  public void notify(User user, String message) {
    digestService.addToQueue(user, message);
  }

  @Override
  public boolean shouldNotify(IoTDevice device, User user) {
    return true; // Always queue for digest
  }
}
