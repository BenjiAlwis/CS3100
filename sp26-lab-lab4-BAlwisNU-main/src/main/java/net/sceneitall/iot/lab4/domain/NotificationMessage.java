package net.sceneitall.iot.lab4.domain;

import java.time.Instant;

/** Represents a notification message to be sent to a user. */
public class NotificationMessage {
  private final String content;
  private final IoTDevice source;
  private final Instant timestamp;
  private final String priority;

  /**
   * Constructs a new notification message.
   *
   * @param content the message content
   * @param source the device that triggered the notification
   * @param priority the priority level (e.g., "LOW", "MEDIUM", "HIGH")
   */
  public NotificationMessage(String content, IoTDevice source, String priority) {
    this.content = content;
    this.source = source;
    this.timestamp = Instant.now();
    this.priority = priority;
  }

  public String getContent() {
    return content;
  }

  public IoTDevice getSource() {
    return source;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public String getPriority() {
    return priority;
  }
}
