# BBM104 — Introduction to Programming Laboratory II

> Hacettepe University · Spring 2025 · Java 8 (Oracle)

Programming assignments from BBM104, focusing on Object-Oriented Programming principles. Each project applies inheritance, encapsulation, polymorphism, and abstraction in increasingly complex systems.

## Assignments

| # | Project | OOP Focus | Run |
|---|---------|-----------|-----|
| PA1 | [Library Management System](./pa1-library-management/) | Inheritance, Encapsulation | `java Main items.txt users.txt commands.txt output.txt` |
| PA2 | [Zoo Manager](./pa2-zoo-manager/) | Abstraction, Polymorphism, Custom Exceptions | `java Main animals.txt persons.txt foods.txt commands.txt output.txt` |
| PA3 | [Student Management System](./pa3-student-management/) | All 4 OOP pillars, Interfaces | `java Main persons.txt departments.txt programs.txt courses.txt assignments.txt grades.txt output.txt` |
| PA4 | [Tank 2025 — JavaFX Game](./pa4-tank2025/) | GUI, Event-driven programming, OOP | `javac Tank2025.java && java Tank2025` |

## Requirements

```bash
java -version  # Java 8 (Oracle)
javac -version
```

## Build

```bash
javac *.java
java Main [args]
```
