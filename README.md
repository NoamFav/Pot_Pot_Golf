# ⛳ Pot Pot Golf

<div align="center">

<img src="https://img.shields.io/badge/java-22+-ED8B00.svg?style=for-the-badge&logo=openjdk" alt="Java">
<img src="https://img.shields.io/badge/lwjgl-3.3-blue.svg?style=for-the-badge" alt="LWJGL">
<img src="https://img.shields.io/badge/maven-3.8-red.svg?style=for-the-badge&logo=apachemaven" alt="Maven">
<img src="https://img.shields.io/badge/license-MIT-green.svg?style=for-the-badge" alt="License">

**Physics-based golfing game with AI opponents and procedural terrain**

[Installation](#installation) · [Controls](#controls) · [Modes](#game-modes)

</div>

---

Pot Pot Golf is a 3D physics-driven golf simulation built with LWJGL. Navigate procedurally generated terrain with realistic ball dynamics, sand traps, water hazards, and intelligent AI bots.

---

## Prerequisites

- Java 22+
- Maven

---

## Installation

```bash
git clone https://github.com/NoamFav/Pot_Pot_Golf.git
cd Pot_Pot_Golf
mvn clean package
mvn exec:exec
```

> **Mac users:** Add `-XstartOnFirstThread` as a JVM argument when running from an IDE.

---

## Controls

### Camera
| Key | Action |
|-----|--------|
| WASD | Move horizontally |
| Space / Shift | Move up / down |
| Right Mouse + Drag | Rotate |

### Game
| Key | Action |
|-----|--------|
| Q | Teleport to ball |
| ↓ | Teleport to hole |
| ↑ | Teleport to start |
| R | Reset all balls |
| Esc | Open menu |
| 1 | AI bot takes a shot |
| 2 | Rule-based bot takes a shot |

---

## Game Modes

- **Single Player** — navigate the course and beat your personal best
- **Multiplayer** — two players take turns on the same course
- **Bot Mode** — watch the AI (Minimax) or rule-based bot play
- **Debug Mode** — customize start/hole positions, terrain, and physics

---

## Customization

All physics, terrain, and visual settings are configurable via the `Consts` class — gravity, friction coefficients, simplex noise parameters, FOV, and more.

---

## Credits

- Terrain textures — upklyak (Freepik)
- Tree models — printable_models (Free3D)
- Skybox — KIIRA (OpenGameArt)
- Font — Mighty Souly by graphicsauceco
- Background music — Fernweh Goldfish (UppBeat)

---

## Authors

Noam Favier, Ole Rotteveel, Beatrice Boccolari, Bruna Calvancati Lauro, Evi Levels, Ahmet Akman, Nehir Sarihan

---

## License

MIT — see [LICENSE](LICENSE).

---

<div align="center">
Made with ❤️ by <a href="https://github.com/NoamFav">NoamFav</a>
</div>
