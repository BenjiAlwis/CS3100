package sceneitall.models;

import org.jspecify.annotations.Nullable;

/**
 * Represents the health status of a device: whether it is healthy, a status message (e.g., "OK",
 * "Connection lost"), and when it was last seen.
 */
public class DeviceHealth {
  @Nullable private String deviceId;
  private boolean healthy;
  @Nullable private String statusMessage; // e.g., "OK", "Firmware outdated", "Connection lost"
  private long lastSeenTimestamp;

  /** Creates an empty device health record. */
  public DeviceHealth() {}

  /**
   * Creates a device health record.
   *
   * @param deviceId the device identifier
   * @param healthy whether the device is healthy
   * @param statusMessage the status message
   * @param lastSeenTimestamp when the device was last seen
   */
  public DeviceHealth(
      String deviceId, boolean healthy, String statusMessage, long lastSeenTimestamp) {
    this.deviceId = deviceId;
    this.healthy = healthy;
    this.statusMessage = statusMessage;
    this.lastSeenTimestamp = lastSeenTimestamp;
  }

  /** Returns the device identifier. */
  @Nullable
  public String getDeviceId() {
    return deviceId;
  }

  /** Sets the device identifier. */
  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  /** Returns true if the device is healthy. */
  public boolean isHealthy() {
    return healthy;
  }

  /** Sets whether the device is healthy. */
  public void setHealthy(boolean healthy) {
    this.healthy = healthy;
  }

  /** Returns the status message. */
  @Nullable
  public String getStatusMessage() {
    return statusMessage;
  }

  /** Sets the status message. */
  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  /** Returns the timestamp when the device was last seen. */
  public long getLastSeenTimestamp() {
    return lastSeenTimestamp;
  }

  /** Sets the last-seen timestamp. */
  public void setLastSeenTimestamp(long lastSeenTimestamp) {
    this.lastSeenTimestamp = lastSeenTimestamp;
  }
}
