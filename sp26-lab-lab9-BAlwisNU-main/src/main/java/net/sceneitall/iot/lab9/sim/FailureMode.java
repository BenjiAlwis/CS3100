package net.sceneitall.iot.lab9.sim;

/** Simulated failure mode for a device in the network. */
public enum FailureMode {
  /** Device responds normally. */
  NONE,
  /** Device accepts connection but never replies (causes timeout). */
  TIMEOUT,
  /** Device fails roughly 60% of the time (transient failures). */
  INTERMITTENT,
  /** Device responds but reports partial failure (command could not execute). */
  PARTIAL,
  /** Network cannot reach device at all. */
  UNREACHABLE
}
