# ADR-004: Context-Sensitive Tab Completion Design

## Context
The CLI needs tab completion that varies by context: the right completions for `show` are recipe titles, for `recipes` they are collection names, for `convert` they start as recipe titles then switch to unit names, and in cook mode the entire command set changes to `next`, `prev`, `ingredients`, and `quit`.

A single static list of completions would not support this. A context-unaware completer would either suggest irrelevant options or fail to surface the right ones for multi-argument commands like `convert` or `import json`.

From L18, the most relevant heuristics were **interface segregation** and the **actor heuristic**. Each completion source should provide only what its context requires, and Cook-mode input behavior should be separated from normal CLI input behavior because the Cook actor has a fundamentally different interaction mode.

## Decision
Implemented `CybCompleter`, a JLine `Completer` that inspects the current raw input line on every tab press and routes to the appropriate completion source:

- `completeRecipes(...)` — sources titles from `library.getRecipeRepository().findAll()`, used for show, scale, delete, cook, shopping-list, and the first argument of convert and export.
- `completeCollections(...)` — sources titles from `library.getCollectionRepository().findAll()`, used for recipes and the second argument of import json.
- `completeUnits(...)` — sources values from `Unit.getParseableNames()`, used as the second argument of convert.
- `completeCookMode(...)` — returns a static list of cook-mode commands (next, prev, ingredients, quit), used whenever `CookModeHolder.isInCookMode()` is true.

`CookModeHolder` is a shared mutable flag that `runCookMode(...)` sets on entry and exit. The completer checks it first, before any command-prefix routing, so cook-mode completions fully replace normal completions while a session is active.

`QuotedTokenizer` is used throughout to correctly parse and complete multi-word recipe and collection titles that appear in quoted strings.

Did not build a separate completer per command. The number and diversity of commands did not justify that level of abstraction, and keeping the routing logic in one class makes context transitions easier to read.

## Consequences
Positive:
- Follows **interface segregation**: each private completion method sources only what its command context needs and nothing else.
- Applies the **actor heuristic**: Cook-mode completion is fully isolated from normal CLI completion through `CookModeHolder`, matching the Cook actor's distinct interaction model.
- Repository-backed suggestions stay synchronized with stored data without any manual refresh.
- Quoted tokenization handles multi-word titles transparently for both parsing and completion.

Negative:
- The `complete(...)` method contains multiple prefix-based conditional branches that must be kept in sync with actual command grammar.
- Handling partial quoted input (e.g. a user mid-way through typing a quoted title) adds tokenization edge cases.
- The completer holds a reference to live repository data, which couples it to the persistence layer.
