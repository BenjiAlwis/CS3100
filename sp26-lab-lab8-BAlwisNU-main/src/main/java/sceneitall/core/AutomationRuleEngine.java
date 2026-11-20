package sceneitall.core;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import sceneitall.DatabaseConnection;
import sceneitall.hardware.ZigbeeGateway;
import sceneitall.models.AutomationRule;
import sceneitall.models.DeviceState;
import sceneitall.models.NotificationPreference;
import sceneitall.models.User;
import sceneitall.util.DateUtils;

/**
 * Evaluates automation rules for a home. Loads rules, checks device states against rule conditions,
 * and triggers alerts when conditions are met.
 */
public class AutomationRuleEngine {

  private static final String PUSH_ENDPOINT = "http://push-service/send";
  private static final String EMAIL_ENDPOINT = "http://email-service/send";
  private static final String SMS_ENDPOINT = "http://sms-service/send";

  /**
   * Evaluates all automation rules for the given home. For each rule, reads the trigger device's
   * state and, if the condition is met, sends the alert message to all users in the home via their
   * preferred notification channels.
   *
   * @param homeId the home whose rules to evaluate
   */
  public void evaluate(String homeId) {
    List<AutomationRule> rules = DatabaseConnection.getInstance().loadRules(homeId);

    ZigbeeGateway gateway = ZigbeeGateway.getGlobalInstance();

    for (AutomationRule rule : rules) {
      String triggerDeviceId = rule.getTriggerDeviceId();
      String ruleHomeId = rule.getHomeId();
      if (triggerDeviceId == null || ruleHomeId == null) {
        continue;
      }
      DeviceState state = gateway.readState(triggerDeviceId);
      if (rule.conditionMet(state)) {
        List<User> users = DatabaseConnection.getInstance().loadUsersForHome(ruleHomeId);
        for (User user : users) {
          String userId = user.getId();
          if (userId == null) {
            continue;
          }
          List<NotificationPreference> prefs =
              DatabaseConnection.getInstance().loadNotificationPreferences(userId);
          for (NotificationPreference pref : prefs) {
            if (pref.isEnabled()) {
              if (pref.getQuietHoursStart() != null && pref.getQuietHoursEnd() != null) {
                if (DateUtils.isAfterHours(pref.getQuietHoursStart(), pref.getQuietHoursEnd())) {
                  continue;
                }
              }
              String endpoint =
                  switch (pref.getChannel()) {
                    case PUSH -> PUSH_ENDPOINT;
                    case EMAIL -> EMAIL_ENDPOINT;
                    case SMS -> SMS_ENDPOINT;
                  };
              String message = rule.getAlertMessage();
              String escaped =
                  message == null
                      ? ""
                      : message
                          .replace("\\", "\\\\")
                          .replace("\"", "\\\"")
                          .replace("\n", "\\n")
                          .replace("\r", "\\r")
                          .replace("\t", "\\t");
              String payload =
                  String.format("{\"userId\":\"%s\",\"message\":\"%s\"}", userId, escaped);
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
                System.err.println("Error sending notification: " + e.getMessage());
                e.printStackTrace();
              }
            }
          }
          if (prefs.isEmpty()) {
            String endpoint = PUSH_ENDPOINT;
            String message = rule.getAlertMessage();
            String escaped =
                message == null
                    ? ""
                    : message
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t");
            String payload =
                String.format("{\"userId\":\"%s\",\"message\":\"%s\"}", userId, escaped);
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
              System.err.println("Error sending notification: " + e.getMessage());
              e.printStackTrace();
            }
          }
        }
      }
    }
  }
}
