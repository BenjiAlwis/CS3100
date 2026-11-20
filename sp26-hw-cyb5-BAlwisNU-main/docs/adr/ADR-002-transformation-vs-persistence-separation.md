# ADR-002: Separate Transformation from Persistence

## Context
The CLI requires a preview-before-save workflow for commands such as scale and convert. That requirement introduces two separate concerns:

- transformation: compute a derived recipe from an original recipe
- persistence: decide whether the derived recipe should be saved

In A4, `RecipeService.scaleRecipe()` combined both concerns and always persisted. That interface shape does not support an interactive confirmation step.

From L18, the main heuristics were **rate of change** and **testability**. Transformation rules (math, units, conversion behavior) and persistence rules (where/how to save) change for different reasons, so combining them increases coupling.

## Decision
Separated transformation from persistence as an explicit two-phase workflow:

1. Compute a transformed `Recipe` in memory.
2. Render a preview comparing original and transformed output.
3. Persist only on explicit user confirmation.

Transformation behavior remains domain-level and side-effect free. Persistence remains repository-level and is invoked only after confirmation.

Did not expose a single "transform-and-save" service operation for scale/convert in this assignment, because that would collapse the preview boundary and reintroduce A4-style coupling.

## Consequences
Positive:
- Follows the **rate-of-change heuristic** by allowing transformation and persistence to evolve independently.
- Improves **testability**: transformation behavior can be tested without repository setup, and persistence can be tested separately.
- Makes preview-before-save a first-class workflow rather than an afterthought.
- Prevents accidental writes when users decline a preview.

Negative:
- Command handlers must orchestrate multiple steps (transform, preview, confirm, optional save).
- Temporary derived recipes may be allocated and discarded when users choose not to save.
- Some orchestration logic can repeat across commands unless extracted into shared helpers.