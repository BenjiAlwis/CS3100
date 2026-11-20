package sceneitall.core;

import java.time.LocalTime;

/** Port for current time, so quiet-hours behavior is testable. */
public interface TimeProvider {
  LocalTime now();
}