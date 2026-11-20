package net.sceneitall.iot.lab2.part3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.sceneitall.iot.DeviceGenerator;
import net.sceneitall.iot.IoTDevice;

/**
 * Part 3: Performance Showdown Exercises.
 *
 * <p>This class contains exercises for comparing the performance of different collection types.
 * Complete the TODO sections in each method.
 */
public final class PerformanceExercises {

  private PerformanceExercises() {
    // Prevent instantiation - this is a utility class
  }

  // ============================================================
  // Exercise 3.1: Collection Builders
  // ============================================================

  /**
   * Exercise 3.1: Build an ArrayList.
   *
   * <p><b>TODO:</b> Add all devices to the ArrayList.
   *
   * @param devices the devices to add
   * @return an ArrayList containing all devices
   */
  public static ArrayList<IoTDevice> buildArrayList(List<IoTDevice> devices) {
    ArrayList<IoTDevice> list = new ArrayList<>();
    // TODO: Add all devices to the ArrayList

    list.addAll(devices);
    return list;
  }

  /**
   * Exercise 3.1: Build a LinkedList.
   *
   * <p><b>TODO:</b> Add all devices to the LinkedList.
   *
   * @param devices the devices to add
   * @return a LinkedList containing all devices
   */
  @SuppressWarnings("JdkObsolete") // LinkedList used intentionally for educational comparison
  public static LinkedList<IoTDevice> buildLinkedList(List<IoTDevice> devices) {
    LinkedList<IoTDevice> list = new LinkedList<>();
    // TODO: Add all devices to the LinkedList

    list.addAll(devices);
    return list;
  }

  /**
   * Exercise 3.1: Build a HashMap.
   *
   * <p><b>TODO:</b> Add all devices, using device ID as the key.
   *
   * @param devices the devices to add
   * @return a HashMap with device IDs as keys and devices as values
   */
  public static HashMap<String, IoTDevice> buildHashMap(List<IoTDevice> devices) {
    HashMap<String, IoTDevice> map = new HashMap<>();
    // TODO: Add all devices, using device ID as the key

    for (IoTDevice device : devices) {
      map.put(device.getId(), device);
    }
    return map;
  }

  // ============================================================
  // Exercise 3.2: Lookup Methods
  // ============================================================

  /**
   * Exercise 3.2: Find a device in an ArrayList.
   *
   * <p><b>TODO:</b> Loop through the list, check each device's ID. Return the device if found, null
   * otherwise.
   *
   * @param devices the ArrayList to search
   * @param targetId the ID to find
   * @return the device if found, null otherwise
   */
  public static IoTDevice findInArrayList(ArrayList<IoTDevice> devices, String targetId) {
    // TODO: Loop through the list, check each device's ID
    // Return the device if found, null otherwise

    for (IoTDevice device : devices) {
      if (device.getId().equals(targetId)) {
        return device;
      }
    }
    return null;
  }

  /**
   * Exercise 3.2: Find a device in a LinkedList.
   *
   * <p><b>TODO:</b> Loop through the list, check each device's ID. Return the device if found, null
   * otherwise.
   *
   * @param devices the LinkedList to search
   * @param targetId the ID to find
   * @return the device if found, null otherwise
   */
  @SuppressWarnings("JdkObsolete") // LinkedList used intentionally for educational comparison
  public static IoTDevice findInLinkedList(LinkedList<IoTDevice> devices, String targetId) {
    // TODO: Loop through the list, check each device's ID
    // Return the device if found, null otherwise

    for (IoTDevice device : devices) {
      if (device.getId().equals(targetId)) {
        return device;
      }
    }
    return null;
  }

  /**
   * Exercise 3.2: Find a device in a HashMap.
   *
   * <p><b>TODO:</b> Look up the device directly by ID.
   *
   * @param deviceMap the HashMap to search
   * @param targetId the ID to find
   * @return the device if found, null otherwise
   */
  public static IoTDevice findInHashMap(HashMap<String, IoTDevice> deviceMap, String targetId) {
    // TODO: Look up the device directly by ID

    return deviceMap.get(targetId);
  }

