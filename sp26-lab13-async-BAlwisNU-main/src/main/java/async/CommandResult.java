package async;

/** Result of executing a {@link DeviceCommand}. */
public final class CommandResult {

  private final boolean success;
  private final String detail;

  private CommandResult(boolean success, String detail) {
    this.success = success;
    this.detail = detail;
  }

  public static CommandResult success(String detail) {
    return new CommandResult(true, detail);
  }

  public static CommandResult success() {
    return success("ok");
  }

  public static CommandResult failure(String reason) {
    return new CommandResult(false, reason);
  }

  public boolean isSuccess() {
    return success;
  }

  public boolean isFailure() {
    return !success;
  }

  public String getDetail() {
    return detail;
  }
}
