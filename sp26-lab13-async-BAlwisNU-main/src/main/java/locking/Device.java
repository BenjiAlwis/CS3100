package locking;

/** Mutable device state inside a hub room (used by locking benchmark). */
public final class Device {

  private final int id;
  private volatile String state;

  public Device(int id, String initialState) {
    this.id = id;
    this.state = initialState;
  }

  public int getId() {
    return id;
  }

  public String getState() {
    return state;
  }

  public void setState(String newState) {
    this.state = newState;
  }
}
