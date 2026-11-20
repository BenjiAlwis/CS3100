package sceneitall.models;

/**
 * Condition type for automation rules. Matches the trigger types that devices can report (motion,
 * temperature threshold, door state).
 */
public enum RuleCondition {
  MOTION_DETECTED,
  TEMPERATURE_ABOVE,
  DOOR_OPENED
}
