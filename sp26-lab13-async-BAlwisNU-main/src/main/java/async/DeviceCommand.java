package async;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Work item for {@link PriorityWorkerPool}. Ordering in a {@link
 * java.util.concurrent.PriorityBlockingQueue} is determined by priority first, then FIFO
 * tie-breaking.
 */
public final class DeviceCommand implements Comparable<DeviceCommand> {

  private static final AtomicLong SEQUENCE = new AtomicLong();

  private final long sequence = SEQUENCE.getAndIncrement();
  private final Priority priority;
  private final String name;
  private final Runnable action;

  public DeviceCommand(String name, Priority priority, Runnable action) {
    this.name = name;
    this.priority = priority;
    this.action = action;
  }

  public String getName() {
    return name;
  }

  public Priority getPriority() {
    return priority;
  }

  public void execute() {
    action.run();
  }

  @Override
  public int compareTo(DeviceCommand other) {
    int byPriority = Integer.compare(this.priority.ordinal(), other.priority.ordinal());
    if (byPriority != 0) {
      return byPriority;
    }
    return Long.compare(this.sequence, other.sequence);
  }
}
