# ADR-001: Service Boundaries Based on Librarian, Cook, and Planner

## Context
The assignment gives three actors with different goals: Librarian, Cook, and Planner.

- Librarian workflows manage stored data (collections and recipes).
- Cook workflows manage an interactive session (step navigation while cooking).
- Planner workflows transform and combine recipe data (scale, convert, shopping list).

The most relevant L18 heuristics were the **actor heuristic** and **rate of change**. These workflows are likely to evolve for different reasons, so combining them in one boundary would increase coupling.

## Decision
Decomposed service responsibilities by actor-facing workflow:

- Librarian boundary: collection and recipe persistence operations.
- Cook boundary: interactive cook-session behavior and step navigation.
- Planner boundary: transformation and aggregation workflows over recipes.

Also made one explicit non-boundary decision: Did **not** create a Planner-only transformation service. Instead, scaling and conversion remain shared domain capabilities that Planner commands orchestrate.

This resolves tension between heuristics:

- The **actor heuristic** suggests Planner ownership of scale/convert workflows.
- **Interface segregation** and **testability** suggest transformation logic should stay reusable and not be coupled to persistence or one actor.

So the workflow is split into phases: transform/preview first, then optionally persist.


## Consequences
Positive:
- Follows the **actor heuristic** by aligning boundaries to Librarian, Cook, and Planner workflows.
- Supports **rate of change**, since session behavior (Cook) and persistence behavior (Librarian) can evolve independently.
- Improves **interface segregation**, because each boundary coordinates only the dependencies it needs.
- Improves **testability**, since interactive session logic can be validated separately from persistence and transformation behavior.

Negative:
- Some workflows still cross boundaries (for example, transformed recipes may later be persisted).
- Shared transformation capability requires discipline to avoid duplicated orchestration code.
- Boundary-driven decomposition increases wiring complexity in CLI coordination code.