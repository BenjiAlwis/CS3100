# Lab 2 Reflection

Answer the following questions based on your experience completing this lab.

## 1. Polymorphism

In your `SmartHomeController`, why didn't you need to write any `instanceof` checks? What would happen if a new device type (like `SmartThermostat`) were added to the codebase?

**Your answer:**

I didn't need to use 'instance of" since he controller stores devices as the interface type IoTDevice and invokes behavior through that interface. Dynamic dispatch executes the right behavior at runtime based on the object’s actual class.

## 2. Collections

For one of the scenarios in Part 2, explain why your chosen collection type was the best fit.

**Your answer:**

Scenario A (Device Registry): HashMap<String, IoTDevice> was the best fit because the ymain operation is fast lookup by a unique device ID. A HashMap provides (key → value) mapping.


## 3. Performance

Record your timing results table from Exercise 3.3:

| Collection | Time to Build (10K devices) | Time for 1,000 Lookups |
|------------|-----------------------------|-----------------------|
| ArrayList  | 0.08 ms | 101.59 ms |
| LinkedList | 1.40 ms |  85.61 ms |
| HashMap    | 6.02 ms |   0.60 ms |

**Analysis questions:**

1. Which was fastest to **build**? Which was fastest to **look up**?

**Your answer:**
Fastest to build - ArrayList
Fastest to lookuo - HashMap


2. Why is HashMap lookup faster than ArrayList/LinkedList?

**Your answer:**
Because get(id) is average O(1), while list search is O(n).

3. For the build times: Were ArrayList and LinkedList similar? Why or why not?

**Your answer:**
No—based on my results they were not similar. ArrayList built much faster (0.08 ms) than LinkedList (1.40 ms). Building a LinkedList requires allocating a separate node object per element and wiring pointers, which adds allocation overhead

4. If you only needed to look up devices **once**, would a HashMap still be worth it?

**Your answer:**
A HashMap becomes worth it when you perform enough lookups to compensate the build cost.

## 4. Collaboration

Did you and your neighbor have different approaches in today's lab? How did you resolve disagreements? What did you learn from discussing with someone who thought differently?

**Your answer:**

There were some differences in our implementations. We discussed to resolve the differences.

## Optional Extensions (if completed)

### Performance Deep Dive

If you completed Stretch Goal 1, record your findings here:

**Your findings:**

### Design Patterns Analysis

If you completed Stretch Goal 2 (`CeilingFanWithLight`), answer:

- Why doesn't `CeilingFanWithLight` extend both `Light` and `Fan`?

**Your answer:**

- What's the advantage of the composition approach?

**Your answer:**

- What's a disadvantage?

**Your answer:**

- When would inheritance be a better choice than composition?

**Your answer:**

### Generic Methods

If you completed Stretch Goal 3, describe your implementation:

**Your implementation notes:**
