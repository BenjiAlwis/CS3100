package sceneitall.util;

import sceneitall.models.AutomationRule;

/** Utility methods for validating domain objects. */
public final class ValidationUtils {
  private ValidationUtils() {}

  /**
   * Returns true if the automation rule is valid. A valid rule has a home ID, trigger device ID,
   * and a condition.
   *
   * @param rule the rule to validate
   * @return true if valid
   */
  public static boolean isValidRule(AutomationRule rule) {
    if (rule == null) {
      return false;
    }
    if (rule.getHomeId() == null || rule.getHomeId().isBlank()) {
      return false;
    }
    if (rule.getTriggerDeviceId() == null || rule.getTriggerDeviceId().isBlank()) {
      return false;
    }
    return rule.getCondition() != null;
  }
}
