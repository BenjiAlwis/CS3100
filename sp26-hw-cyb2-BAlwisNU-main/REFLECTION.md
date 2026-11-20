# Reflection

Your reflection should include 1-5 sentences for each of the questions below. Answers should be answered to the best of your ability, and reference your code submission. Up to 2 points will be deducted for each question that is omitted or answered incompletely.

## 1. API Design & Coupling

Why are transformation methods on `Recipe` rather than in a separate service? What type of coupling exists between `Recipe` and `ConversionRegistry`, and how does the design keep it loose?

I put transformation methods on Recipe because scaling/converting are core domain behaviors: a recipe knows how to produce a transformed version of itself, and the API stays discoverable (recipe.scale(...), recipe.convert(...)).
The relationship between Recipe and ConversionRegistry is mainly stamp coupling. Recipe methods like scale, convert, and scaleToTarget accept a ConversionRegistry object, which is a user-defined type rather than a primitive type. That means changes to the ConversionRegistry interface could require changes in Recipe. However, the design minimizes the risk of stamp coupling by depending only on the interface (ConversionRegistry) rather than the concrete class (LayeredConversionRegistry). Recipe never accesses internal fields or rule structures and it only calls the interface methods (convert, withRules), which prevents content coupling and common coupling, because no internal registry data or globals are shared.

## 2. Responsibility Assignment

Where did you put the logic for transforming ingredients? Did you add helper methods to domain classes? How did you decide what belongs where?

The ingredient-transformation logic lives primarily inside Recipe.scale, Recipe.scaleToTarget, and Recipe.convert, because those operations must rebuild the recipe’s ingredient list and instruction list consistently. I added small helper methods inside Recipe (scaleQuantity(...) and tryConvert(...)) to reduce duplication and isolate the “how” of scaling/conversion from the main loops. I avoided pushing transformation logic into Ingredient subclasses because the transformation needs context from the recipe.
The rule I used was: keep primitives/utility logic in private helpers, keep cross-object coordination in Recipe, and keep validation/representation in the individual domain types.


## 3. Information Hiding

What design decisions are hidden behind the `ConversionRegistry` interface? What could change in `LayeredConversionRegistry` without affecting code that uses the interface?

The ConversionRegistry interface hides how rules are stored, how priority is enforced, and how matching is performed (generic vs ingredient-specific, per-level ordering). Code using the interface only relies on behavior: “convert this quantity” and “return a new registry with extra rules,” not on whether rules are in lists or maps. In LayeredConversionRegistry, I could change from “copy everything forward” to a “chain of parent registries” model without affecting callers, as long as the same results are returned. I could also replace the internal lists with indexed structures (like maps keyed by (fromUnit,toUnit) and ingredient) to speed up lookup, and interface users wouldn’t change. Even rule iteration order or caching strategies could change internally, as long as the externally observed priority semantics remain correct.

## 4. Immutability

What are the benefits and costs of requiring immutable `Recipe` objects? How did immutability affect your transformation implementation?

Immutable Recipe objects make transformations safer because calling scale() or convert() can’t accidentally corrupt the original recipe, which reduces bugs and makes reasoning/testing easier. Immutability also helps with sharing: the same recipe can be reused across multiple transformations. The main cost is more allocations: each transformation creates new lists and new domain objects, which is extra memory/time compared to mutating in place. Immutability shaped my implementation by forcing a “rebuild” approach: construct new ingredient lists and instruction lists, and return a new Recipe rather than editing fields. It also pushed me to return unmodifiable lists to prevent external mutation.


## 5. Extensibility

Pick one future requirement (e.g., bulk conversion to metric, export with unit preferences, a `RecipeBook` class). How well does your design support it? What would need to change?

A future requirement like “export recipes with unit preferences (metric vs imperial)” fits well because the conversion behavior is already centralized behind ConversionRegistry and Recipe.convert(...). You could implement a preference-driven export by selecting a target unit per dimension (e.g., grams for weight, milliliters for volume) and applying convert(...) accordingly, possibly in an export layer rather than changing core domain classes. The main thing you would add is a mapping from unit-dimension -> preferred unit, plus some policy for ingredients that can’t be converted (e.g., keep original units or annotate). If you wanted “bulk conversion to metric” for everything, you might add a convenience method (either on Recipe or as a separate exporter/service) that calls convert(...) multiple times per ingredient based on dimension. The existing interface-based registry design means you can extend conversion rules (e.g., more density rules) without changing the transformation code, as long as the registry provides the required conversions.



## 6. AI Usage (Ungraded)

Did you use AI assistance? If so, describe how and reflect on whether it helped or hindered your learning of design principles.

I didn't use AI tools for this assignment.
