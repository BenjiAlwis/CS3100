package net.sceneitall.iot.lab4.services;

import net.sceneitall.iot.lab4.domain.User;

/** Service for queuing notifications for daily digest delivery. */
public interface DigestQueueService {
  /**
   * Adds a notification to the user's daily digest queue.
   *
   * @param user the user whose digest queue should receive the notification
   * @param message the notification message
   */
  void addToQueue(User user, String message);
}
