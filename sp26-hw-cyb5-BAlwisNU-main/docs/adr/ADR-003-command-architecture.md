# ADR-003: Command Dispatch Architecture for the CLI

## Context
The CLI needs to support a large and growing set of commands (show, search, import, delete, scale, convert, shopping-list, cook, and others). Each command has its own argument structure, validation rules, and workflow. Without a deliberate structure, all of that logic would accumulate inside the main REPL loop, making individual commands hard to read, test, or change in isolation.

From L18, the most relevant heuristics were **interface segregation** and **testability**. Each command should only depend on the services it actually uses, and each command's workflow should be testable independently.

## Decision
Implemented a central dispatch method, `handle(String line)`, that is the single entry point for all user input. Its responsibilities are narrowly scoped:

1. Tokenize the input line using `QuotedTokenizer.tokenize(line)` to support multi-word recipe and collection names.
2. Extract the first token as the command name.
3. Route to a dedicated handler method (`handleScale(...)`, `handleConvert(...)`, `handleCook(...)`, etc.) via a switch statement.
4. Catch and surface `RepositoryException` and general exceptions without crashing the loop.

Each handler method is solely responsible for its own command: argument validation, domain logic coordination, and output formatting. Shared logic (recipe lookup, formatting, `saveDerivedRecipe(...)`) is extracted into helper methods rather than duplicated across handlers.

Did not introduce a separate command object or command registry pattern. With the current command set, the switch-dispatch approach keeps wiring simple and keeps each command's logic directly visible without additional abstraction layers.

## Consequences
Positive:
- Supports **interface segregation**: each handler interacts only with the services its command needs (e.g. `handleCook(...)` does not touch repositories).
- Improves **testability**: tests can send string input to `handle(...)` directly and assert on output, making E2E command tests straightforward without needing to mock dispatch logic.
- The REPL loop stays minimal and focused on I/O, not command details.
- Adding a new command requires only a new handler method and a single case in the switch.

Negative:
- The CLI class accumulates knowledge of all commands, growing as new ones are added.
- Shared helpers (recipe resolution, formatting, `saveDerivedRecipe(...)`) must be coordinated across handlers without duplication.
- If the command set grows significantly, the switch statement and class size may justify a more formal command registry in the future.