package sceneitall.models;

/**
 * Represents the current state of an IoT device. Used when evaluating automation rules to determine
 * if conditions like motion detected or temperature above threshold are met.
 */
public class DeviceState {
  private final String deviceId;
  private final RuleCondition stateType;
  private final boolean triggered;

  /**
   * Creates a device state.
   *
   * @param deviceId the device identifier
   * @param stateType the type of condition
   * @param triggered whether the condition is currently triggered
   */
  public DeviceState(String deviceId, RuleCondition stateType, boolean triggered) {
    this.deviceId = deviceId;
    this.stateType = stateType;
    this.triggered = triggered;
  }

  /** Returns the device identifier. */
  public String getDeviceId() {
    return deviceId;
  }

  /** Returns the type of condition (e.g., motion, temperature). */
  public RuleCondition getStateType() {
    return stateType;
  }

  /** Returns true if the condition is currently triggered (e.g., motion detected). */
  public boolean isTriggered() {
    return triggered;
  }

  /**
   * Returns true if this state matches the given rule condition and is triggered.
   *
   * @param condition the rule condition to check
   * @return true if the state matches
   */
  public boolean matches(RuleCondition condition) {
    return condition != null && stateType == condition && triggered;
  }
}
