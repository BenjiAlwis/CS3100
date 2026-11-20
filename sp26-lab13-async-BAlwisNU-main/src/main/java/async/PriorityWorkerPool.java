package async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Priority worker pool starter code (Lab 13 Part 3).
 *
 * <p>Students: replace {@link LinkedBlockingQueue} with {@link
 * java.util.concurrent.PriorityBlockingQueue}, finish {@link #submitCommand(DeviceCommand)}
 * (including {@code .exceptionally(...)}), and implement {@link #processNextCommand()}.
 */
public final class PriorityWorkerPool implements AutoCloseable {

  // TODO(student): swap for PriorityBlockingQueue so HIGH work is dequeued before LOW work.
  private final BlockingQueue<WorkItem> queue = new LinkedBlockingQueue<>();

  private final AtomicBoolean shutdown = new AtomicBoolean(false);
  private final List<Thread> workers = new ArrayList<>();

  public PriorityWorkerPool(int workerCount) {
    for (int i = 0; i < workerCount; i++) {
      Thread thread = new Thread(this::workerLoop, "priority-worker-" + i);
      thread.setDaemon(true);
      workers.add(thread);
      thread.start();
    }
  }

  /**
   * Enqueues work and returns a future that should complete when a worker finishes {@code command}.
   *
   * <p>TODO(student): complete {@link CompletableFuture} semantics and translate failures with
   * {@code .exceptionally(...)} into {@link CommandResult#failure(String)} — callers must not see a
   * completed exceptionally future for normal command failures.
   */
  public CompletableFuture<CommandResult> submitCommand(DeviceCommand command) {
    CompletableFuture<CommandResult> result = new CompletableFuture<>();
    try {
      queue.put(new WorkItem(command, result));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      result.completeExceptionally(e);
    }
    // TODO(student): add .exceptionally(...) per README
    return result;
  }

  /**
   * Waits for work, executes it, and completes the associated future.
   *
   * <p>TODO(student): {@code queue.take()} is already wired below — ensure failures map
   * consistently with {@link #submitCommand(DeviceCommand)} once you add {@code exceptionally}.
   */
  public void processNextCommand() throws InterruptedException {
    WorkItem item = queue.take();
    try {
      item.getCommand().execute();
      item.getFuture().complete(CommandResult.success());
    } catch (RuntimeException e) {
      item.getFuture().completeExceptionally(e);
    }
  }

  @Override
  public void close() throws InterruptedException {
    shutdown.set(true);
    for (Thread worker : workers) {
      worker.interrupt();
    }
    for (Thread worker : workers) {
      worker.join(2_000);
    }
  }

  private void workerLoop() {
    while (!shutdown.get()) {
      try {
        processNextCommand();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  static final class WorkItem implements Comparable<WorkItem> {

    private final DeviceCommand command;
    private final CompletableFuture<CommandResult> future;

    WorkItem(DeviceCommand command, CompletableFuture<CommandResult> future) {
      this.command = command;
      this.future = future;
    }

    DeviceCommand getCommand() {
      return command;
    }

    CompletableFuture<CommandResult> getFuture() {
      return future;
    }

    @Override
    public int compareTo(WorkItem other) {
      return this.command.compareTo(other.command);
    }
  }
}
