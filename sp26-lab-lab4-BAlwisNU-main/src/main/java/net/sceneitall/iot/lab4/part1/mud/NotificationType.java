package net.sceneitall.iot.lab4.part1.mud;

/** Enumeration of notification types in the "big ball of mud" implementation. */
public enum NotificationType {
  /** Send notification immediately. */
  IMMEDIATE,

  /** Queue notification for daily digest. */
  DAILY_DIGEST,

  /** Smart notification based on time and preferences. */
  SMART
}
