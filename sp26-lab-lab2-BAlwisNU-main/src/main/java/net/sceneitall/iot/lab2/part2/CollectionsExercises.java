package net.sceneitall.iot.lab2.part2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sceneitall.iot.DimmableLight;
import net.sceneitall.iot.Fan;
import net.sceneitall.iot.IoTDevice;
import net.sceneitall.iot.Light;
import net.sceneitall.iot.SwitchedLight;
import net.sceneitall.iot.TunableWhiteLight;

/**
 * Part 2: Collections Selection Exercises.
 *
 * <p>This class contains exercises for choosing and using the right collection types. Complete the
 * TODO sections in each method.
 */
public final class CollectionsExercises {

  private CollectionsExercises() {
    // Prevent instantiation - this is a utility class
  }

  /**
   * Exercise 2.1: Fix the Generics Bug.
   *
   * <p>This code has a bug! The raw type List allows any object to be added, which causes a
   * ClassCastException at runtime.
   *
   * <p><b>TODO:</b> Fix this code using generics so the compiler catches the bug. The string should
   * cause a compile-time error, not a runtime crash.
   *
   * <p>After fixing, comment out or remove the problematic line so your code can compile.
   */
  // Remove the SuppressWarnings when you fix the code.
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void demonstrateGenericsBug() {
    List<Light> devices = new ArrayList<Light>(); // Raw type warning! TODO: Fix this
    devices.add(new DimmableLight("test", 100));
    // devices.add("oops, this is a string"); // No compile error... TODO: This line should fail

    // This will crash at runtime!
    for (Light light : devices) {
      // Light light = (Light) obj;
      light.turnOn();
    }
  }

  /**
   * Exercise 2.2 Scenario A: Device Registry.
   *
   * <p>Create a device registry that stores devices by their unique ID. You need to quickly look up
   * a device by its ID.
   *
   * <p><b>TODO:</b> Choose the right collection type and implement this method.
   *
   * <p>Questions to consider:
   *
   * <ul>
   *   <li>Do you need to look up by key or by position?
   *   <li>Do keys need to be unique?
   * </ul>
   *
   * @return a collection containing at least 3 devices with meaningful IDs
   */
  public static Map<String, IoTDevice> createDeviceRegistry() {
    // TODO: Choose the right collection type
    // TODO: Add at least 3 devices with meaningful IDs
    // TODO: Return the collection

    Map<String, IoTDevice> registry = new HashMap<>();

    registry.put("living-room-main", new TunableWhiteLight("living-room-main", 3000, 80));
    registry.put("bedroom-lamp", new DimmableLight("bedroom-lamp", 60));
    registry.put("kitchen-fan", new Fan("kitchen-fan"));

    return registry;
  }

  /**
   * Exercise 2.2 Scenario B: Devices by Room.
   *
   * <p>Track all devices in each room. A room can have multiple devices, and you need to find all
   * devices in a given room quickly.
   *
   * <p><b>TODO:</b> Implement this method.
   *
   * <p>Questions to consider:
   *
   * <ul>
   *   <li>What should the key type be?
   *   <li>What should the value type be?
   * </ul>
   *
   * @return a collection mapping room names to lists of devices, with at least 2 rooms
   */
  public static Map<String, List<IoTDevice>> groupDevicesByRoom() {

    Map<String, List<IoTDevice>> byRoom = new HashMap<>();

    List<IoTDevice> livingRoom = new ArrayList<>();
    livingRoom.add(new TunableWhiteLight("living-room-main", 4000, 100));
    livingRoom.add(new Fan("living-room-fan"));

    List<IoTDevice> bedroom = new ArrayList<>();
    bedroom.add(new DimmableLight("bedroom-lamp", 40));
    bedroom.add(new SwitchedLight("bedroom-ceiling"));

    byRoom.put("Living Room", livingRoom);
    byRoom.put("Bedroom", bedroom);

    return byRoom;
  }

  /**
   * Exercise 2.2 Scenario C: Online Device Tracking.
   *
   * <p>Track which devices are currently online. When a device comes online, you add it. When it
   * goes offline, you remove it. You frequently need to check "is this device online?"
   *
   * <p><b>TODO:</b> Choose a collection that efficiently answers "contains?" queries. Order doesn't
   * matter.
   *
   * @return a collection for tracking online device IDs
   */
  public static Set<String> createOnlineDeviceTracker() {
    Set<String> online = new HashSet<>();

    online.add("living-room-main");
    online.add("bedroom-lamp");
    online.add("kitchen-fan");

    return online;
  }

