package bugs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * SceneItAll-inspired concurrency examples. Each public method below has a concurrency defect — fix
 * in place and add your {@code // EXPLAIN:} reasoning.
 */
public final class ConcurrencyExamples {

  private static final int CHANNEL_COUNT = 10;
  private static final int DEVICES_PER_CHANNEL = 100;

  /** Bug 1: shared counter updated from many threads without synchronization. */
  private int deviceCount;

  /** Bug 2: per-device scene strings updated without atomicity across the array. */
  private final String[] scenePerDevice = new String[5];

  private final Object deviceLock = new Object();
  private final Object roomLock = new Object();

  private volatile int brightnessPercent = 100;

  /**
   * Bug 1 — vanishing increments: multiple channel scans increment a shared counter.
   *
   * @return how many devices were "found" (should be {@code CHANNEL_COUNT * DEVICES_PER_CHANNEL})
   */
  public int scanAllChannels() throws InterruptedException {
    deviceCount = 0;
    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < CHANNEL_COUNT; i++) {
      Thread t =
          new Thread(
              () -> {
                for (int j = 0; j < DEVICES_PER_CHANNEL; j++) {
                  deviceCount++;
                }
              });
      threads.add(t);
      t.start();
    }
    for (Thread t : threads) {
      t.join();
    }
    // EXPLAIN: (student) Why was the count sometimes low, and how did you fix it?
    return deviceCount;
  }

  /**
   * Bug 2 — ghost scene: every device should end up in {@code sceneName}, but interleaved updates
   * can leave a mix of two scenes.
   */
  public void activateScene(String sceneName) {
    for (int i = 0; i < scenePerDevice.length; i++) {
      scenePerDevice[i] = sceneName;
    }
    // EXPLAIN: (student) What is the critical section, and how does your lock scope prevent the
    // ghost state?
  }

  /** Used by tests — snapshot of device scenes (defensive copy). */
  public String[] getScenePerDeviceSnapshot() {
    String[] copy = new String[scenePerDevice.length];
    System.arraycopy(scenePerDevice, 0, copy, 0, scenePerDevice.length);
    return copy;
  }

  /** Reset scenes for the next test iteration. */
  public void resetScenes(String initial) {
    for (int i = 0; i < scenePerDevice.length; i++) {
      scenePerDevice[i] = initial;
    }
  }

  /**
   * Bug 3 — inconsistent lock ordering: firmware path locks device then room; scene path locks room
   * then device. Can deadlock.
   */
  public void updateFirmware() {
    synchronized (deviceLock) {
      busyWork();
      synchronized (roomLock) {
        busyWork();
      }
    }
    // EXPLAIN: (student) Which lock is always acquired first after your fix, and why?
  }

  /**
   * Bug 3 — opposite lock order from {@link #updateFirmware()} (deadlock with concurrent calls).
   */
  public void activateSceneWithFirmwareCheck() {
    synchronized (roomLock) {
      busyWork();
      synchronized (deviceLock) {
        busyWork();
      }
    }
  }

  /**
   * Bug 4 — two independent async steps; the brighten may run before or after the dim, so the final
   * brightness is nondeterministic.
   */
  public int dimThenBrighten() throws Exception {
    CompletableFuture<Void> dim =
        CompletableFuture.runAsync(
            () -> {
              brightnessPercent = 30;
            });
    CompletableFuture<Void> bright =
        CompletableFuture.runAsync(
            () -> {
              brightnessPercent = 80;
            });
    CompletableFuture.allOf(dim, bright).join();
    // EXPLAIN: (student) Could you use thenApply instead of thenCompose? Why or why not?
    return brightnessPercent;
  }

  public int getBrightnessPercent() {
    return brightnessPercent;
  }

  public void resetBrightness(int value) {
    brightnessPercent = value;
  }

  private static void busyWork() {
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
