package async;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class PriorityWorkerPoolTest {

  @Test
  void allCommandsComplete() throws Exception {
    try (PriorityWorkerPool pool = new PriorityWorkerPool(2)) {
      List<CompletableFuture<CommandResult>> futures = new ArrayList<>();
      for (int i = 0; i < 50; i++) {
        DeviceCommand command =
            new DeviceCommand(
                "cmd-" + i,
                Priority.MEDIUM,
                () -> {
                  /* no-op work item */
                });
        futures.add(pool.submitCommand(command));
      }
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);
      assertThat(futures).hasSize(50).allMatch(f -> f.isDone() && !f.isCompletedExceptionally());
    }
  }

  @Test
  void highPriorityCommandsFinishBeforeLowOnAverageFIFO() throws Exception {
    try (PriorityWorkerPool pool = new PriorityWorkerPool(2)) {
      List<CompletableFuture<CommandResult>> futures = new ArrayList<>();
      List<Priority> completionOrder = new ArrayList<>();
      Object lock = new Object();

      // Flood the queue with lower-priority work first; a correct priority queue still surfaces
      // HIGH early.
      for (int i = 0; i < 20; i++) {
        DeviceCommand low =
            slowCommand(
                "low-" + i,
                Priority.LOW,
                () -> busySleep(2)); // small delay so ordering differences show up under contention
        futures.add(trackedSubmit(pool, low, completionOrder, lock));
      }
      for (int i = 0; i < 20; i++) {
        DeviceCommand medium = slowCommand("med-" + i, Priority.MEDIUM, () -> busySleep(1));
        futures.add(trackedSubmit(pool, medium, completionOrder, lock));
      }
      for (int i = 0; i < 10; i++) {
        DeviceCommand high = slowCommand("high-" + i, Priority.HIGH, () -> busySleep(1));
        futures.add(trackedSubmit(pool, high, completionOrder, lock));
      }

      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);

      double avgHighIndex = averageCompletionIndex(completionOrder, Priority.HIGH);
      double avgLowIndex = averageCompletionIndex(completionOrder, Priority.LOW);
      assertThat(avgHighIndex).isLessThan(avgLowIndex);
    }
  }

  @Test
  void failedCommandReturnsFailureResultInsteadOfThrowing() throws Exception {
    try (PriorityWorkerPool pool = new PriorityWorkerPool(1)) {
      DeviceCommand boom =
          new DeviceCommand(
              "boom",
              Priority.HIGH,
              () -> {
                throw new IllegalStateException("device unavailable");
              });
      CommandResult result = pool.submitCommand(boom).get(5, TimeUnit.SECONDS);
      assertThat(result.isFailure()).isTrue();
    }
  }

  private static CompletableFuture<CommandResult> trackedSubmit(
      PriorityWorkerPool pool, DeviceCommand command, List<Priority> completionOrder, Object lock) {
    return pool.submitCommand(command)
        .whenComplete(
            (result, error) -> {
              synchronized (lock) {
                completionOrder.add(command.getPriority());
              }
            });
  }

  private static DeviceCommand slowCommand(String name, Priority priority, Runnable work) {
    return new DeviceCommand(name, priority, work);
  }

  private static void busySleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private static double averageCompletionIndex(List<Priority> completionOrder, Priority target) {
    long sum = 0;
    long count = 0;
    for (int i = 0; i < completionOrder.size(); i++) {
      if (completionOrder.get(i) == target) {
        sum += i;
        count++;
      }
    }
    assertThat(count).isPositive();
    return (double) sum / count;
  }
}
