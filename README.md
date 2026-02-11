# Pac-Man Game (Java Swing)

A classic Pac-Man game implementation using Java Swing, featuring design patterns for clean, maintainable code architecture.

## Quick Start

```bash
# Compile
javac -d build/classes -sourcepath src/main/java @.javacfiles.txt

# Run
java -cp build/classes com.pacman.ui.GameFrame
```

## Controls

- **Arrow Keys**: Move Pac-Man (up, down, left, right)
- **Close Window**: Exit game

---

## Architecture Overview

### Design Patterns Used

| Pattern | Purpose | Implementation |
|---------|---------|----------------|
| **State** | Ghost behavior modes | `GhostState` + 5 concrete states |
| **Strategy** | Ghost targeting AI | `IGhostStrategy` + 4 concrete strategies |
| **Factory** | Ghost creation | `AbstractGhostFactory` + 4 factories |
| **Observer** | Event notifications | `Observer`/`Sujet` interfaces |

---

## Project Structure

```
src/main/java/com/pacman/
│
├── core/                    # Core game logic
│   ├── Game.java           # Main controller, manages entities
│   ├── Observer.java       # Observer interface (receives events)
│   ├── Sujet.java          # Subject interface (sends events)  
│   └── UIPanel.java        # Score display panel
│
├── entity/                  # Game entities
│   ├── Entity.java         # Abstract base class
│   ├── StaticEntity.java   # Non-moving entities (8px)
│   ├── MovingEntity.java   # Moving entities (32px, animated)
│   ├── Wall.java           # Maze boundaries
│   ├── GhostHouse.java     # Ghost spawn door
│   ├── PacGum.java         # Regular pellet (+10 pts)
│   ├── SuperPacGum.java    # Power pellet (+100 pts)
│   └── PacMan.java         # Player character
│
├── ghost/                   # Ghost-related classes
│   ├── Ghost.java          # Abstract ghost with states
│   ├── Blinky.java         # Red ghost (Shadow)
│   ├── Pinky.java          # Pink ghost (Speedy)
│   ├── Inky.java           # Cyan ghost (Bashful)
│   ├── Clyde.java          # Orange ghost (Pokey)
│   │
│   ├── state/              # State Pattern: Ghost behaviors
│   │   ├── GhostState.java     # Abstract state with pathfinding
│   │   ├── ChaseMode.java      # Pursuing PacMan
│   │   ├── ScatterMode.java    # Targeting corners
│   │   ├── FrightenedMode.java # Vulnerable (random movement)
│   │   ├── EatenMode.java      # Returning to house (eyes only)
│   │   └── HouseMode.java      # Inside ghost house
│   │
│   ├── strategy/           # Strategy Pattern: Targeting AI
│   │   ├── IGhostStrategy.java # Strategy interface
│   │   ├── BlinkyStrategy.java # Targets PacMan directly
│   │   ├── PinkyStrategy.java  # Targets 4 tiles ahead
│   │   ├── InkyStrategy.java   # Uses Blinky position to flank
│   │   └── ClydeStrategy.java  # Shy - retreats when close
│   │
│   └── factory/            # Factory Pattern: Ghost creation
│       ├── AbstractGhostFactory.java
│       ├── BlinkyFactory.java
│       ├── PinkyFactory.java
│       ├── InkyFactory.java
│       └── ClydeFactory.java
│
├── util/                    # Utility classes
│   ├── CsvReader.java      # Level loading
│   ├── KeyHandler.java     # Keyboard input
│   ├── CollisionDetector.java    # Entity collisions
│   ├── WallCollisionDetector.java # Wall collisions
│   └── Utils.java          # Math helpers
│
└── ui/                      # User interface
    ├── GameFrame.java      # Main window
    └── GamePanel.java      # Game rendering (60 FPS)
```

---

## Entity Hierarchy

```
Entity (abstract)
├── StaticEntity (8x8 pixels, fixed position)
│   ├── Wall         - Blocks movement
│   ├── GhostHouse   - Ghost spawn door (ghosts pass through)
│   ├── PacGum       - Regular pellet (+10 points)
│   └── SuperPacGum  - Power pellet (+100 points, frightens ghosts)
│
└── MovingEntity (32x32 pixels, animated sprites)
    ├── PacMan       - Player character
    └── Ghost        - Enemy characters
        ├── Blinky (Red)   - Direct pursuit
        ├── Pinky (Pink)   - Ambush (targets ahead)
        ├── Inky (Cyan)    - Flanking (uses Blinky's position)
        └── Clyde (Orange) - Shy (retreats when close)
```

---

## Ghost State Machine

```
                    ┌─────────────┐
                    │  HouseMode  │ ◄── Start
                    └──────┬──────┘
                           │ exit house
                           ▼
           ┌────────────────────────────────┐
           │                                │
     ┌─────┴──────┐     timer      ┌───────┴───────┐
     │ ChaseMode  │◄──────────────►│ ScatterMode   │
     └─────┬──────┘  (20s / 7s)    └───────┬───────┘
           │                                │
           └────────────────────────────────┘
                           │
                           │ SuperPacGum eaten
                           ▼
                ┌────────────────────┐
                │  FrightenedMode    │ (7 seconds, random movement)
                └─────────┬──────────┘
                          │ eaten by PacMan
                          ▼
                  ┌──────────────┐
                  │  EatenMode   │ (eyes only, returns to house)
                  └──────┬───────┘
                         │ enters house
                         ▼
                    HouseMode
```

