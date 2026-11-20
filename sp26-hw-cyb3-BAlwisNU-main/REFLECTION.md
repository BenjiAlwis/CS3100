# Reflection

Complete all 6 questions below. Each question is worth **4 points** (24 points total).

Your reflection should include **3-5 sentences** for each question. Reference your code submission
with specific file names, class names, or method names where appropriate.

---

## Q1: Domain Model Design (4 points)

**Prompt:** Describe your approach to implementing `PersonalCollectionImpl` and `WebCollectionImpl`.
How did you leverage the `CookbookImpl` reference implementation? What modifications did you make
for each collection type's unique metadata?

**Your Answer:**

I implemented PersonalCollectionImpl and WebCollectionImpl by replicating the CookbookImpl pattern: immutable fields, a validating Builder, and transformation methods (addRecipe, removeRecipe) that return new instances. I used CookbookImpl as the reference for how to normalize blank optional strings (treat as absent), how to validate title eagerly in title(...), and how to enforce duplicate recipe-ID checks when recipes(...) is set. For PersonalCollectionImpl, I added the type-specific metadata description and notes as optional strings that normalize blanks to empty Optional. For WebCollectionImpl, I added required sourceUrl plus optional siteName and dateAccessed, with siteName also using blank-string normalization. In both types, I ensured getSourceType() returns the correct enum value (PERSONAL or WEBSITE) and included the type-specific fields in equals()/hashCode() consistent with CookbookImpl.
---

## Q2: Architecture and Testability (4 points)

**Prompt:** The assignment uses interfaces (`RecipeCollection`, `RecipeRepository`) with concrete
implementations. Give a specific example from your code showing how this separation enables testing.
What would be harder to test without the interface abstraction?

**Your Answer:**

The interface separation let me write tests against RecipeCollectionRepository and RecipeCollection without depending on the concrete JSON storage logic beyond what the interface promises. For example, in JsonRecipeCollectionRepositoryTest, I call repository.save(...) and then repository.findById(...) and assert the returned object preserves the concrete subtype (e.g., instanceof Cookbook) without referencing internal file formats or helper methods. This makes it easy to test behavior like idempotent delete, case-insensitive findByTitle, and polymorphic round-trips purely through the repository interface. Without the interface, my domain tests would either need real file I/O everywhere or would be forced to mock internal private helpers, which is brittle and not aligned with the spec. The abstraction also makes mutation testing meaningful because the same tests can be run against alternative (buggy) implementations while keeping the test expectations constant.

---

## Q3: JSON Serialization (4 points)

**Prompt:** Explain how polymorphic serialization works for `RecipeCollection` in your
`JsonRecipeCollectionRepository`. What Jackson annotations did you add and why? Show an example of
the JSON structure for one collection type.

**Your Answer:**

Polymorphic serialization works by having Jackson include a discriminator property in the JSON (commonly "type") so it can reconstruct the correct concrete class when deserializing a RecipeCollection. I ensured my collection implementations follow the same pattern as the provided Quantity/Ingredient hierarchies by adding @JsonTypeInfo and @JsonSubTypes (either on the base interface if allowed by the starter code, or on the concrete implementations in the way the project expects) and using @JsonCreator + @JsonProperty on the private constructor used for deserialization. This is why objectMapper.readValue(file, RecipeCollection.class) can return a CookbookImpl (or PersonalCollectionImpl, WebCollectionImpl) instead of a generic interface instance. A representative JSON for a cookbook looks like: {"type":"cookbook","id":"c1","title":"Test Cookbook","author":"Test Author","recipes":[...]} (with additional metadata like isbn/publisher/year when present). The key is that the "type" field and matching subtype mapping are what preserve the concrete type across save/load.

---

## Q4: AI Effectiveness (4 points)

**Prompt:** Identify two specific tasks where AI assistance was most valuable and one where it was
least helpful. For each, explain why you think the AI performed that way.

**Your Answer:**

AI was most valuable for generating boilerplate-heavy Javadoc and consistent method documentation across repositories and model implementations, because the style was repetitive and easy to template. AI was also very helpful in expanding tests systematically (edge cases, error cases, mutation-oriented checks) once I provided the exact specification, because it could enumerate likely fault patterns quickly. The least helpful area was diagnosing subtle Jackson polymorphism failures when my JSON structure didn’t match the annotations (e.g., unexpected fields like "sourceType"), because the correct fix depended on the project’s exact annotation placement and constructor signatures. In those cases, AI suggestions were sometimes “generically correct” but not aligned with how the starter code expected polymorphism to be configured. The best results came when I used AI for hypothesis generation, then validated by reading the reference implementation and reproducing the exact annotation and constructor patterns.


---

## Q5: AI for Understanding Code (4 points)

**Prompt:** This assignment provided substantial starter code (interfaces, Jackson-annotated
classes, design patterns). Describe how you used AI to understand unfamiliar parts of the provided
code. What specific questions did you ask? What did you learn that you wouldn't have figured out as
quickly on your own?

**Your Answer:**

I used AI to walk through the provided interface hierarchy (RecipeCollection, Cookbook, PersonalCollection, WebCollection) and confirm what behaviors were required for immutability and transformation methods. I asked targeted questions like “What does @JsonTypeInfo do here and how does it affect readValue(..., RecipeCollection.class)?” and “Why does CookbookImpl use a private @JsonCreator constructor plus a Builder?” AI helped me connect how Jackson uses @JsonCreator/@JsonProperty to rehydrate immutable objects and why Optional fields require Jdk8Module. I also asked for interpretation checks on tricky spec points like blank-string normalization and order-independence for repository/library search methods. The biggest time-saver was getting a quick mental model of how polymorphic type tags propagate into nested objects (recipes containing quantities/ingredients), which made debugging repository round-trips much faster.

---

## Q6: AI Iteration (4 points)

**Prompt:** Describe a specific instance where AI-generated code didn't work correctly on the first
try. What was the original code? What was wrong with it? How did you fix it (with or without AI
help)?

**Your Answer:**

A concrete example was an AI-generated save() implementation that mistakenly called ObjectMapper.writeValue(...) as if writeValue were a static method, which doesn’t compile because writeValue is an instance method on a configured mapper. The same mistake appeared for readValue, where the code used ObjectMapper.readValue(...) instead of mapper.readValue(...), causing compilation errors and also ignoring the modules I registered (Jdk8Module, JavaTimeModule). I fixed it by storing a configured ObjectMapper field (via createObjectMapper()), then calling objectMapper.writeValue(file.toFile(), obj) and objectMapper.readValue(file.toFile(), Type.class) everywhere. Another iteration issue was a stream map(...) lambda that threw a checked IOException, which caused type inference problems; I corrected it by catching IOException inside the lambda and rethrowing as RepositoryException. After these fixes, the repository compiled cleanly and the tests could exercise behavior rather than failing at build time.


---
