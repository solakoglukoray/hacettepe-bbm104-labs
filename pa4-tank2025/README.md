# PA4 — Tank 2025

> BBM104 Introduction to Programming Laboratory II · Spring 2025

## Overview

A fully playable 2D tank game built entirely with JavaFX — no scene builder, no external libraries, no CSS. The game features a player-controlled tank, enemy AI, bullet physics, and explosion animations.

## Gameplay

- **Move:** Arrow keys (4 cardinal directions)
- **Shoot:** `X` key
- **Pause:** `P` key → pause menu (restart or quit)
- **Game Over:** `R` to restart, `Escape` to quit

## Features

| Feature | Description |
|---------|-------------|
| Player tank | Movement + shooting with thread animations |
| Enemy AI | Random direction movement, fires at random intervals |
| Bullet physics | Constant speed, wall collision, explosion effect |
| Indestructible walls | Border + interior walls, no pass-through |
| Enemy spawning | Random intervals, upper map area |
| Score system | Increments on enemy kill |
| Lives system | 3 lives, respawn on hit, game over screen |
| Pause menu | Mid-game pause with restart/quit options |

## Run

```bash
javac Tank2025.java
java Tank2025
```

> Requires Java 8 (Oracle) with JavaFX bundled. Tested on Windows 10+ and macOS 12+.

## Topics

`JavaFX` `GUI programming` `Event-driven design` `Animation` `OOP` `Game loop`
