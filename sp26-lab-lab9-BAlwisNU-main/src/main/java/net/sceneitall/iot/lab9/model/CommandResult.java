package net.sceneitall.iot.lab9.model;

/** Result of sending a command to a device. May indicate partial failure. */
public class CommandResult {

  private final DeviceState state;
  private final boolean partialFailure;

  public CommandResult(DeviceState state, boolean partialFailure) {
    this.state = state;
    this.partialFailure = partialFailure;
  }

  public DeviceState getState() {
    return state;
  }

  public boolean isPartialFailure() {
    return partialFailure;
  }
}
