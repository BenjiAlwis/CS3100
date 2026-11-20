package locking;

/** Single lock for the entire hub — correct but serializes all activations. */
public final class HubCoarseLock implements SceneItAllHub {

  private final Object hubLock = new Object();

  @Override
  public void activateScene(Room room, String sceneState) {
    synchronized (hubLock) {
      for (Device device : room.getDevices()) {
        device.setState(sceneState);
      }
    }
  }
}
