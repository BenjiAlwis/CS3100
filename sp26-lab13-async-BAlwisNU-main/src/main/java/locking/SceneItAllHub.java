package locking;

/**
 * Hub simulator: atomically (when implemented correctly) applies a scene state to all devices in a
 * room.
 */
public interface SceneItAllHub {

  /**
   * Every device in {@code room} should end up with {@code sceneState} when this call returns
   * (under a correct implementation).
   */
  void activateScene(Room room, String sceneState);
}
