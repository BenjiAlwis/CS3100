# Lab 13 Reflection

Replace the prompts below with your answers. Submit this file through your own Pawtograder lab repository (both partners submit individually).

## Section 1: Concurrency Bugs

For each bug (1–4), answer in three short lines:

### Bug 1 (vanishing device count)

- **Root cause (one sentence):**
- **Fix (one sentence):**
- **How many of the 50 iterations failed before your fix?**

### Bug 2 (ghost scene)

- **Root cause (one sentence):**
- **Fix (one sentence):**
- **How many of the 50 iterations failed before your fix?**

### Bug 3 (frozen hub / deadlock)

- **Root cause (one sentence):**
- **Fix (one sentence):**
- **How many of the 50 iterations failed before your fix?**

### Bug 4 (phantom update)

- **Root cause (one sentence):**
- **Fix (one sentence):**
- **How many of the 50 iterations failed before your fix?**

## Section 2: Locking Performance

Run `./gradlew benchmark` (or `./gradlew run`) and paste your numbers:

- **No lock:** \_\_\_ ms, errors: \_\_\_
- **Coarse lock:** \_\_\_ ms
- **Fine-grained:** \_\_\_ ms

Questions:

1. How much faster is no-locking vs coarse-locking? What is the cost of correctness?
2. How much faster is fine-grained vs coarse-grained? Why does locking per room help?
3. Would fine-grained locking help if all activations targeted the same room? Why or why not?
4. Before you ran the benchmark, which strategy did you think would be fastest? Were you right?

## Section 3: Priority Workers

- How did `PriorityBlockingQueue` change processing order compared to `LinkedBlockingQueue`?
- What happens when a HIGH-priority command arrives while workers are busy with LOW-priority commands?
- How does this connect to blast radius? Why should door-lock commands rank above brightness adjustments?

## Section 4: Collaboration

- What feedback did your partner give you during a role swap? Was it useful?
- When you disagreed about a fix, how did you resolve it? (If you did not disagree, describe a moment one of you changed the other’s mind.)
- What is one thing you noticed about how _you_ debug that you had not been aware of before today?

## Section 5: Looking Ahead

- Your GA1 project uses `BackgroundTaskRunner` for work off the JavaFX thread. If the app shows stale data or freezes, which bug pattern from this lab would you suspect first? Why?