  // ============================================================
  // Exercise 3.3: Performance Test Runner
  // ============================================================

  /**
   * Exercise 3.3: Run the Performance Comparison.
   *
   * <p>This method generates devices, builds each collection type, and measures lookup times.
   *
   * <p>Run with: ./gradlew runPart3
   */
  @SuppressWarnings("JdkObsolete") // LinkedList used intentionally for educational comparison
  public static void runPerformanceComparison() {
    final int deviceCount = 10_000;
    final int lookupCount = 1_000;

    System.out.println("=== Lab 2: Performance Comparison ===");
    System.out.println("Generating " + deviceCount + " devices...");

    List<IoTDevice> devices = DeviceGenerator.generateMixedDevices(deviceCount);
    List<String> lookupIds = DeviceGenerator.generateLookupIds(deviceCount, lookupCount);

    System.out.println("\n--- Build Times ---");

    // Build ArrayList
    long startTime = System.nanoTime();
    ArrayList<IoTDevice> arrayList = buildArrayList(devices);
    long arrayListBuildTime = System.nanoTime() - startTime;
    System.out.printf(
        "ArrayList build:  %,d ns (%.2f ms)%n",
        arrayListBuildTime, arrayListBuildTime / 1_000_000.0);

    // Build LinkedList
    startTime = System.nanoTime();
    LinkedList<IoTDevice> linkedList = buildLinkedList(devices);
    long linkedListBuildTime = System.nanoTime() - startTime;
    System.out.printf(
        "LinkedList build: %,d ns (%.2f ms)%n",
        linkedListBuildTime, linkedListBuildTime / 1_000_000.0);

    // Build HashMap
    startTime = System.nanoTime();
    HashMap<String, IoTDevice> hashMap = buildHashMap(devices);
    long hashMapBuildTime = System.nanoTime() - startTime;
    System.out.printf(
        "HashMap build:    %,d ns (%.2f ms)%n", hashMapBuildTime, hashMapBuildTime / 1_000_000.0);

    System.out.println("\n--- Lookup Times (" + lookupCount + " lookups) ---");

    // ArrayList lookup
    startTime = System.nanoTime();
    for (String id : lookupIds) {
      findInArrayList(arrayList, id);
    }
    long arrayListLookupTime = System.nanoTime() - startTime;
    System.out.printf(
        "ArrayList lookup:  %,d ns (%.2f ms)%n",
        arrayListLookupTime, arrayListLookupTime / 1_000_000.0);

    // LinkedList lookup
    startTime = System.nanoTime();
    for (String id : lookupIds) {
      findInLinkedList(linkedList, id);
    }
    long linkedListLookupTime = System.nanoTime() - startTime;
    System.out.printf(
        "LinkedList lookup: %,d ns (%.2f ms)%n",
        linkedListLookupTime, linkedListLookupTime / 1_000_000.0);

    // HashMap lookup
    startTime = System.nanoTime();
    for (String id : lookupIds) {
      findInHashMap(hashMap, id);
    }
    long hashMapLookupTime = System.nanoTime() - startTime;
    System.out.printf(
        "HashMap lookup:    %,d ns (%.2f ms)%n",
        hashMapLookupTime, hashMapLookupTime / 1_000_000.0);

    System.out.println("\n--- Summary ---");
    System.out.println("Collection     | Build Time | Lookup Time (" + lookupCount + " ops)");
    System.out.println("---------------|------------|------------------");
    System.out.printf(
        "ArrayList      | %7.2f ms | %7.2f ms%n",
        arrayListBuildTime / 1_000_000.0, arrayListLookupTime / 1_000_000.0);
    System.out.printf(
        "LinkedList     | %7.2f ms | %7.2f ms%n",
        linkedListBuildTime / 1_000_000.0, linkedListLookupTime / 1_000_000.0);
    System.out.printf(
        "HashMap        | %7.2f ms | %7.2f ms%n",
        hashMapBuildTime / 1_000_000.0, hashMapLookupTime / 1_000_000.0);
  }

  /**
   * Main method for running the performance comparison.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    runPerformanceComparison();
  }
}
