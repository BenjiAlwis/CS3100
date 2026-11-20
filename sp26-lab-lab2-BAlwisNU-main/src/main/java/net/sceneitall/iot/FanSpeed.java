package net.sceneitall.iot;

/**
 * Enumeration of available fan speed settings.
 *
 * <p>Fans typically support discrete speed levels rather than continuous control. This enum
 * represents the common speed options available on most ceiling and standing fans.
 */
public enum FanSpeed {
  /** Fan is off (no rotation). */
  OFF,

  /** Lowest speed setting - gentle airflow. */
  LOW,

  /** Medium speed setting - moderate airflow. */
  MEDIUM,

  /** Highest speed setting - maximum airflow. */
  HIGH
}
