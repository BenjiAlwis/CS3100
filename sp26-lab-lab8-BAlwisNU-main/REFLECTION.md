# Lab 8 Reflection: Hexagonal Architecture

**Partner's Name: Aryaman Sarcar

For part 4, other partner’s names : Hongrui Xiong and Yu-Tse Weng


1. **Domain vs Infrastructure:** In `AutomationRuleEngine.evaluate()`, which lines are domain logic
   and which are infrastructure? What specifically hurts testability (controllability or
   observability)?

   _For each feature area (Device Management and Automation Rules), complete: "To test [feature]'s core business logic in isolation, I would need to stub out _____ ."_


public void evaluate(String homeId) {

  // [INFRASTRUCTURE] Direct singleton call to persistence layer.
  // [TESTABILITY ISSUE - CONTROLLABILITY] Cannot inject fake repository.
  List<AutomationRule> rules =
      DatabaseConnection.getInstance().loadRules(homeId);

  // [INFRASTRUCTURE] Direct singleton call to hardware SDK.
  // [TESTABILITY ISSUE - CONTROLLABILITY] Cannot inject fake device state reader.
  ZigbeeGateway gateway = ZigbeeGateway.getGlobalInstance();

  // [DOMAIN FLOW] Evaluate each rule belonging to the home.
  for (AutomationRule rule : rules) {

    String triggerDeviceId = rule.getTriggerDeviceId();
    String ruleHomeId = rule.getHomeId();

    // [DOMAIN GUARD] Skip malformed rules.
    // This is business validation logic.
    if (triggerDeviceId == null || ruleHomeId == null) {
      continue;
    }

    // [INFRASTRUCTURE] Reads device state from hardware layer.
    // [TESTABILITY ISSUE - CONTROLLABILITY] Depends on real Zigbee hardware.
    DeviceState state = gateway.readState(triggerDeviceId);

    // [DOMAIN LOGIC] Core business decision:
    // Should this rule fire given the current device state?
    // conditionMet() is defined on AutomationRule and uses DeviceState.
    // This is pure domain logic (no IO).
    if (rule.conditionMet(state)) {

      // [INFRASTRUCTURE] Load users for this home from persistence.
      // [TESTABILITY ISSUE - CONTROLLABILITY]
      List<User> users =
          DatabaseConnection.getInstance().loadUsersForHome(ruleHomeId);

      // [DOMAIN FLOW] Notify each user associated with the home.
      for (User user : users) {

        String userId = user.getId();
        if (userId == null) {
          continue;  // [DOMAIN GUARD]
        }

        // [INFRASTRUCTURE] Load notification preferences.
        // [TESTABILITY ISSUE - CONTROLLABILITY]
        List<NotificationPreference> prefs =
            DatabaseConnection.getInstance()
                .loadNotificationPreferences(userId);

        for (NotificationPreference pref : prefs) {

          // [DOMAIN POLICY] Only send if channel enabled.
          if (pref.isEnabled()) {

            // [DOMAIN POLICY + INFRASTRUCTURE CLOCK]
            // Quiet hours is business policy,
            // but implemented using system time via DateUtils.
            // [TESTABILITY ISSUE - CONTROLLABILITY]
            if (pref.getQuietHoursStart() != null
                && pref.getQuietHoursEnd() != null) {

              if (DateUtils.isAfterHours(
                      pref.getQuietHoursStart(),
                      pref.getQuietHoursEnd())) {
                continue;
              }
            }

            // [DOMAIN DECISION] Select notification channel.
            String endpoint =
                switch (pref.getChannel()) {
                  case PUSH -> PUSH_ENDPOINT;
                  case EMAIL -> EMAIL_ENDPOINT;
                  case SMS -> SMS_ENDPOINT;
                };

            String message = rule.getAlertMessage();

            // [INFRASTRUCTURE DETAIL] Manual JSON escaping.
            // Not domain logic — purely transport formatting.
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
                String.format(
                    "{\"userId\":\"%s\",\"message\":\"%s\"}",
                    userId,
                    escaped);

            try {

              // [INFRASTRUCTURE] Direct HTTP client creation.
              // [TESTABILITY ISSUE - CONTROLLABILITY]
              // Cannot stub notification sender.
              HttpClient client = HttpClient.newHttpClient();

              HttpRequest req =
                  HttpRequest.newBuilder()
                      .uri(URI.create(endpoint))
                      .POST(HttpRequest.BodyPublishers.ofString(payload))
                      .header("Content-Type", "application/json")
                      .build();

              // [INFRASTRUCTURE] Network IO side-effect.
              // [TESTABILITY ISSUE - OBSERVABILITY]
              // No return value; no easy way to assert notification occurred.
              client.send(req, HttpResponse.BodyHandlers.discarding());

            } catch (Exception e) {

              // [INFRASTRUCTURE ERROR HANDLING]
              // Only logs to stderr.
              // [TESTABILITY ISSUE - OBSERVABILITY]
              System.err.println(
                  "Error sending notification: " + e.getMessage());
              e.printStackTrace();
            }
          }
        }

        // [DOMAIN POLICY] If user has no preferences,
        // default to PUSH notification.
        if (prefs.isEmpty()) {

          // [INFRASTRUCTURE] Same HTTP sending logic as above.
          // [TESTABILITY ISSUE - CONTROLLABILITY + OBSERVABILITY]
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
              String.format(
                  "{\"userId\":\"%s\",\"message\":\"%s\"}",
                  userId,
                  escaped);

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
            System.err.println(
                "Error sending notification: " + e.getMessage());
            e.printStackTrace();
          }
        }
      }
    }
  }
}

