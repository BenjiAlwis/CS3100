# Reflection (A5: Interactive CLI)

Complete all 6 questions below. Reflection questions are worth 20 points total (see grading rubric).

---

1. **Applying Boundary Heuristics:** Which of the four L18 heuristics (rate of change, actor,
   interface segregation, testability) most influenced your service layer design? Give a concrete
   example: describe a specific boundary you drew (or chose not to draw) and explain which
   heuristic(s) informed that decision. How did the three actors (Librarian, Cook, Planner)
   influence your thinking? If multiple heuristics pointed in different directions, how did you
   resolve the tension?

Answer:
The actor heuristic influenced my design the most. I used the three assignment actors as the primary boundary signal: Librarian (manage stored collections and recipes), Cook (run an interactive cooking session), and Planner (transform and combine recipe data). This gave me a practical way to separate responsibilities before thinking about implementation details.

A boundary I drew was between Librarian workflows and Cook workflows. Librarian commands such as creating collections, importing, and deleting are repository facing and persistence oriented. Cook mode is session oriented: it manages temporary state like current step index and user navigation commands. I kept cook state and navigation logic out of repository operations because they serve a different actor and a different interaction style.

Rate of change reinforced that same boundary. Cook mode is likely to evolve through UX changes (new navigation commands, timers, annotations), while repository workflows change when data management rules change. Keeping them separate lowers the chance that a cook-mode change breaks storage logic, and vice versa.

The main tension came from Planner features (scale and convert). Actor thinking suggests these belong to Planner, but interface segregation and testability suggest transformation logic should stay near the recipe domain model so it can be reused and tested without persistence. So this is also a boundary I chose not to draw: I did not create a separate planner-only transformation service.

I resolved that tension by splitting the workflow into two phases: transform first, persist second. The CLI computes and previews a transformed recipe, then saves only if the user confirms. This keeps Planner behavior clear at the command level while preserving reusable transformation logic and clean separation from repository concerns.


2. **ADR Writing Experience:** Reflect on writing your ADRs. Did documenting your decisions change
   how you thought about them? Was there a moment where writing the "Consequences" section made you
   reconsider a choice? How useful do you think ADRs would be on a team project vs. a solo
   assignment?

Answer:
Writing the ADRs changed how I thought about my design because it forced me to move from "this works" to "this is the boundary and here is why." In particular, documenting ADR-001 made me clarify that actor boundaries (Librarian, Cook, Planner) were my top-level organizing choice, not just a naming convention for CLI handlers.

The Consequences section was where I reconsidered a decision most directly. My first instinct for scale and convert was to keep all logic inside Planner-facing command flow. While writing consequences, I realized that would over-couple transformation behavior to one actor and hurt reuse and testability. That pushed me to a more explicit split: transformation and preview first, optional persistence second. The cost is extra orchestration in command code, but the benefit is cleaner reuse and easier testing.

On a solo assignment, ADRs were useful mainly as a reasoning tool and a way to verify I had not ignored tradeoffs. On a team project, they are much more valuable: they preserve decision history, reduce repeated design debates, and help new contributors understand constraints before changing code. In practice, that shared context would prevent accidental reversals of decisions like actor-based boundaries or preview-before-save behavior.


3. **Transformation vs. Persistence:** A4's `RecipeService.scaleRecipe()` always saved. Your design
   needed to support "preview before save." Describe concretely how your service layer handles this
   differently. What methods exist? How does the CLI compose them? What would break if you tried to
   bolt this capability onto A4's interface?

Answer:

In A4, `RecipeService.scaleRecipe()` combined transformation and persistence in a single call and always saved the result. That interface left no room for an intermediate state where the transformed recipe exists in memory but has not yet been saved.

In this design, transformation and persistence are two separate steps with explicit methods for each:

- `recipe.scale(factor)` and `recipe.convert(targetUnit, registry)` on the `Recipe` domain object perform the transformation and return a new `Recipe` in memory with no side effects.
- `formatScaleComparison(...)` and `formatConvertComparison(...)` render a side-by-side preview for the user.
- `saveDerivedRecipe(original, derived)` handles persistence by finding every collection containing the original recipe and adding the derived one.

The CLI composes these in `handleScale(...)` and `handleConvert(...)` as three sequential steps: transform first, then render the preview, then prompt for confirmation before calling `saveDerivedRecipe(...)`.

If we tried to bolt preview-before-save onto A4's interface, there are two concrete problems. First, because `scaleRecipe()` persists immediately, any call to it would write to the repository before the user could see or approve the result. The only way to recover would be to delete the just-saved recipe on a user decline, which is fragile. Second, because A4's method owns both concerns, there is no clean point to inject the preview step without restructuring the method itself. The preview requires the transformed object to be available before any save decision is made, which is only possible if transformation and persistence are separated from the start.


4. **Cook Mode State Management:** Interactive cooking mode tracks state (current step, original
   recipe). Where does this state live in your architecture — in the CLI controller, in a service,
   in a dedicated session object? What tradeoffs did you consider? The Cook actor has different
   needs than the Librarian — how did this influence where you placed cook mode state? Could the
   same state management approach work for a future "meal planning session" for the Planner actor?

