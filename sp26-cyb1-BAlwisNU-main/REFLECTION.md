# Reflection

Your reflection should include 1-5 sentences for each of the questions below. Answers should be answered to the best of your ability, and reference your code submission. Up to 2 points will be deducted for each question that is omitted or answered incompletely.

1. **Inheritance Design:** Why is it useful to have `MeasuredIngredient` and `VagueIngredient` both extend a common `Ingredient` class, rather than an interface? What advantages does this provide for future development? Similarly, why is the `Quantity` hierarchy beneficial?

My Answer:

Having both MeasuredIngredient and VagueIngredient extend a common Ingredient class allowed shared state and behavior (for example name, preparation, and notes fields and their getters) to be implemented only once and shared with the sub classes (MeasuredIngredient and VagueIngredient) avoiding duplication inside the sub classes. An interface cannot hold common data or enforce the conditions such as names should be non-blank and trimmed in the constructor. On the other hand, abstract class allows it which makes sense for Ingredient to be an abstract class and MeasuredIngredient and VagueIngredient to extend it.

In the same way, the Quantity hierarchy allows ExactQuantity, FractionalQuantity, and RangeQuantity to share the unit field and getUnit() logic while varying only how numeric values and strings are computed.


2. **Composition vs Inheritance:** `MeasuredIngredient` uses composition (has-a relationship) with `Quantity`, while `ExactQuantity` uses inheritance (is-a relationship) with `Quantity`. Explain when composition is preferred over inheritance and vice versa, using examples from this assignment.

My Answer:

In this project, ExactQuantity has an inheritance relationship with Quantity because it is a kind of Quantity that shares the same conceptual role. In contrast, MeasuredIngredient uses composition because it is not a kind of Quantity; instead, it has a Quantity that describes how much of the ingredient is needed. Composition is more suitable here because the ingredient and its quantity are separate concepts that can vary independently. Using inheritance between MeasuredIngredient and Quantity would wrongly suggest that an ingredient is a quantity. However composition correctly models their real-world relationship and keeps the design flexible for future extensions.


3. **Immutability:** Did you make your classes immutable (i.e., their state cannot change after construction)? Why or why not? What are the tradeoffs of immutable objects in a domain model like recipes?


My Answer:

Quantity is an abstract class, but it enforces immutability in their subclasses by storing their state in final fields and not exposing any mutators. ExactQuantity, FractionalQuantity, and RangeQuantity are immutable because all fields (amount, whole, numerator, denominator, min, max, unit) are private final, there are no setters and all validation and trimming happens in the constructor.  An object such as quantity should represent a fixed fact in a recipe rather than something that changes over time. Immutability makes the code safer and easier to reason about, because objects can be shared without worrying that some other part of the program will modify them. The main downside is that if you want to change something (like adjusting a quantity or preparation) you have to create a new object instead of updating the old one.


4. **Type Safety:** How do the `Unit` and `UnitSystem` enums provide better type safety compared to using strings for units? Give a specific example of an error that enums prevent.

My Answer:

With strings, a typo like "cups", "cup", "Cup", or "cupp" would all compile, but some would be wrong and cause bugs at runtime. With enums, the compiler guarantees that only valid units such as Unit.CUP or Unit.GRAM can be used, and mistakes are caught at compile time. Enums also let the program group and reason about units (such as filtering all UnitSystem.METRIC units), which would be fragile and error-prone with raw strings


5. **Challenges:** What was the most challenging aspect of this assignment? What strategy did you use to overcome it?

It took time to clearly understand what should be an Ingredient, what should be a Quantity, and how classes like MeasuredIngredient and VagueIngredient relate to each other without overlapping responsibilities. I overcame this by carefully reading the domain description and mapping each real-world concept (ingredient, unit, amount, preparation) to a single class with a clear purpose. When something felt confusing, I checked whether I was mixing concepts that should be separate, such as quantities versus ingredients. This helped me keep the design clean and avoid duplicated logic.


6. **AI Usage:** Did you use AI to help you with this assignment? If so, how did you use it? (Note that this information will be used to help improve the course, and **not** used to penalize you for using AI, as-per our [AI policy](https://neu-pdi.github.io/cs3100-public-resources/assignments/cyb1-recipes#ai-policy-for-this-assignment).)

I didn't use AI for this assignment.
