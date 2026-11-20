package locking;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Sanity check that {@link HubNoLock} can leave devices in the same room disagreeing. */
class HubNoLockContentionTest {

  @Test
  void twoThreadsOverlappingOnSameRoomCanProduceInconsistentDevices() throws Exception {
    boolean sawTear = false;
    for (int attempt = 0; attempt < 5_000; attempt++) {
      final int trial = attempt;
      List<Room> layout = LockingBenchmark.buildLayout();
      HubNoLock hub = new HubNoLock();
      Room room0 = layout.get(0);
      Thread a = new Thread(() -> hub.activateScene(room0, "scene-A-" + trial));
      Thread b = new Thread(() -> hub.activateScene(room0, "scene-B-" + trial));
      a.start();
      b.start();
      a.join();
      b.join();
      if (LockingBenchmark.countInconsistentRooms(layout) > 0) {
        sawTear = true;
        break;
      }
    }
    assertThat(sawTear)
        .as("HubNoLock should be able to tear state with deliberate overlap")
        .isTrue();
  }
}
