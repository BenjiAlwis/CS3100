package sceneitall.models;

import org.jspecify.annotations.Nullable;

/**
 * Represents an automation rule, e.g., "if motion detected after 10pm, turn on porch light and send
 * alert".
 */
public class AutomationRule {
  @Nullable private String id;
  @Nullable private String homeId;
  @Nullable private String triggerDeviceId;
  @Nullable private RuleCondition condition;
  @Nullable private String alertMessage;
  @Nullable private String actionDeviceId; // optional: device to control when rule fires

  /** Creates an empty automation rule. */
  public AutomationRule() {}

  /**
   * Creates an automation rule with the given fields.
   *
   * @param id the rule identifier
   * @param homeId the home this rule belongs to
   * @param triggerDeviceId the device that triggers the rule
   * @param condition the condition type to check
   * @param alertMessage the message to send when the rule fires
   */
  public AutomationRule(
      String id,
      String homeId,
      String triggerDeviceId,
      RuleCondition condition,
      String alertMessage) {
    this.id = id;
    this.homeId = homeId;
    this.triggerDeviceId = triggerDeviceId;
    this.condition = condition;
    this.alertMessage = alertMessage;
  }

  /**
   * Returns true if the given device state satisfies this rule's condition.
   *
   * @param state the current device state
   * @return true if the condition is met
   */
  public boolean conditionMet(DeviceState state) {
    RuleCondition cond = this.getCondition();
    return state != null && cond != null && state.matches(cond);
  }

  /** Returns the rule identifier. */
  @Nullable
  public String getId() {
    return id;
  }

  /** Sets the rule identifier. */
  public void setId(String id) {
    this.id = id;
  }

  /** Returns the home this rule belongs to. */
  @Nullable
  public String getHomeId() {
    return homeId;
  }

  /** Sets the home ID. */
  public void setHomeId(String homeId) {
    this.homeId = homeId;
  }

  /** Returns the ID of the device that triggers this rule. */
  @Nullable
  public String getTriggerDeviceId() {
    return triggerDeviceId;
  }

  /** Sets the trigger device ID. */
  public void setTriggerDeviceId(String triggerDeviceId) {
    this.triggerDeviceId = triggerDeviceId;
  }

  /** Returns the condition type for this rule. */
  @Nullable
  public RuleCondition getCondition() {
    return condition;
  }

  /** Sets the condition type. */
  public void setCondition(RuleCondition condition) {
    this.condition = condition;
  }

  /** Returns the message to send when the rule fires. */
  @Nullable
  public String getAlertMessage() {
    return alertMessage;
  }

  /** Sets the alert message. */
  public void setAlertMessage(String alertMessage) {
    this.alertMessage = alertMessage;
  }

  /** Returns the optional ID of the device to control when the rule fires. */
  @Nullable
  public String getActionDeviceId() {
    return actionDeviceId;
  }

  /** Sets the action device ID. */
  public void setActionDeviceId(String actionDeviceId) {
    this.actionDeviceId = actionDeviceId;
  }
}
