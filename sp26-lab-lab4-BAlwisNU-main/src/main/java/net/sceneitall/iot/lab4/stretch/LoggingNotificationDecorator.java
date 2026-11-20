package net.sceneitall.iot.lab4.stretch;

import org.jspecify.annotations.Nullable;

import net.sceneitall.iot.lab4.domain.IoTDevice;
import net.sceneitall.iot.lab4.domain.User;
import net.sceneitall.iot.lab4.part1.strategy.NotificationStrategy;

/**
 * A decorator that logs all notifications before delegating to the wrapped strategy.
 *
 * <p>This class demonstrates the Decorator pattern (Lecture 8): - Wraps another
 * NotificationStrategy - Adds logging behavior without modifying the original strategy - Can be
 * stacked with other decorators for multiple cross-cutting concerns
 *
 * <p><strong>Example usage:</strong>
 *
 * <pre>{@code
 * // Create a base strategy
 * NotificationStrategy base = new ImmediateNotificationStrategy(pushService);
 *
 * // Wrap it with logging
 * NotificationStrategy logged = new LoggingNotificationDecorator(base, logger);
 *
 * // Use the decorated strategy
 * manager.setStrategy(logged);
 * manager.notifyUser(device, "Battery low");
 * // Output: [LOG] Notifying user1: Battery low
 * // Then: actual notification is sent via base strategy
 * }</pre>
 *
 * <p><strong>Stretch Goal:</strong> Implement this decorator to add logging to any notification
 * strategy.
 */
public class LoggingNotificationDecorator implements NotificationStrategy {
  private final @Nullable NotificationStrategy wrapped;
  private final @Nullable Logger logger;

  /**
   * Creates a logging decorator that wraps another strategy.
   *
   * @param wrapped the strategy to wrap and delegate to
   * @param logger the logger to use for logging notifications
   */
  public LoggingNotificationDecorator(NotificationStrategy wrapped, Logger logger) {
    this.wrapped = wrapped;
    this.logger = logger;
  }

  /**
   * Logs the notification, then delegates to the wrapped strategy.
   *
   * <p>TODO: Implement this method to: 1. Log the notification (use logger.log()) 2. Delegate to
   * wrapped.notify()
   *
   * @param user the user to notify
   * @param message the notification message
   */
  @Override
  public void notify(User user, String message) {
    // TODO: Log the notification
    // Example: logger.log("Notifying " + user.getId() + ": " + message);

    // TODO: Delegate to the wrapped strategy
    // wrapped.notify(user, message);

    throw new UnsupportedOperationException("TODO: Implement notify()");
  }

  /**
   * Delegates the shouldNotify decision to the wrapped strategy.
   *
   * <p>The decorator doesn't change the decision logic, only adds logging.
   *
   * <p>TODO: Implement this method to delegate to wrapped.shouldNotify()
   *
   * @param device the device that triggered the notification
   * @param user the user who owns the device
   * @return true if the wrapped strategy says we should notify
   */
  @Override
  public boolean shouldNotify(IoTDevice device, User user) {
    // TODO: Delegate to the wrapped strategy
    // return wrapped.shouldNotify(device, user);

    throw new UnsupportedOperationException("TODO: Implement shouldNotify()");
  }

  /**
   * Simple logger interface for this exercise.
   *
   * <p>In a real system, you'd use a logging framework like SLF4J or java.util.logging.
   */
  public interface Logger {
    /**
     * Logs a message.
     *
     * @param message the message to log
     */
    void log(String message);
  }
}
