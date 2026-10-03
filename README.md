<div align="center">
<picture>
  <source media="(prefers-color-scheme: dark)" srcset=".github/brand/banner-night.svg">
  <img alt="Pot_Pot_Golf: Golfing game implemented in Java using a fully custom built game engine using LWJGL" src=".github/brand/banner-paper.svg" width="100%">
</picture>
<br><br>
<a href="#prerequisites"><picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/tab-prerequisites-night.svg"><img alt="prerequisites" src=".github/brand/tab-prerequisites-paper.svg"></picture></a>
<a href="#installation"><picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/tab-installation-night.svg"><img alt="installation" src=".github/brand/tab-installation-paper.svg"></picture></a>
<a href="#controls"><picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/tab-controls-night.svg"><img alt="controls" src=".github/brand/tab-controls-paper.svg"></picture></a>
<a href="#game-modes"><picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/tab-game-modes-night.svg"><img alt="game modes" src=".github/brand/tab-game-modes-paper.svg"></picture></a>
<a href="#customization"><picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/tab-customization-night.svg"><img alt="customization" src=".github/brand/tab-customization-paper.svg"></picture></a>
<a href="#credits"><picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/tab-credits-night.svg"><img alt="credits" src=".github/brand/tab-credits-paper.svg"></picture></a>
<a href="#authors"><picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/tab-authors-night.svg"><img alt="authors" src=".github/brand/tab-authors-paper.svg"></picture></a>
<a href="#license"><picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/tab-license-night.svg"><img alt="license" src=".github/brand/tab-license-paper.svg"></picture></a>
</div>

<br>

Pot Pot Golf is a 3D physics-driven golf simulation built with LWJGL. Navigate procedurally generated terrain with realistic ball dynamics, sand traps, water hazards, and intelligent AI bots.

<p>
<a name="prerequisites"></a>
<picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/section-prerequisites-night.svg"><img alt="prerequisites" src=".github/brand/section-prerequisites-paper.svg" width="100%"></picture>
</p>

- Java 22+
- Maven

<p>
<a name="installation"></a>
<picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/section-installation-night.svg"><img alt="installation" src=".github/brand/section-installation-paper.svg" width="100%"></picture>
</p>

```bash
git clone https://github.com/NoamFav/Pot_Pot_Golf.git
cd Pot_Pot_Golf
mvn clean package
mvn exec:exec
```

> **Mac users:** Add `-XstartOnFirstThread` as a JVM argument when running from an IDE.

<p>
<a name="controls"></a>
<picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/section-controls-night.svg"><img alt="controls" src=".github/brand/section-controls-paper.svg" width="100%"></picture>
</p>

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

<p>
<a name="game-modes"></a>
<picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/section-game-modes-night.svg"><img alt="game modes" src=".github/brand/section-game-modes-paper.svg" width="100%"></picture>
</p>

- **Single Player** — navigate the course and beat your personal best
- **Multiplayer** — two players take turns on the same course
- **Bot Mode** — watch the AI (Minimax) or rule-based bot play
- **Debug Mode** — customize start/hole positions, terrain, and physics

<p>
<a name="customization"></a>
<picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/section-customization-night.svg"><img alt="customization" src=".github/brand/section-customization-paper.svg" width="100%"></picture>
</p>

All physics, terrain, and visual settings are configurable via the `Consts` class — gravity, friction coefficients, simplex noise parameters, FOV, and more.

<p>
<a name="credits"></a>
<picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/section-credits-night.svg"><img alt="credits" src=".github/brand/section-credits-paper.svg" width="100%"></picture>
</p>

- Terrain textures — upklyak (Freepik)
- Tree models — printable_models (Free3D)
- Skybox — KIIRA (OpenGameArt)
- Font — Mighty Souly by graphicsauceco
- Background music — Fernweh Goldfish (UppBeat)

<p>
<a name="authors"></a>
<picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/section-authors-night.svg"><img alt="authors" src=".github/brand/section-authors-paper.svg" width="100%"></picture>
</p>

Noam Favier, Ole Rotteveel, Beatrice Boccolari, Bruna Calvancati Lauro, Evi Levels, Ahmet Akman, Nehir Sarihan

<p>
<a name="license"></a>
<picture><source media="(prefers-color-scheme: dark)" srcset=".github/brand/section-license-night.svg"><img alt="license" src=".github/brand/section-license-paper.svg" width="100%"></picture>
</p>

MIT — see [LICENSE](LICENSE).

<div align="center">
Made with ❤️ by <a href="https://github.com/NoamFav">NoamFav</a>
</div>

<br>

<a href="https://nf-software.com">
<picture>
  <source media="(prefers-color-scheme: dark)" srcset=".github/brand/footer-night.svg">
  <img alt="NF Software" src=".github/brand/footer-paper.svg" width="100%">
</picture>
</a>
