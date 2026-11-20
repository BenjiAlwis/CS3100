package sceneitall.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/** Utility methods for date and time operations. */
public final class DateUtils {
  private DateUtils() {}

  /**
   * Returns true if the current time falls within the quiet hours window. Quiet hours are specified
   * as "HH:mm" strings (e.g., "22:00" to "07:00").
   *
   * @param quietStart start of quiet hours
   * @param quietEnd end of quiet hours
   * @return true if currently within quiet hours
   */
  public static boolean isAfterHours(String quietStart, String quietEnd) {
    LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
    LocalTime start = quietStart != null ? LocalTime.parse(quietStart) : LocalTime.of(22, 0);
    LocalTime end = quietEnd != null ? LocalTime.parse(quietEnd) : LocalTime.of(7, 0);
    LocalTime current = now.toLocalTime();
    if (start.isAfter(end)) {
      return current.isAfter(start) || current.isBefore(end);
    }
    return current.isAfter(start) && current.isBefore(end);
  }

  /** Returns the current system timestamp in milliseconds. */
  public static long currentTimestamp() {
    return System.currentTimeMillis();
  }
}
