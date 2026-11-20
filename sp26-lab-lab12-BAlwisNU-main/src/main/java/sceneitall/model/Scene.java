package sceneitall.model;

import java.util.Map;

/**
 * A named preset that maps device names to target state values.
 *
 * <p>Target value semantics depend on device type:
 *
 * <ul>
 *   <li>Light: brightness (0-100)
 *   <li>Fan: FanSpeed ordinal (0 = LOW, 1 = MEDIUM, 2 = HIGH)
 *   <li>Shade: position (0 = closed, 100 = open)
 * </ul>
 */
public final class Scene {

  private final String name;
  private final Map<String, Integer> targets;

  /**
   * Creates a new scene.
   *
   * @param name the display name for this scene
   * @param targets device name to target value mappings
   */
  public Scene(String name, Map<String, Integer> targets) {
    this.name = name;
    this.targets = Map.copyOf(targets); // defensive copy
  }

  /** Returns the scene name. */
  public String getName() {
    return name;
  }

  /** Returns an unmodifiable map of device name to target value. */
  public Map<String, Integer> getTargets() {
    return targets; // already unmodifiable from Map.copyOf
  }

  @Override
  public String toString() {
    return "Scene[" + name + ", targets=" + targets + "]";
  }
}
