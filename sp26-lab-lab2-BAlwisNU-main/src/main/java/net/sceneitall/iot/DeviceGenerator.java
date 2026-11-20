package net.sceneitall.iot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for generating test IoT devices.
 *
 * <p>This class is used in the performance comparison exercises (Part 3) to generate large numbers
 * of devices for benchmarking different collection types.
 */
public final class DeviceGenerator {

  private static final Random RANDOM = new Random(42); // Fixed seed for reproducibility

  private DeviceGenerator() {
    // Prevent instantiation
  }

  /**
   * Generates a list of mixed device types.
   *
   * <p>The generated devices include a mix of:
   *
   * <ul>
   *   <li>SwitchedLight
   *   <li>DimmableLight
   *   <li>TunableWhiteLight
   *   <li>Fan
   * </ul>
   *
   * @param count the number of devices to generate
   * @return a list of generated devices
   */
  public static List<IoTDevice> generateMixedDevices(int count) {
    List<IoTDevice> devices = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      devices.add(generateDevice(i));
    }
    return devices;
  }

  /**
   * Generates a single device with a unique ID.
   *
   * @param index the index used to generate a unique ID
   * @return a randomly-typed device
   */
  private static IoTDevice generateDevice(int index) {
    String id = "device-" + index;
    int type = RANDOM.nextInt(4);

    switch (type) {
      case 0:
        return new SwitchedLight(id);
      case 1:
        return new DimmableLight(id, RANDOM.nextInt(101));
      case 2:
        return new TunableWhiteLight(id, 2700 + RANDOM.nextInt(3801), RANDOM.nextInt(101));
      case 3:
        return new Fan(id);
      default:
        return new SwitchedLight(id);
    }
  }

  /**
   * Generates a list of device IDs that exist in the generated list.
   *
   * <p>This is useful for lookup performance testing - it generates IDs that will actually be found
   * in the collection.
   *
   * @param deviceCount the number of devices that were generated
   * @param lookupCount the number of lookup IDs to generate
   * @return a list of device IDs for lookup testing
   */
  public static List<String> generateLookupIds(int deviceCount, int lookupCount) {
    List<String> ids = new ArrayList<>();
    Random lookupRandom = new Random(123); // Different seed for variety
    for (int i = 0; i < lookupCount; i++) {
      ids.add("device-" + lookupRandom.nextInt(deviceCount));
    }
    return ids;
  }
}
