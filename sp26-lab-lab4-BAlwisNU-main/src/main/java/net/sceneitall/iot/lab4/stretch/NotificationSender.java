package net.sceneitall.iot.lab4.stretch;

import net.sceneitall.iot.lab4.domain.NotificationMessage;
import net.sceneitall.iot.lab4.domain.User;

/**
 * A custom functional interface for sending notifications.
 *
 * <p>This interface demonstrates creating type-safe functional interfaces instead of using generic
 * ones like {@link java.util.function.BiConsumer}. Benefits of custom functional interfaces: -
 * Better type safety (NotificationMessage instead of String) - Self-documenting (clear method name
 * and parameters) - Can add default methods for composition
 *
 * <p><strong>Example usage:</strong>
 *
 * <pre>{@code
 * // Lambda implementation
 * NotificationSender sender = (user, message) ->
 *     pushService.send(user, message.getContent());
 *
 * // Method reference
 * NotificationSender sender = pushService::send;
 *
 * // Chaining with andThen()
 * NotificationSender combined = sender1.andThen(sender2);
 * combined.send(user, message); // Sends via both sender1 and sender2
 * }</pre>
 *
 * <p><strong>Stretch Goal:</strong> Implement the {@link #andThen(NotificationSender)} default
 * method for composing senders.
 */
@FunctionalInterface
public interface NotificationSender {
  /**
   * Sends a notification to the specified user.
   *
   * @param recipient the user to notify
   * @param message the notification message to send
   */
  void send(User recipient, NotificationMessage message);

  /**
   * Returns a composed NotificationSender that first sends via this sender, then via the after
   * sender.
   *
   * <p>This enables composing multiple notification channels, for example: - Send via push AND log
   * to audit trail - Send via email AND SMS - Send via primary channel AND backup channel
   *
   * <p>TODO: Implement this method to: 1. Send via this sender 2. Then send via the after sender 3.
   * Return a new NotificationSender that does both
   *
   * <p><strong>Hint:</strong> Return a lambda that calls both senders:
   *
   * <pre>{@code
   * return (user, message) -> {
   *     this.send(user, message);
   *     after.send(user, message);
   * };
   * }</pre>
   *
   * @param after the sender to execute after this one
   * @return a composed NotificationSender
   * @throws NullPointerException if after is null
   */
  default NotificationSender andThen(NotificationSender after) {
    // TODO: Implement the andThen composition
    // Return a new NotificationSender that:
    // 1. Calls this.send(user, message)
    // 2. Then calls after.send(user, message)

    throw new UnsupportedOperationException("TODO: Implement andThen()");
  }
}
