package sceneitall.model;

/** Speed settings for a smart fan. */
public enum FanSpeed {
  LOW,
  MEDIUM,
  HIGH;

  /**
   * Returns the FanSpeed corresponding to the given ordinal.
   *
   * @param ordinal 0 = LOW, 1 = MEDIUM, 2 = HIGH
   * @return the matching FanSpeed
   * @throws IllegalArgumentException if the ordinal is out of range
   */
  public static FanSpeed fromOrdinal(int ordinal) {
    FanSpeed[] values = values();
    if (ordinal < 0 || ordinal >= values.length) {
      throw new IllegalArgumentException("Invalid FanSpeed ordinal: " + ordinal);
    }
    return values[ordinal];
  }
}
