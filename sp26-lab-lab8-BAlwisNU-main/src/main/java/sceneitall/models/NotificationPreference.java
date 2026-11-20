package sceneitall.models;

import org.jspecify.annotations.Nullable;

/**
 * Represents a user's notification preference for a channel, including whether it is enabled and
 * quiet hours.
 */
public class NotificationPreference {
  @Nullable private String userId;
  @Nullable private NotificationChannel channel;
  private boolean enabled;
  @Nullable private String quietHoursStart; // e.g., "22:00"
  @Nullable private String quietHoursEnd; // e.g., "07:00"

  /** Creates an empty notification preference. */
  public NotificationPreference() {}

  /** Returns the user identifier. */
  @Nullable
  public String getUserId() {
    return userId;
  }

  /** Sets the user identifier. */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /** Returns the notification channel. */
  @Nullable
  public NotificationChannel getChannel() {
    return channel;
  }

  /** Sets the notification channel. */
  public void setChannel(NotificationChannel channel) {
    this.channel = channel;
  }

  /** Returns true if this channel is enabled for notifications. */
  public boolean isEnabled() {
    return enabled;
  }

  /** Sets whether this channel is enabled. */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /** Returns the quiet hours start time (e.g., "22:00"). */
  @Nullable
  public String getQuietHoursStart() {
    return quietHoursStart;
  }

  /** Sets the quiet hours start time. */
  public void setQuietHoursStart(String quietHoursStart) {
    this.quietHoursStart = quietHoursStart;
  }

  /** Returns the quiet hours end time (e.g., "07:00"). */
  @Nullable
  public String getQuietHoursEnd() {
    return quietHoursEnd;
  }

  /** Sets the quiet hours end time. */
  public void setQuietHoursEnd(String quietHoursEnd) {
    this.quietHoursEnd = quietHoursEnd;
  }
}
