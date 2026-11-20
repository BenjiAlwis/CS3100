package sceneitall.model;

/** A smart window shade with adjustable position. */
public final class Shade implements Device {

  private final String name;
  private int position; // 0 = closed, 100 = fully open

  /**
   * Creates a new shade that is fully closed.
   *
   * @param name the display name for this shade
   */
  public Shade(String name) {
    this.name = name;
    this.position = 0;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public DeviceType getType() {
    return DeviceType.SHADE;
  }

  /** Returns the current position (0 = closed, 100 = fully open). */
  public int getPosition() {
    return position;
  }

  /**
   * Sets the shade position.
   *
   * @param position 0-100
   * @throws IllegalArgumentException if position is out of range
   */
  public void setPosition(int position) {
    if (position < 0 || position > 100) {
      throw new IllegalArgumentException("Position must be between 0 and 100, got: " + position);
    }
    this.position = position;
  }

  @Override
  public String toString() {
    return "Shade[" + name + ", position=" + position + "]";
  }
}
