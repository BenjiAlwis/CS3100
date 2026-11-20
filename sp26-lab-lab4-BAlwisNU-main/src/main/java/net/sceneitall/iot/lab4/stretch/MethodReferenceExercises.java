package net.sceneitall.iot.lab4.stretch;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import net.sceneitall.iot.lab4.domain.IoTDevice;
import net.sceneitall.iot.lab4.domain.Light;

/**
 * Exercises for practicing method references.
 *
 * <p>Method references are a shorthand for lambdas that just call a single method. They make code
 * more concise and often more readable.
 *
 * <p><strong>Method reference types:</strong>
 *
 * <ul>
 *   <li><strong>Instance method:</strong> {@code object::methodName} → {@code x ->
 *       object.methodName(x)}
 *   <li><strong>Instance method on parameter:</strong> {@code ClassName::methodName} → {@code x ->
 *       x.methodName()}
 *   <li><strong>Static method:</strong> {@code ClassName::staticMethod} → {@code x ->
 *       ClassName.staticMethod(x)}
 *   <li><strong>Constructor:</strong> {@code ClassName::new} → {@code x -> new ClassName(x)}
 * </ul>
 *
 * <p><strong>Stretch Goal:</strong> Convert the lambdas below to method references where possible.
 */
public final class MethodReferenceExercises {

  private MethodReferenceExercises() {
    // Utility class - prevent instantiation
  }

  /**
   * Lambda that gets the ID from an IoTDevice.
   *
   * <p>TODO: Convert this to a method reference.
   *
   * <p><strong>Hint:</strong> This is an instance method on the parameter type (IoTDevice).
   *
   * <p><strong>Lambda version:</strong> {@code device -> device.getId()}
   *
   * <p><strong>Method reference version:</strong> {@code IoTDevice::getId}
   */
  public static Function<IoTDevice, String> getDeviceId() {
    // TODO: Convert to method reference
    return device -> device.getId();
  }

  /**
   * Lambda that turns on a Light.
   *
   * <p>TODO: Convert this to a method reference.
   *
   * <p><strong>Hint:</strong> This is an instance method on the parameter type (Light).
   *
   * <p><strong>Lambda version:</strong> {@code light -> light.turnOn()}
   *
   * <p><strong>Method reference version:</strong> {@code Light::turnOn}
   */
  public static Consumer<Light> turnOnLight() {
    // TODO: Convert to method reference
    return light -> light.turnOn();
  }

  /**
   * Lambda that checks if an IoTDevice is available.
   *
   * <p>TODO: Convert this to a method reference.
   *
   * <p><strong>Hint:</strong> This is an instance method on the parameter type (IoTDevice).
   *
   * <p><strong>Lambda version:</strong> {@code device -> device.isAvailable()}
   *
   * <p><strong>Method reference version:</strong> {@code IoTDevice::isAvailable}
   */
  public static Predicate<IoTDevice> isDeviceAvailable() {
    // TODO: Convert to method reference
    return device -> device.isAvailable();
  }

  /**
   * Lambda that returns the maximum of two integers.
   *
   * <p>TODO: Convert this to a method reference.
   *
   * <p><strong>Hint:</strong> This is a static method on Math.
   *
   * <p><strong>Lambda version:</strong> {@code (a, b) -> Math.max(a, b)}
   *
   * <p><strong>Method reference version:</strong> {@code Math::max}
   */
  public static BiFunction<Integer, Integer, Integer> maximum() {
    // TODO: Convert to method reference
    return (a, b) -> Math.max(a, b);
  }

  /**
   * Lambda that prints a string.
   *
   * <p>TODO: Convert this to a method reference.
   *
   * <p><strong>Hint:</strong> This is an instance method on a specific object (System.out).
   *
   * <p><strong>Lambda version:</strong> {@code s -> System.out.println(s)}
   *
   * <p><strong>Method reference version:</strong> {@code System.out::println}
   */
  public static Consumer<String> printer() {
    // TODO: Convert to method reference
    return s -> System.out.println(s);
  }
}
