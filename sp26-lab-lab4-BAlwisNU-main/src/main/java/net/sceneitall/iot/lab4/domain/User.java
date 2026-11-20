package net.sceneitall.iot.lab4.domain;

import java.time.LocalTime;
import java.time.ZoneId;

/** Represents a user of the SceneItAll IoT system. */
public class User {
  private final String id;
  private final String name;
  private final String email;
  private final boolean quietHoursEnabled;
  private final LocalTime quietHoursStart;
  private final LocalTime quietHoursEnd;

  /**
   * Constructs a new User with the specified properties.
   *
   * @param id the unique identifier for the user
   * @param name the user's name
   * @param email the user's email address
   * @param quietHoursEnabled whether quiet hours are enabled for this user
   * @param quietHoursStart the start time for quiet hours (e.g., 22:00)
   * @param quietHoursEnd the end time for quiet hours (e.g., 08:00)
   */
  public User(
      String id,
      String name,
      String email,
      boolean quietHoursEnabled,
      LocalTime quietHoursStart,
      LocalTime quietHoursEnd) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.quietHoursEnabled = quietHoursEnabled;
    this.quietHoursStart = quietHoursStart;
    this.quietHoursEnd = quietHoursEnd;
  }

  /** Simplified constructor for users without quiet hours. */
  public User(String id, String name, String email) {
    this(id, name, email, false, LocalTime.of(22, 0), LocalTime.of(8, 0));
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public boolean isQuietHoursEnabled() {
    return quietHoursEnabled;
  }

  /**
   * Checks if the current time falls within the user's quiet hours.
   *
   * @return true if quiet hours are enabled and the current time is within quiet hours
   */
  public boolean isInQuietHours() {
    if (!quietHoursEnabled) {
      return false;
    }

    LocalTime now = LocalTime.now(ZoneId.of("America/Los_Angeles"));

    // Handle quiet hours that span midnight (e.g., 22:00 to 08:00)
    if (quietHoursStart.isAfter(quietHoursEnd)) {
      return now.isAfter(quietHoursStart) || now.isBefore(quietHoursEnd);
    } else {
      return now.isAfter(quietHoursStart) && now.isBefore(quietHoursEnd);
    }
  }
}
