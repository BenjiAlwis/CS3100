# Diagrams

Save your hexagonal architecture diagram here.

**From Part 3, Exercise 3.2:** Create a diagram showing the hexagonal architecture for **Automation Rules** — the domain, port interfaces, at least one production adapter, and one test double.

You can produce this in any way that works:
- Save a Mermaid diagram as `automation-rules.md`
- Take a photo of a paper diagram and add it here
- Use any diagramming tool and export the result

Your diagram should correctly show:
- Adapters depending on ports (arrows point inward)
- Test doubles implementing the same ports as production adapters
- The domain depending only on port interfaces, not on adapters
