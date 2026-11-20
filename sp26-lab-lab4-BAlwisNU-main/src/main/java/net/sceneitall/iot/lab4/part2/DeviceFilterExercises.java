package net.sceneitall.iot.lab4.part2;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sceneitall.iot.lab4.domain.DimmableLight;
import net.sceneitall.iot.lab4.domain.IoTDevice;
import net.sceneitall.iot.lab4.domain.Light;

/**
 * Exercises for practicing lambdas and predicates with IoT devices.
 *
 * <p>This class contains exercises for Lab 4 Part 2, where you'll practice: - Writing simple
 * lambdas - Using instanceof for type checking - Composing predicates with and(), or(), negate() -
 * Using predicates with the Stream API
 *
 * <p>Each method should return a {@link Predicate} or filtered {@link List}. Replace the {@code
 * return null;} statements with your implementations.
 */
public final class DeviceFilterExercises {

  private DeviceFilterExercises() {
    // Utility class - prevent instantiation
  }

  /**
   * Returns a predicate that tests if a device is currently available.
   *
   * <p><strong>Hint:</strong> IoTDevice has an {@code isAvailable()} method.
   *
   * <p><strong>Example usage:</strong>
   *
   * <pre>{@code
   * Predicate<IoTDevice> filter = availableDevices();
   * boolean result = filter.test(myDevice); // true if device is available
   * }</pre>
   *
   * @return a predicate that returns true if the device is available
   */
  public static Predicate<IoTDevice> availableDevices() {
    // TODO: Return a lambda that checks if a device is available
    return device -> device.isAvailable();
  }

  /**
   * Returns a predicate that tests if a device is a Light (or subtype of Light).
   *
   * <p><strong>Hint:</strong> Use the {@code instanceof} operator.
   *
   * <p><strong>Example usage:</strong>
   *
   * <pre>{@code
   * Predicate<IoTDevice> filter = lightsOnly();
   * boolean result = filter.test(myDevice); // true if device is a Light or DimmableLight
   * }</pre>
   *
   * @return a predicate that returns true if the device is a Light
   */
  public static Predicate<IoTDevice> lightsOnly() {
    // TODO: Return a lambda that checks if a device is an instance of Light
    return device -> device instanceof Light;
  }

  /**
   * Returns a predicate that tests if a device is a critical device.
   *
   * <p><strong>Hint:</strong> IoTDevice has an {@code isCritical()} method.
   *
   * <p><strong>Example usage:</strong>
   *
   * <pre>{@code
   * Predicate<IoTDevice> filter = criticalDevices();
   * boolean result = filter.test(myDevice); // true if device is critical
   * }</pre>
   *
   * @return a predicate that returns true if the device is critical
   */
  public static Predicate<IoTDevice> criticalDevices() {
    // TODO: Return a lambda that checks if a device is critical
    return device -> device.isCritical();
  }

  /**
   * Returns a predicate that tests if a DimmableLight's brightness is above a threshold.
   *
   * <p>If the device is not a DimmableLight, the predicate should return false.
   *
   * <p><strong>Hint:</strong> You'll need to
   *
   * <ul>
   *   <li>Check if the device is an instanceof DimmableLight
   *   <li>If so, cast it and check if its brightness is above the threshold
   *   <li>Return false otherwise
   * </ul>
   *
   * <p><strong>Example usage:</strong>
   *
   * <pre>{@code
   * Predicate<IoTDevice> filter = brightnessAbove(50);
   * boolean result = filter.test(myLight); // true if light is dimmable and brightness > 50
   * }</pre>
   *
   * @param threshold the minimum brightness (0-100)
   * @return a predicate that returns true if the device is a DimmableLight with brightness above
   *     the threshold
   */
  public static Predicate<IoTDevice> brightnessAbove(int threshold) {
    // TODO: Return a lambda that:
    // 1. Checks if the device is a DimmableLight
    // 2. If so, checks if its brightness is above the threshold
    // 3. Returns false otherwise
    return device -> {
      if (device instanceof DimmableLight dimmable) {
        return dimmable.getBrightness() > threshold;
      }
      return false;
    };
  }

  /**
   * Returns a predicate that tests if a device is both available AND a light.
   *
   * <p><strong>Do not write a new lambda!</strong> Use the {@link Predicate#and(Predicate)} method
   * to compose {@link #availableDevices()} and {@link #lightsOnly()}.
   *
   * <p><strong>Example usage:</strong>
   *
   * <pre>{@code
   * Predicate<IoTDevice> filter = availableLights();
   * boolean result = filter.test(myDevice); // true if device is available AND a light
   * }</pre>
   *
   * @return a composed predicate using the and() method
   */
  public static Predicate<IoTDevice> availableLights() {
    // TODO: Compose availableDevices() and lightsOnly() using and()
    return availableDevices().and(lightsOnly());
  }

  /**
   * Returns a predicate that tests if a device is either unavailable OR critical.
   *
   * <p><strong>Do not write a new lambda!</strong> Use predicate composition: - Use {@link
   * Predicate#negate()} to invert availableDevices() - Use {@link Predicate#or(Predicate)} to
   * combine with criticalDevices()
   *
   * <p>A device needs attention if it's unavailable OR if it's critical (or both).
   *
   * <p><strong>Example usage:</strong>
   *
   * <pre>{@code
   * Predicate<IoTDevice> filter = needsAttention();
   * boolean result = filter.test(myDevice); // true if device is unavailable OR critical
   * }</pre>
   *
   * @return a composed predicate that returns true if the device is unavailable or critical
   */
  public static Predicate<IoTDevice> needsAttention() {
    // TODO: Compose predicates using or() and negate()
    // A device needs attention if it's unavailable OR if it's critical
    return availableDevices().negate().or(criticalDevices());
  }

  /**
   * Returns all available dimmable lights with brightness above 50%.
   *
   * <p>Use the Stream API with your predicates to filter the devices.
   *
   * <p><strong>Hint:</strong> Use {@code devices.stream()}, then {@code .filter(...)}, then {@code
   * .collect(Collectors.toList())}.
   *
   * <p><strong>Example usage:</strong>
   *
   * <pre>{@code
   * List<IoTDevice> allDevices = Arrays.asList(light1, light2, camera, dimmableLight);
   * List<IoTDevice> bright = findBrightLights(allDevices);
   * // Returns only available DimmableLights with brightness > 50
   * }</pre>
   *
   * @param devices the list of devices to filter
   * @return a list of devices that are available, dimmable lights with brightness above 50
   */
  public static List<IoTDevice> findBrightLights(List<IoTDevice> devices) {
    // TODO: Use devices.stream()
    //       .filter(your predicates)
    //       .collect(Collectors.toList())
    // You'll want to filter for:
    // 1. Available devices
    // 2. Lights (use lightsOnly() or check instanceof DimmableLight)
    // 3. Brightness above 50
    return devices.stream()
        .filter(availableDevices())
        .filter(brightnessAbove(50))
        .collect(Collectors.toList());
  }
}