To test Device Management’s core business logic in isolation, I would need to stub out data storage for devices and health.


To test Automation Rules’ core business logic in isolation, I would need to stub out rule storage, user/prefs storage, device state reads, notification sending, and the clock.



2. **Repository Interface Design:** Your final `DeviceRepository` and `RuleRepository` interfaces,
   with reasoning: _"I defined [interface] as [description] because [reason]."_



public interface DeviceRepository {
  void saveDevice(IoTDevice device);

  List<IoTDevice> loadDevices(String homeId);

  @Nullable
  DeviceHealth loadDeviceHealth(String deviceId);
}

I defined DeviceRepository as a narrow persistence interface that exposes only saveDevice, loadDevices, and loadDeviceHealth because those are the only storage operations that DeviceManager actually needs. I deliberately avoided including unrelated database methods (such as rule or user operations) to prevent recreating the DatabaseConnection “god object.” By keeping the interface small and expressed entirely in domain types (IoTDevice, DeviceHealth), I ensured that the dependency flows through an abstraction and can easily be replaced with an in-memory implementation during testing.


public interface RuleRepository {
  List<AutomationRule> loadRules(String homeId);
}

I defined RuleRepository as a focused interface with a single loadRules(homeId) method because the Automation Rules domain only needs to retrieve rules for a given home in order to evaluate them. I intentionally avoided exposing storage-specific concerns (such as database queries, JDBC types, or singleton access) so that the interface reflects what the domain needs rather than how the data is stored. This keeps the port aligned with hexagonal architecture principles and makes it straightforward to stub in unit tests.




3. **Refactored DeviceManager:** Your refactored class using constructor injection. Confirm: no
   `getInstance()` calls remain, and you can now instantiate it with `InMemoryDeviceRepository` for
   testing.


public class DeviceManager {

  private final DeviceRepository deviceRepository;

  public DeviceManager(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
  }

  /**
   * Adds a device to the specified home and persists it.
   *
   * @param homeId the home to add the device to
   * @param device the device to add
   */
  public void addDevice(String homeId, IoTDevice device) {
    device.setHomeId(homeId);
    deviceRepository.saveDevice(device);
  }

  /**
   * Returns all devices in the given home.
   *
   * @param homeId the home identifier
   * @return list of devices in the home
   */
  public List<IoTDevice> getDevices(String homeId) {
    return deviceRepository.loadDevices(homeId);
  }

  /**
   * Checks the health status of a device.
   *
   * @param deviceId the device identifier
   * @return the device health status
   */
  public DeviceHealth checkHealth(String deviceId) {
    DeviceHealth health = deviceRepository.loadDeviceHealth(deviceId);

    // Preserve existing behavior: default OK if missing
    if (health == null) {
      return new DeviceHealth(deviceId, true, "OK", System.currentTimeMillis());
    }
    return health;
  }
}

I refactored DeviceManager to depend on the DeviceRepository interface via constructor injection so its business logic can be tested without a database singleton. I considered keeping DatabaseConnection.getInstance() inside DeviceManager, but constructor injection is better because it improves controllability (tests can pass an in-memory repo) and prevents hidden global state from affecting unit tests.

4. **Port Interfaces:** Your final port interfaces for device state and notification. Did Copilot's
   first attempt use infrastructure types? What did you fix?

Copilot initially suggested ports shaped like the infrastructure (ZigbeeFrame, HttpClient). I rewrote them to use only domain types (DeviceState, NotificationChannel) so they can be stubbed in unit tests and don’t leak transport/hardware details.


5. **Peer Review Finding:** One thing you noticed reviewing another pair's work — an infrastructure
   leak, a clean port design, or a remaining singleton. What would you change or keep?

They had a clean device-state port: DeviceState readState(String deviceId) returning a domain DeviceState rather than Zigbee SDK types. I would keep this design because it’s narrow, domain-oriented, and can be stubbed with a simple fake in tests. It’s a strong example of a hexagonal port that doesn’t leak infrastructure

6. **Extending the Architecture:** What ports would Analytics & Reporting need? Would it share any
   with Device Management or Automation Rules?

Analytics needs ports for historical event queries, report delivery, and time; it may share user/home membership and notification delivery ports, but it should not reuse DeviceRepository or RuleRepository because analytics data access is a different domain concern and combining them would recreate the DatabaseConnection-style coupling.

7. **Meta (pick one):**
   - _Connections:_ How does one of your design decisions connect to a concept from L7-L17?
   - _Communication:_ Did explaining a decision out loud clarify your thinking?
   - _AI tools:_ How did manual-first (Part 2) compare to Copilot-first (Part 3)?
   Yes. Explaining my decision forced me to move beyond “this feels cleaner” and clearly articulate why dependency injection improves controllability and removes hidden singleton coupling. Putting the reasoning into words helped me notice where infrastructure was still leaking into the domain. Using the structured format made me connect the design directly to DIP and testability rather than relying on intuition.
