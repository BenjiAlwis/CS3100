# Lab 9 Reflection: Network Fault Tolerance

**Partner's Name:** Rhyam

---

1. **The Command Pattern**

   The CLI dispatch loop in `SceneItAllCLI.java` uses the Command pattern. An alternative
   implementation would use a chain of `if/else` statements:

   ```java
   // Alternative: if/else chain
   if (parts[0].equals("list")) {
       // ... list logic inline ...
   } else if (parts[0].equals("on")) {
       // ... on logic inline ...
   } else if (parts[0].equals("status")) {
       // ... your new status logic inline ...
   }
   ```

   Compare the two approaches:

   - What would it take to add a new command in each approach? How many files would you need to touch?
   - What would it take to _test_ a single command in isolation in each approach?
   - Which coupling type from
     [L7](https://neu-pdi.github.io/cs3100-public-resources/lecture-notes/l7-design-for-change) does
     the if/else chain exhibit? What about the Command pattern?
   - Is there any situation where you'd prefer the if/else approach?

My Response:
   The Command pattern in SceneItAllCLI is easier to extend and test than an if/else chain.
   With the Command pattern, adding a new command mostly means creating one new command class (for example, StatusCommand) and registering it in the CLI command map. I would have to edit the central dispatch method every time, and often add more logic there, which increases risk of breaking unrelated commands.

   Testing is also cleaner with Command objects. I can instantiate one command class, mock/stub its dependencies (DeviceGateway, DeviceRegistry, Terminal), and test only that command in isolation. In an if/else design, command logic is mixed into one big method, so tests are more integration-heavy and harder to isolate.

   The if/else chain has tighter coupling (especially control coupling around centralized branching and shared dispatch internals), while the Command pattern has looser coupling through a stable interface (Command) and dependency injection.

   I might still prefer if/else with a very small throwaway script with 2-3 commands where long-term extensibility and isolated testing are not priorities. For this lab’s requirements, Command pattern is better.

2. **Timeout and Retry Design**

   Describe your final implementation of timeout and retry:

   - What timeout value did you choose, and why?
   - How many retry attempts, and why?
   - What is the maximum time a user might wait before getting an answer? Is that acceptable?
   - Did you treat `readState` and `sendCommand` differently? Why or why not?

My Response:
   I chose a 5-second timeout per network attempt. In a home IoT network, healthy devices should usually respond under that, so 5 seconds is a good balance between not failing too aggressively and not hanging too long.

   I used different retry policies by operation type:

      sendCommand: 1 attempt (no retry)
      readState: 2 attempts with exponential backoff
      I treated them differently because readState is safe to retry, but sendCommand can have side effects. If a command actually executed on the device and only the response was lost, retrying could duplicate the action.

   For readState, the maximum wait is about:

      attempt 1 timeout: 5s
      backoff before attempt 2: 2s
      attempt 2 timeout: 5s
      Total worst case: ~12 seconds.

   For sendCommand, worst case is ~5 seconds (single timeout).
   I think this is acceptable for a homeowner CLI because it avoids indefinite hangs and gives predictable failure behavior while still recovering from some transient read failures.

3. **Adapting to Partial Failures**

   When the requirements changed to include partial failures:

   - What assumption did your original retry logic make that turned out to be wrong?
   - What did you change in your implementation?
   - Did you modify your existing code, build on top of it, or start over? What drove that decision?

My Response :
   My original retry logic assumed failures were mostly temporary and that all operations should be handled the same way. That broke down with partial failures, where some devices fail while others still work. I changed the design so failures are handled per device and the app degrades gracefully instead of failing everything. I also treated operations differently: readState can retry, but sendCommand is more careful to avoid duplicate side effects. I built on top of my existing timeout/retry code rather than starting over, since the core structure was still useful.

4. **User Communication**

   In your `status` command:

   - What output format did you choose? Paste a sample output.
   - What information did you include? What did you leave out?
   - How did you decide what a homeowner user actually needs to see vs. what belongs in a log file?

   Sample Output:

   <=<<<<<=====DEVICE             [TYPE        ] STATUS      STATE
   ----------------------------------------------------------------
   living-room-thermostat [thermostat  ] REACHABLE    - on
   front-door-lock    [lock        ] UNREACHABLE
   thermostat-main    [thermostat  ] REACHABLE    - unknown
   living-room-light  [dimmable    ] REACHABLE    - on
   porch-camera       [camera      ] UNREACHABLE

My Response :
   I included the information a homeowner can act on immediately: device name, device type, whether it is REACHABLE or UNREACHABLE, and current state when available (for example on, off, 60%, or state unknown). This lets the user quickly see which device is the problem without reading technical details.

   I left out low-level debugging data such as stack traces, exception class names, retry/backoff timing internals, and raw network error details. That information is useful for developers, but it would be noisy and confusing in a homeowner-facing CLI.

   I decided what to show by asking: “Can this help the user make a next step right now?” If yes, it belongs in status output. If it is mainly diagnostic, it belongs in logs.

5. **Meta (pick one)**

   - _Connections:_ How does one pattern you implemented (timeout, retry, exponential backoff, graceful
     degradation) connect to a specific concept from
     [L20](https://neu-pdi.github.io/cs3100-public-resources/lecture-notes/l20-networks)? Be
     specific—quote or reference the lecture material.
   - _Adaptability:_ The requirements change was intentional. How did it feel when we announced it?
     What made it easier or harder to adapt? What would you do differently next time requirements
     change on you?
   - _Real-world parallel:_ Describe a situation outside of programming where you had to adapt when
     something didn't go as planned. What's similar to what you did today?

My Response :
     One clear connection is timeouts + retry with exponential backoff, which maps directly to L20’s point that distributed systems are unreliable and clients should “fail fast, then retry with increasing delays” instead of waiting forever or retrying aggressively. In my implementation, each request has a 5s timeout, readState retries with backoff (2s, then another attempt), and failures eventually return a clear error. This addresses the L20/fallacies idea that “the network is reliable” is false, while backoff also avoids creating extra load during transient outages.
