package sceneitall.core;

import sceneitall.models.NotificationChannel;

/** Port for delivering notifications without exposing HTTP/transport details. */
public interface NotificationPort {
  void send(String userId, String message, NotificationChannel channel);
}