### State Descriptions

| State | Sprite | Behavior | Duration |
|-------|--------|----------|----------|
| **HouseMode** | Normal | Exit ghost house | Until exit |
| **ChaseMode** | Normal | Pursue PacMan (strategy-based) | 20 seconds |
| **ScatterMode** | Normal | Target corner (strategy-based) | 7 seconds |
| **FrightenedMode** | Blue/White | Random movement, can be eaten | 7 seconds |
| **EatenMode** | Eyes only | Return to ghost house | Until house |

---

## Ghost Targeting Strategies

Each ghost has unique targeting behavior:

### Blinky (Red) - "Shadow"
- **Chase**: Targets PacMan's current position
- **Scatter**: Top-right corner

### Pinky (Pink) - "Speedy"
- **Chase**: Targets 4 tiles ahead of PacMan
- **Scatter**: Top-left corner

### Inky (Cyan) - "Bashful"
- **Chase**: Complex - uses vector from Blinky to 2 tiles ahead of PacMan, then doubles it
- **Scatter**: Bottom-right corner

### Clyde (Orange) - "Pokey"
- **Chase**: If >8 tiles from PacMan, targets PacMan; otherwise, retreats to scatter target
- **Scatter**: Bottom-left corner

```
   Pinky ┌─────────────────────┐ Blinky
   (0,0) │                     │ (448,0)
         │                     │
         │                     │
   Clyde └─────────────────────┘ Inky
   (0,496)                       (448,496)
```

---

## Observer Pattern Flow

```
PacMan (Subject) ──notifies──► Observer(s)
                               ├── Game
                               │   • Destroys eaten pellets
                               │   • Triggers ghost Frightened mode
                               │   • Handles game over
                               │
                               └── UIPanel
                                   • Updates score display
                                   • +10 for PacGum
                                   • +100 for SuperPacGum
                                   • +500 for eating ghost
```

---

## Level Format (CSV)

The game level is loaded from `level.csv` using semicolon separators:

| Symbol | Entity | Description |
|--------|--------|-------------|
| `x` | Wall | Blocks movement |
| `.` | PacGum | Regular pellet (+10 points) |
| `o` | SuperPacGum | Power pellet (+100 points) |
| `-` | GhostHouse | Door (ghosts can pass) |
| `P` | PacMan | Player spawn position |
| `b` | Blinky | Red ghost spawn |
| `p` | Pinky | Pink ghost spawn |
| `i` | Inky | Cyan ghost spawn |
| `c` | Clyde | Orange ghost spawn |

Cell size: 8 pixels. Entity size: 32 pixels (4x4 cells).

---

## Required Assets

Place these PNG files in the project root:

| File | Description |
|------|-------------|
| `background.png` | Maze background (448x496) |
| `pacman.png` | PacMan sprite sheet |
| `blinky.png` | Red ghost sprite sheet |
| `pinky.png` | Pink ghost sprite sheet |
| `inky.png` | Cyan ghost sprite sheet |
| `clyde.png` | Orange ghost sprite sheet |
| `ghost_frightened.png` | Blue frightened ghost |
| `ghost_frightened_2.png` | White frightened ghost (flashing) |
| `ghost_eaten.png` | Eyes-only sprite |
| `level.csv` | Level data |

---

## Game Specifications

| Property | Value |
|----------|-------|
| Window Size | 704 x 496 pixels |
| Gameplay Area | 448 x 496 pixels |
| UI Panel | 256 x 496 pixels |
| Frame Rate | 60 FPS |
| Cell Size | 8 pixels |
| Entity Size | 32 pixels |
| PacMan Speed | 2 pixels/frame |
| Ghost Speed | 2 pixels/frame |

---

## Score System

| Action | Points |
|--------|--------|
| Eat PacGum | +10 |
| Eat SuperPacGum | +100 |
| Eat Frightened Ghost | +500 |

---

## Class Responsibilities

### Core Classes

| Class | Responsibility |
|-------|----------------|
| `Game` | Entity management, game logic, Observer callbacks |
| `GamePanel` | 60 FPS game loop, rendering, input handling |
| `GameFrame` | Main window container |
| `UIPanel` | Score display, Observer for score updates |

### Entity Classes

| Class | Responsibility |
|-------|----------------|
| `Entity` | Base class with position, size, hitbox |
| `StaticEntity` | Fixed-position entities (8px) |
| `MovingEntity` | Animated entities with velocity (32px) |
| `PacMan` | Player input, collision detection, Subject |
| `Ghost` | State machine, strategy-based AI |

### Pattern Classes

| Class | Pattern | Responsibility |
|-------|---------|----------------|
| `GhostState` | State | Ghost behavior base class |
| `IGhostStrategy` | Strategy | Ghost targeting interface |
| `AbstractGhostFactory` | Factory | Ghost creation interface |
| `Observer/Sujet` | Observer | Event notification |

---

## Building & Running

### Prerequisites
- Java JDK 8+
- PNG assets in project root
- `level.csv` in project root

### Compile
```bash
# Generate file list (PowerShell)
Get-ChildItem -Path src\main\java\com\pacman -Recurse -Filter "*.java" | ForEach-Object { $_.FullName } | Out-File -FilePath .javacfiles.txt -Encoding ASCII

# Compile
javac -d build/classes -sourcepath src/main/java "@.javacfiles.txt"
```

### Run
```bash
java -cp build/classes com.pacman.ui.GameFrame
```

---

## License

Educational project for learning Java game development and design patterns.

