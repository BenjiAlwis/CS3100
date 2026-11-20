package sceneitall;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jspecify.annotations.Nullable;

import sceneitall.models.AutomationRule;
import sceneitall.models.DeviceHealth;
import sceneitall.models.Home;
import sceneitall.models.IoTDevice;
import sceneitall.models.NotificationPreference;
import sceneitall.models.User;

/**
 * Central data access for SceneItAll. Provides persistence for automation rules, devices, device
 * health, users, homes, and notification preferences.
 *
 * <p>In production this would connect to a database via JDBC. This implementation uses in-memory
 * storage.
 */
public final class DatabaseConnection {
  @Nullable private static DatabaseConnection instance;

  // In-memory storage for lab purposes (simulates DB tables)
  private final Map<String, List<AutomationRule>> rulesByHome = new ConcurrentHashMap<>();
  private final Map<String, List<IoTDevice>> devicesByHome = new ConcurrentHashMap<>();
  private final Map<String, DeviceHealth> deviceHealth = new ConcurrentHashMap<>();
  private final Map<String, User> users = new ConcurrentHashMap<>();
  private final Map<String, Home> homes = new ConcurrentHashMap<>();
  private final Map<String, List<String>> homeMemberIds = new ConcurrentHashMap<>();
  private final Map<String, List<NotificationPreference>> notificationPrefsByUser =
      new ConcurrentHashMap<>();

  /** Returns the shared database connection instance. */
  public static DatabaseConnection getInstance() {
    if (instance == null) {
      instance = new DatabaseConnection();
    }
    return instance;
  }

  private DatabaseConnection() {}

  /**
   * Loads all automation rules for the given home.
   *
   * @param homeId the home identifier
   * @return list of automation rules for the home
   */
  public List<AutomationRule> loadRules(String homeId) {
    return new ArrayList<>(rulesByHome.getOrDefault(homeId, List.of()));
  }

  /**
   * Saves an automation rule.
   *
   * @param rule the rule to save
   */
  public void saveRule(AutomationRule rule) {
    rulesByHome.computeIfAbsent(rule.getHomeId(), k -> new ArrayList<>()).add(rule);
  }

  /**
   * Loads all IoT devices for the given home.
   *
   * @param homeId the home identifier
   * @return list of devices in the home
   */
  public List<IoTDevice> loadDevices(String homeId) {
    return new ArrayList<>(devicesByHome.getOrDefault(homeId, List.of()));
  }

  /**
   * Saves an IoT device.
   *
   * @param device the device to save
   */
  public void saveDevice(IoTDevice device) {
    devicesByHome.computeIfAbsent(device.getHomeId(), k -> new ArrayList<>()).add(device);
  }

  /**
   * Checks the health status of a device.
   *
   * @param deviceId the device identifier
   * @return the device health status
   */
  public DeviceHealth checkDeviceHealth(String deviceId) {
    return deviceHealth.getOrDefault(
        deviceId, new DeviceHealth(deviceId, true, "OK", System.currentTimeMillis()));
  }

  /**
   * Saves device health status.
   *
   * @param health the health record to save
   */
  public void saveDeviceHealth(DeviceHealth health) {
    deviceHealth.put(health.getDeviceId(), health);
  }

  /**
   * Loads a user by ID.
   *
   * @param userId the user identifier
   * @return the user, or null if not found
   */
  @Nullable
  public User loadUser(String userId) {
    return users.get(userId);
  }

  /**
   * Saves a user.
   *
   * @param user the user to save
   */
  public void saveUser(User user) {
    users.put(user.getId(), user);
  }

  /**
   * Loads a home by ID.
   *
   * @param homeId the home identifier
   * @return the home, or null if not found
   */
  @Nullable
  public Home loadHome(String homeId) {
    return homes.get(homeId);
  }

  /**
   * Saves a home.
   *
   * @param home the home to save
   */
  public void saveHome(Home home) {
    homes.put(home.getId(), home);
    if (home.getOwnerUserId() != null) {
      List<String> members = homeMemberIds.computeIfAbsent(home.getId(), k -> new ArrayList<>());
      if (!members.contains(home.getOwnerUserId())) {
        members.add(home.getOwnerUserId());
      }
    }
  }

  /**
   * Loads all users associated with a home (owner and members).
   *
   * @param homeId the home identifier
   * @return list of users who have access to the home
   */
  public List<User> loadUsersForHome(String homeId) {
    List<String> memberIds = new ArrayList<>(homeMemberIds.getOrDefault(homeId, List.of()));
    Home home = homes.get(homeId);
    if (home != null
        && home.getOwnerUserId() != null
        && !memberIds.contains(home.getOwnerUserId())) {
      memberIds.add(home.getOwnerUserId());
    }
    return memberIds.stream().map(users::get).filter(u -> u != null).toList();
  }

  /**
   * Loads notification preferences for a user.
   *
   * @param userId the user identifier
   * @return list of notification preferences
   */
  public List<NotificationPreference> loadNotificationPreferences(String userId) {
    return new ArrayList<>(notificationPrefsByUser.getOrDefault(userId, List.of()));
  }

  /**
   * Saves a notification preference.
   *
   * @param pref the preference to save
   */
  public void saveNotificationPreference(NotificationPreference pref) {
    notificationPrefsByUser.computeIfAbsent(pref.getUserId(), k -> new ArrayList<>()).add(pref);
  }
}
