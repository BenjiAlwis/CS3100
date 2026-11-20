package locking;

/** One lock per room — safe, and allows concurrent activations in different rooms. */
public final class HubFineLock implements SceneItAllHub {

  @Override
  public void activateScene(Room room, String sceneState) {
    synchronized (room) {
      for (Device device : room.getDevices()) {
        device.setState(sceneState);
      }
    }
  }
}
