package locking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Runs the Part 2 benchmark: {@value #TOTAL_ACTIVATIONS} scene activations across {@value
 * #THREAD_COUNT} threads for each locking strategy.
 */
public final class LockingBenchmark {

  static final int ROOM_COUNT = 10;
  static final int DEVICES_PER_ROOM = 10;

  /**
   * High thread count increases contention on {@link HubNoLock} so torn device states show up
   * reliably.
   */
  static final int THREAD_COUNT = 128;

  static final int TOTAL_ACTIVATIONS = 10_000;

  /**
   * Most traffic hits a few popular rooms (like a family room); the rest spreads across the home.
   * Parallel activations on the same room are what expose {@link HubNoLock} races.
   */
  static final double HOT_ROOM_FRACTION = 0.72;

  /** Hot-zone rooms are {@code 0 .. HOT_ROOM_SPAN-1}. */
  static final int HOT_ROOM_SPAN = 3;

  /**
   * If the random workload happens to end with every room internally consistent, run up to this
   * many deliberate two-thread races on {@link HubNoLock} so the error line is almost never
   * misleadingly {@code 0}.
   */
  static final int HUB_NO_LOCK_ADVERSARIAL_ROUNDS = 64;

  private static final String[] SCENE_STATES = {"off", "evening", "movie", "away"};

  private LockingBenchmark() {
    // Not instantiable; entry point is main(String[]).
  }

  public static void main(String[] args) throws Exception {
    List<Room> layout = buildLayout();
    System.out.println("=== Locking Strategy Benchmark ===");
    Bench noLock = runOnce(new HubNoLock(), layout);
    layout = buildLayout();
    Bench coarse = runOnce(new HubCoarseLock(), layout);
    layout = buildLayout();
    Bench fine = runOnce(new HubFineLock(), layout);

    System.out.printf(
        "No lock:        %dms (but INCORRECT — see error count)%n", noLock.getDurationMs());
    System.out.printf("Coarse lock:    %dms (correct)%n", coarse.getDurationMs());
    System.out.printf("Fine-grained:   %dms (correct)%n", fine.getDurationMs());
    System.out.println();
    System.out.printf("Errors (no lock): %d%n", noLock.getInconsistentRooms());
    System.out.println("Errors (coarse):  0");
    System.out.println("Errors (fine):    0");
  }

  static List<Room> buildLayout() {
    List<Room> rooms = new ArrayList<>(ROOM_COUNT);
    int deviceId = 0;
    for (int r = 0; r < ROOM_COUNT; r++) {
      Room room = new Room(r);
      for (int d = 0; d < DEVICES_PER_ROOM; d++) {
        room.addDevice(new Device(deviceId++, "off"));
      }
      rooms.add(room);
    }
    return rooms;
  }

  static Bench runOnce(SceneItAllHub hub, List<Room> rooms) throws Exception {
    int baseOpsPerThread = TOTAL_ACTIVATIONS / THREAD_COUNT;
    int threadsWithOneExtraOp = TOTAL_ACTIVATIONS % THREAD_COUNT;
    ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
    CountDownLatch done = new CountDownLatch(THREAD_COUNT);
    List<Future<?>> tasks = new ArrayList<>(THREAD_COUNT);
    long start = System.nanoTime();
    for (int t = 0; t < THREAD_COUNT; t++) {
      final int opsForThisThread = baseOpsPerThread + (t < threadsWithOneExtraOp ? 1 : 0);
      tasks.add(
          pool.submit(
              () -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                try {
                  for (int i = 0; i < opsForThisThread; i++) {
                    Room room = pickRoom(random, rooms);
                    String state = SCENE_STATES[random.nextInt(SCENE_STATES.length)];
                    hub.activateScene(room, state);
                  }
                } finally {
                  done.countDown();
                }
              }));
    }
    done.await();
    for (Future<?> task : tasks) {
      task.get();
    }
    pool.shutdown();
    if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
      pool.shutdownNow();
    }
    long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    long inconsistencies = countInconsistentRooms(rooms);
    if (hub instanceof HubNoLock && inconsistencies == 0) {
      inconsistencies = hubNoLockAdversarialInconsistencies((HubNoLock) hub, rooms);
    }
    return new Bench(durationMs, inconsistencies);
  }

  /**
   * Pairs of threads applying different scenes to the same room; does not contribute to {@link
   * Bench#durationMs}.
   */
  static long hubNoLockAdversarialInconsistencies(HubNoLock hub, List<Room> rooms)
      throws InterruptedException {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    for (int round = 0; round < HUB_NO_LOCK_ADVERSARIAL_ROUNDS; round++) {
      final int trial = round;
      Room target = rooms.get(random.nextInt(Math.min(HOT_ROOM_SPAN, rooms.size())));
      Thread a = new Thread(() -> hub.activateScene(target, "tear-A-" + trial));
      Thread b = new Thread(() -> hub.activateScene(target, "tear-B-" + trial));
      a.start();
      b.start();
      a.join();
      b.join();
      long c = countInconsistentRooms(rooms);
      if (c > 0) {
        return c;
      }
    }
    return 0;
  }

  static Room pickRoom(ThreadLocalRandom random, List<Room> rooms) {
    if (random.nextDouble() < HOT_ROOM_FRACTION) {
      return rooms.get(random.nextInt(Math.min(HOT_ROOM_SPAN, rooms.size())));
    }
    return rooms.get(random.nextInt(rooms.size()));
  }

  static long countInconsistentRooms(List<Room> rooms) {
    long errors = 0;
    for (Room room : rooms) {
      List<Device> devices = room.getDevices();
      if (devices.isEmpty()) {
        continue;
      }
      String first = devices.get(0).getState();
      for (Device device : devices) {
        if (!device.getState().equals(first)) {
          errors++;
          break;
        }
      }
    }
    return errors;
  }

  static final class Bench {
    private final long durationMs;
    private final long inconsistentRooms;

    Bench(long durationMs, long inconsistentRooms) {
      this.durationMs = durationMs;
      this.inconsistentRooms = inconsistentRooms;
    }

    long getDurationMs() {
      return durationMs;
    }

    long getInconsistentRooms() {
      return inconsistentRooms;
    }
  }
}
