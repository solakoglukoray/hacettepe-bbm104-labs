# PA2 — Zoo Manager

> BBM104 Introduction to Programming Laboratory II · Spring 2025

## Overview

A zoo management simulation where personnel feed animals and visitors tour habitats. Each animal type has unique feeding behavior and habitat cleaning logic, implemented via abstraction and polymorphism.

## Class Hierarchy

```
Animal (abstract)
├── Lion        (meat, 5kg base @ age 5, ±50g/year)
├── Elephant    (plants, 10kg base @ age 20, ±15g/year)
├── Penguin     (fish, 3kg base @ age 4, ±40g/year)
└── Chimpanzee  (meat + plants, 6kg base @ age 10, ±0.025/year)

Person
├── Personnel   (can feed + clean habitats)
└── Visitor     (can visit only)
```

## Custom Exceptions

- `NotEnoughFoodException` — thrown when food stock is insufficient
- `UnauthorizedFeedException` — thrown when a visitor tries to feed
- `AnimalNotFoundException` — thrown for invalid animal names
- `PersonNotFoundException` — thrown for invalid person IDs

## Run

```bash
javac Main.java
java Main animals.txt persons.txt foods.txt commands.txt output.txt
```

## Topics

`Abstraction` `Polymorphism` `Custom exceptions` `File I/O` `JavaDoc`