  /**
   * Main method for interactive testing.
   *
   * <p>When doing exploratory development, the fastest way to test is often NOT to write exhaustive
   * JUnit tests upfront. Instead, you can "poke at" your code interactively using a main method.
   * This lets you quickly see what your collections contain, how they behave, and whether your
   * design makes sense.
   *
   * <p>Once you understand what you're building, THEN you write proper tests. But for initial
   * exploration? A simple main method is your friend.
   *
   * <p>To run this:
   *
   * <pre>
   * ./gradlew runPart2
   * </pre>
   *
   * @param args command line arguments (unused)
   */
  public static void main(String[] args) {
    System.out.println("=== Part 2: Collections Exercises - Interactive Testing ===\n");

    // Test Exercise 2.1: Generics Bug
    System.out.println("--- Exercise 2.1: Generics Bug ---");
    System.out.println("Uncomment demonstrateGenericsBug() once you've fixed the generics:");
    System.out.println("(If you run it now, it will crash with a ClassCastException)");
    // demonstrateGenericsBug();
    System.out.println();

    // Test Exercise 2.2 Scenario A: Device Registry
    System.out.println("--- Exercise 2.2 Scenario A: Device Registry ---");
    Object registry = createDeviceRegistry();
    System.out.println("Registry type: " + registry.getClass().getSimpleName());
    System.out.println("Registry contents: " + registry);

    // Once you implement it as a Map, uncomment this:
    Map<String, IoTDevice> deviceMap = (Map<String, IoTDevice>) registry;
    System.out.println("Number of devices: " + deviceMap.size());
    System.out.println("Looking up device 'living-room-1': " + deviceMap.get("living-room-1"));
    System.out.println("Contains 'bedroom-lamp'? " + deviceMap.containsKey("bedroom-lamp"));
    System.out.println();

    // Test Exercise 2.2 Scenario B: Devices by Room
    System.out.println("--- Exercise 2.2 Scenario B: Devices by Room ---");
    Object roomGroups = groupDevicesByRoom();
    System.out.println("Room groups type: " + roomGroups.getClass().getSimpleName());
    System.out.println("Room groups contents: " + roomGroups);

    // Once you implement it as a Map<String, List<...>>, uncomment this:
    Map<String, List<IoTDevice>> byRoom = (Map<String, List<IoTDevice>>) roomGroups;
    System.out.println("Rooms: " + byRoom.keySet());
    System.out.println("Devices in 'Living Room': " + byRoom.get("Living Room"));
    // System.out.println("Number of devices in 'Bedroom': " + byRoom.get("Bedroom").size());
    List<IoTDevice> bedroomDevices = byRoom.get("Bedroom");
    if (bedroomDevices != null) {
      System.out.println("Number of devices in 'Bedroom': " + bedroomDevices.size());
    } else {
      System.out.println("Bedroom not found in room map");
    }
    System.out.println();

    // Test Exercise 2.2 Scenario C: Online Device Tracker
    System.out.println("--- Exercise 2.2 Scenario C: Online Device Tracker ---");
    Object tracker = createOnlineDeviceTracker();
    System.out.println("Tracker type: " + tracker.getClass().getSimpleName());
    System.out.println("Tracker contents: " + tracker);

    // Once you implement it as a Set, uncomment this:
    Set<String> onlineDevices = (Set<String>) tracker;
    System.out.println("Number of online devices: " + onlineDevices.size());
    onlineDevices.add("living-room-1");
    onlineDevices.add("bedroom-lamp");
    System.out.println("Is 'living-room-1' online? " + onlineDevices.contains("living-room-1"));
    System.out.println("Is 'kitchen-toaster' online? " + onlineDevices.contains("kitchen-toaster"));
    onlineDevices.remove("living-room-1");
    System.out.println("After removing: " + onlineDevices);
    System.out.println();

    System.out.println("=== Tips for Interactive Testing ===");
    System.out.println("1. Print your collections to see what they contain");
    System.out.println("2. Try adding/removing elements and print again");
    System.out.println("3. Test edge cases (empty collections, duplicate keys, null values)");
    System.out.println("4. Once you understand the behavior, write proper tests!");
  }
}
