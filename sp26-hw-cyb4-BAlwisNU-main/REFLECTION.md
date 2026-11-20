# Reflection (A4: RecipeService and Testing)

Complete all 6 questions below. Each question is worth 4 points (24 points total).

---

1. **Parsing Design:** How did you structure your parsing logic? Did you create separate classes
   (e.g., `IngredientParser`, `RecipeTextParser`) or keep it inline? What tradeoffs did you
   consider? If you were explaining your design choice to a skeptical teammate who preferred a
   different approach, what arguments would you use to advocate for your decision?

Answer:

I split parsing into two classes. `RecipeTextParser` handles the big-picture recipe structure
(title, sections, servings, instruction numbering), while `IngredientParser` handles the line-level
ingredient formats (`a/an`, fractions, ranges, units, `of`, vague ingredients, and comma-separated
preparation). `RecipeServiceImpl` then stays focused on orchestration (lookups, saves, updates).

The tradeoff is that this adds a little more structure and indirection, but for me it paid off in
clarity and debugging speed. If I had kept all parsing inline in the service, every bug fix would
have risked breaking persistence flow too. I would defend this design to a teammate by pointing out
that parser changes are now isolated: I can change one parsing rule without touching repository
logic or logging behavior.

---

2. **What Are Your Tests Actually Testing?** Look at your `RecipeServiceTest` suite. Are your tests
   primarily verifying _coordination_ (the service calls the right methods in the right order) or
   _computation_ (the service produces correct results)? Which type of bug would your tests catch?
   Which might they miss? Is that the right balance for a service layer?

Answer:

My tests do both, but they intentionally lean toward coordination because this assignment is about a
service facade. I verify repository interactions, update order, and exception precedence, while also
checking key computation outcomes (parsing correctness, scaling, conversion behavior, and shopping
list aggregation rules).

So these tests are strong at catching “service glue” bugs: wrong method calls, missing saves,
bad ordering, and many parser mistakes. They are weaker at corner cases outside the written spec,
which I accepted on purpose to avoid overfitting. For this layer, I think that’s the right balance:
high confidence in contract behavior with enough computation checks to catch meaningful bugs.

---

3. **Implementing a Non-Ideal Interface:** The `RecipeService` facade bundles multiple
   responsibilities into single methods. How did you keep your _internal_ implementation clean
   despite this external constraint? What would you change about the interface if you could redesign
   it?

Answer:

I treated `RecipeService` as an external constraint and kept internals clean anyway. In practice,
`RecipeServiceImpl` now handles validation + orchestration, while parsing logic lives in parser
classes and domain transformations stay immutable.

If I could redesign the interface, I would separate concerns more explicitly: parsing, pure
transformations, and persistence side effects would be distinct operations. I would especially separate
“compute result” from “save result,” because bundling those together makes methods convenient for
CLI callers but harder to reason about and test.

---

4. **Mocks, Fakes, and Untestable Designs:** This assignment required mock-based testing for
   repositories, but `importFromJson` forced you to use real temp files because file I/O isn't
   behind a mockable interface. Compare these two testing approaches you used: (a) mocking
   `RecipeRepository` for methods like `scaleRecipe`, and (b) creating temp files for
   `importFromJson`. What bugs does each approach catch? What bugs might each miss? If you could
   redesign the `importFromJson` method signature to make it more testable, what would you change?
   What interface or abstraction would you introduce so that file reading could be mocked?

Answer:

Mock-based tests (like for `scaleRecipe` and `generateShoppingList`) were great for quickly
validating service behavior in isolation. They catch bad coordination and exception-precedence bugs
very well, but they won’t catch real file/JSON integration problems.

Temp-file tests for `importFromJson` gave me that realism: they catch malformed files,
deserialization issues, and real I/O failures. The downside is they are slower and less isolated.

If I redesigned this, I would inject a `RecipeLoader`/`RecipeReader` interface (for example,
`Recipe load(Path path) throws IOException`) so `RecipeServiceImpl` depends on a mockable port. The
default implementation can still use Jackson + file I/O, but tests could choose pure mocks or
integration-style temp-file tests depending on the goal.

---

5. **What the Struggle Taught You:** Describe a moment where you were stuck on this assignment. What
   was confusing? How did you get unstuck (office hours, debugging, stepping away, etc.)? What did
   this experience reveal about how you work best — do you prefer to push through, step away, seek
   help early, or something else? How might you approach a similar situation differently next time?

Answer:

The hardest part for me was parsing edge cases that looked similar but needed different behavior,
especially around title handling and `a/an` ingredient parsing. I ran into a subtle bug where
repeated title text could be skipped incorrectly.

What helped was slowing down and writing one focused test per rule, then fixing only that rule.
That process reminded me I am more effective when I break problems into tiny verifiable pieces,
instead of trying to “fix everything” in one pass. Next time I would write edge-case tests earlier,
not after the happy path already works.

---

6. **AI Collaboration:** Which tasks benefited most from AI assistance (e.g., boilerplate, parsing
   logic, test generation, debugging)? Where did you need to think independently? Did the AI teach
   you anything new — a technique, pattern, or concept you hadn't seen before? What's one thing you
   learned about working effectively with AI on this assignment?

Answer:

AI was most helpful for repetitive work and fast feedback loops: boilerplate Mockito setup,
suggesting targeted tests, spotting redundant tests, and converting tool output (checkstyle/errors)
into focused fixes.

I still had to do the core reasoning myself: interpreting the spec boundaries, deciding exception
precedence, and choosing which tests are safe for mutation grading. My biggest takeaway is that AI
works best when I provide exact contract examples and expected outcomes. Vague prompts gave vague
results; concrete prompts gave usable code and better debugging help.
