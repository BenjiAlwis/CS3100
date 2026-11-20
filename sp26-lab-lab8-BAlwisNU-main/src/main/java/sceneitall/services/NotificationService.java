package sceneitall.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import sceneitall.DatabaseConnection;
import sceneitall.models.NotificationChannel;
import sceneitall.models.NotificationPreference;
import sceneitall.util.DateUtils;

/**
 * Sends notifications via push, email, or SMS. Loads user preferences to determine delivery
 * channels and quiet hours before sending.
 */
public class NotificationService {

  private static final String PUSH_ENDPOINT = "http://push-service/send";
  private static final String EMAIL_ENDPOINT = "http://email-service/send";
  private static final String SMS_ENDPOINT = "http://sms-service/send";

  /**
   * Sends an alert to the specified user on the given channel. Respects the user's notification
   * preferences and quiet hours.
   *
   * @param userId the user to notify
   * @param message the alert message
   * @param channel the delivery channel
   */
  public void sendAlert(String userId, String message, NotificationChannel channel) {
    List<NotificationPreference> prefs =
        DatabaseConnection.getInstance().loadNotificationPreferences(userId);

    NotificationPreference pref =
        prefs.stream().filter(p -> p.getChannel() == channel).findFirst().orElse(null);

    if (pref != null && !pref.isEnabled()) {
      return;
    }

    if (pref != null && pref.getQuietHoursStart() != null && pref.getQuietHoursEnd() != null) {
      if (DateUtils.isAfterHours(pref.getQuietHoursStart(), pref.getQuietHoursEnd())) {
        return;
      }
    }

    String endpoint =
        switch (channel) {
          case PUSH -> PUSH_ENDPOINT;
          case EMAIL -> EMAIL_ENDPOINT;
          case SMS -> SMS_ENDPOINT;
        };

    String payload =
        String.format("{\"userId\":\"%s\",\"message\":\"%s\"}", userId, escapeJson(message));
    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest req =
          HttpRequest.newBuilder()
              .uri(URI.create(endpoint))
              .POST(HttpRequest.BodyPublishers.ofString(payload))
              .header("Content-Type", "application/json")
              .build();
      client.send(req, HttpResponse.BodyHandlers.discarding());
    } catch (Exception e) {
      // Probably should do something more sophisticated here....
      System.err.println("Error sending notification: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static String escapeJson(String s) {
    if (s == null) {
      return "";
    }
    return s.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");
  }
}