Answer:

Cook mode state is split across two locations, each responsible for a different concern.

The primary session state — the current step `index` and the active `Recipe` — lives as local variables inside `runCookMode(Recipe recipe, CookModeHolder holder)`. This state is created fresh each time a cook session starts and is discarded automatically when the session ends, either by finishing all steps or by the user typing `quit`. It never gets stored in a field, a service, or the heap beyond the call stack of that one method.

The second piece of state is `CookModeHolder`, a small shared object that holds a single `boolean inCookMode`. Its only job is to let `CybCompleter` know whether to suggest cook-mode commands (`next`, `prev`, `ingredients`, `quit`) instead of normal CLI commands. `runCookMode(...)` sets it to `true` on entry and clears it in a `finally` block on exit, so it stays consistent even if the session ends unexpectedly.

I considered putting session state into a dedicated service or session object. The tradeoff is that a service would make the state more visible and testable in isolation, but it would also require injecting it through the context and managing lifecycle explicitly. Because cook-mode state is inherently scoped to a single `runCookMode(...)` call with no need to survive beyond it, local variables are simpler and carry less risk of stale state across sessions.

The Cook actor's needs directly shaped this placement. Librarian commands are stateless and independent: each call to `handleImport(...)` or `handleDelete(...)` is self-contained. Cook mode is a session: commands like `next` and `prev` only make sense relative to a step index, and that index has no meaning outside the cooking session. Treating it as local state in the loop made it obvious that the index is scoped to the session and cannot accidentally leak into unrelated workflows.

The same approach could work for a future meal planning session for the Planner, but only if that session's state is also short-lived and scoped to a single interactive loop. If the Planner session needed to survive across multiple commands or be resumed later, local variables would not be sufficient and a more explicit session object with managed lifecycle would be the better choice.


5. **E2E Testing Experience:** This assignment used E2E tests with a dumb terminal instead of mocks.
   Compare this to A4's mock-based approach. Which bugs did E2E testing catch (or would catch) that
   mocks might miss? Were there situations where you wished you had finer-grained unit tests? What's
   your takeaway about when to use each approach?

Answer:

The E2E tests here use a JLine dumb terminal via `CliTestBase`, which pipes real string input through to the CLI and captures the full output. Every test sends one or more commands through `sendCommands(...)`, runs the CLI, then asserts on the raw output string. This is different from A4's mock approach, where tests verified method calls on injected service doubles rather than observing real output.

The main class of bugs E2E tests catch that mocks typically cannot is integration-path bugs: issues that only appear when multiple layers compose correctly. A concrete example is cook-mode navigation. A mock-based test could verify that `runCookMode(...)` was called, but it would not catch a bug where `printCookStep(...)` displays the wrong step number, or where `next` at the last step fails to print the "Finished cooking" message. `CookModeControllerTest` catches both of those by asserting on strings like `"Step 2 of 4"` and `"Finished cooking"` in the actual output. Similarly, `FormatTests` tests like `scale_longIngredientName_truncatesInComparisonTable` catch formatting regressions that would be invisible to a mock verifying only that `recipe.scale(...)` was called.

There were situations where fine-grained unit tests would have been faster to debug. When a formatting test failed, the E2E output contained the full CLI session, which meant scanning a large string to find the specific line that was wrong. A targeted unit test directly on `formatScaleComparison(...)` or `formatConvertComparison(...)` would have isolated the failure immediately. `FormatTests` partly fills this role by running specific scenarios through the full CLI, but a pure unit test on the formatting method would be faster to diagnose.

My takeaway is that E2E tests are the right default for a CLI because the user-facing output is the contract. Mocks verify internal structure, which can pass even when the actual output is wrong. Unit tests are worth adding when a specific piece of logic (formatting, transformation math, tokenization) is complex enough that debugging through a full CLI run is slow. The two approaches are most effective used together: E2E tests as the primary coverage layer, targeted unit tests for components where failure modes are not obvious from output alone.


6. **AI Collaboration:** Which parts of the CLI did AI help you build most effectively? Where did
   you need to think independently about design? Did AI help or hinder your architectural thinking
   — for example, did it suggest designs that violated the boundary heuristics, or did it help you
   apply them?

Answer:

AI was most helpful for generating initial scaffolding for the CLI commands and for helping debug some of the issues that appeared in the tests. It was especially useful for suggesting improvements to formatting logic, handling edge cases, and understanding error messages from the build and test output.
However, the overall architecture decisions required more independent thinking. For example, deciding how to separate transformation from persistence or where to store cook mode state required understanding the assignment requirements and the L18 boundary heuristics.
There were also cases where AI suggested designs that were too centralized or mixed responsibilities in ways that would violate the boundary heuristics. In those situations I had to adjust the suggestions so that the design better aligned with the actor-based boundaries and separation of concerns discussed in the lectures. Overall AI was helpful for implementation details, but the architectural structure still required my own reasoning.
