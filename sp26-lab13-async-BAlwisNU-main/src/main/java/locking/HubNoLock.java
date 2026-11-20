package locking;

import java.util.concurrent.locks.LockSupport;

/**
 * Fast path: no synchronization (incorrect under concurrent load — devices in a room can disagree).
 *
 * <p>A short voluntary delay per device widens the race window so benchmarks usually observe torn
 * room state. Real hubs have comparable per-device latency over the radio; we model a best-case
 * fast link, not zero time.
 */
public final class HubNoLock implements SceneItAllHub {

  /** Gives competing activations time to interleave between device writes (nanoseconds). */
  private static final long PARK_PER_DEVICE_NS = 55_000L;

  @Override
  public void activateScene(Room room, String sceneState) {
    for (Device device : room.getDevices()) {
      device.setState(sceneState);
      LockSupport.parkNanos(PARK_PER_DEVICE_NS);
    }
  }
}
