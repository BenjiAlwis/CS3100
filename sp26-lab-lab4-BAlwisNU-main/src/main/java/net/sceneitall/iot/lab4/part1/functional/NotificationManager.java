package net.sceneitall.iot.lab4.part1.functional;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import net.sceneitall.iot.lab4.domain.IoTDevice;
import net.sceneitall.iot.lab4.domain.User;

/**
 * Functional implementation using lambdas and functional interfaces.
 *
 * <p>This class demonstrates how functional programming can replace the Strategy pattern: -
 * Strategies become functions (Predicate and BiConsumer) - No need for separate strategy classes -
 * Easy to compose and combine behaviors inline - More concise for simple strategies
 *
 * <p><strong>Example usage:</strong>
 *
 * <pre>{@code
 * // Immediate notifications using lambda
 * manager.setStrategy(
 *     ctx -> true,  // shouldNotify: always
 *     (user, msg) -> pushService.send(user, msg)  // sendNotification: push
 * );
 *
 * // Or with method references for cleaner code
 * manager.setStrategy(
 *     ctx -> true,
 *     pushService::send
 * );
 *
 * // Smart notifications with inline logic
 * manager.setStrategy(
 *     ctx -> !ctx.getUser().isInQuietHours() || ctx.getDevice().isCritical(),
 *     (user, msg) -> {
 *         if (user.isInQuietHours()) {
 *             digestService.addToQueue(user, msg);
 *         } else {
 *             pushService.send(user, msg);
 *         }
 *     }
 * );
 * }</pre>
 */
public class NotificationManager {
  // Strategy is now just a pair of functions
  private Predicate<NotificationContext> shouldNotify;
  private BiConsumer<User, String> sendNotification;

  /**
   * Sets the notification strategy using functional interfaces.
   *
   * <p>This demonstrates how lambdas can replace entire strategy classes. Instead of creating a new
   * class that implements NotificationStrategy, we pass two functions:
   *
   * <ol>
   *   <li>A predicate that tests whether to notify
   *   <li>A consumer that performs the notification
   * </ol>
   *
   * @param shouldNotify predicate that tests if notification should be sent
   * @param sendNotification consumer that sends the notification
   */
  public void setStrategy(
      Predicate<NotificationContext> shouldNotify, BiConsumer<User, String> sendNotification) {
    this.shouldNotify = shouldNotify;
    this.sendNotification = sendNotification;
  }

  /**
   * Notifies the user about a device event using the functional strategy.
   *
   * <p>This method is simpler than the class-based version because the strategy is just two
   * functions. However, the trade-off is that the behavior is less discoverable—you can't see a
   * list of "strategy classes" in the IDE.
   *
   * @param device the device that triggered the notification
   * @param message the notification message
   * @throws IllegalStateException if no strategy has been set
   */
  public void notifyUser(IoTDevice device, String message) {
    if (shouldNotify == null || sendNotification == null) {
      throw new IllegalStateException("No notification strategy set");
    }

    NotificationContext ctx = new NotificationContext(device, device.getOwner());
    if (shouldNotify.test(ctx)) {
      sendNotification.accept(device.getOwner(), message);
    }
  }
}
