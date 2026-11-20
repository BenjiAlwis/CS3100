package net.sceneitall.iot.lab2.stretch;

import java.util.ArrayList;
import java.util.List;

import net.sceneitall.iot.IoTDevice;

/**
 * Stretch Goal Exercises.
 *
 * <p>These are optional extension exercises for students who finish early.
 */
public final class StretchExercises {

  private StretchExercises() {
    // Prevent instantiation - this is a utility class
  }

  /**
   * Stretch Goal 3: Filter devices by type using generics.
   *
   * <p>Filters a list of devices to return only those of a specific type.
   *
   * <p>Example usage:
   *
   * <pre>
   *   List&lt;Light&gt; lights = filterByType(allDevices, Light.class);
   *   List&lt;Fan&gt; fans = filterByType(allDevices, Fan.class);
   * </pre>
   *
   * <p><b>TODO (Optional):</b> Implement this method.
   *
   * @param <T> the type of device to filter for
   * @param devices the list of devices to filter
   * @param type the class object representing the desired type
   * @return a new list containing only devices of the specified type
   */
  public static <T extends IoTDevice> List<T> filterByType(List<IoTDevice> devices, Class<T> type) {
    // TODO: Implement this method
    // Hint: Use type.isInstance(device) to check if a device matches
    return new ArrayList<>();
  }
}
