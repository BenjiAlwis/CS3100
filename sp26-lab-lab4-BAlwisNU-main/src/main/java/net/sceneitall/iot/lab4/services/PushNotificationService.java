package net.sceneitall.iot.lab4.services;

import net.sceneitall.iot.lab4.domain.User;

/** Service for sending push notifications to users' mobile devices. */
public interface PushNotificationService {
  /**
   * Sends a push notification to the specified user.
   *
   * @param user the user to notify
   * @param message the notification message
   */
  void send(User user, String message);
}
