# Pac-Man Game (Java Swing)

A classic-style Pac-Man game implementation using Java Swing, featuring a redesigned arcade maze, 4 animated ghosts, and Pac-Man mouth animation.

## Features

- **Classic Arcade Maze**: 19x21 grid designed to closely match the original Pac-Man layout
  - Outer boundary loop
  - Central ghost spawn box
  - Long horizontal corridors
  - Vertical connectors
  - Side tunnels
  - Symmetrical design
- **Pac-Man Character**: Animated yellow player-controlled character
  - Smooth keyboard-controlled movement (arrow keys)
  - Animated mouth that opens and closes while moving
  - Mouth direction rotates based on movement direction
- **4 Animated Ghosts**: Distinct enemy characters with personality
  - **Blinky** (Red) - spawns at center
  - **Pinky** (Pink) - spawns at top-left
  - **Inky** (Cyan) - spawns at top-right
  - **Clyde** (Orange) - spawns at bottom
  - Semi-circle heads with wavy bottoms
  - White eyes with black pupils
  - Random movement respecting maze walls
  - Move slightly slower than Pac-Man
- **Wall Collision Detection**: Prevents movement through walls
- **Ghost Collision Detection**: Penalizes score when colliding with ghosts
- **Advanced Graphics**: Graphics2D rendering with polished visuals
  - Thick blue walls with rounded edges
  - Black background
  - White pellets along paths
  - Larger power pellets in corner positions
  - Antialiased graphics for smooth appearance

## Project Structure

```
src/main/java/com/pacman/
├── game/          # Game logic (extensible for future features)
├── model/         # Game data models
│   ├── GameMap.java    # 2D grid with classic arcade maze design
│   ├── PacMan.java     # Player character with mouth animation
│   └── Ghost.java      # Enemy characters with random movement
└── ui/            # GUI components
    ├── GameFrame.java  # Main application window
    └── GamePanel.java  # Game rendering and game loop
```

## How to Compile

```bash
javac -d build/classes -sourcepath src/main/java src/main/java/com/pacman/model/*.java src/main/java/com/pacman/ui/*.java
```

## How to Run

```bash
java -cp build/classes com.pacman.ui.GameFrame
```

## Controls

- **Arrow Keys**: Move Pac-Man up, down, left, or right
- **Close Window**: Exit game

## Game Details

- **Blue Walls**: Boundaries and maze structure with thick rounded edges
- **White Dots**: Pellets to collect (small dots along paths)
- **White Larger Dots**: Power pellets in corner positions
- **Yellow Circle with Animated Mouth**: Pac-Man (the player)
- **Colored Ghosts**:
  - Red ghost (Blinky)
  - Pink ghost (Pinky)
  - Cyan ghost (Inky)
  - Orange ghost (Clyde)
- **Collision System**: 
  - Pac-Man loses 100 points per ghost collision
  - Pac-Man resets to center after collision
  - Score displayed on screen

## Implementation Notes

### GameMap Class
- Implements classic Pac-Man arcade maze layout with 19x21 grid
- Creates outer boundary, ghost spawn box, corridors, connectors, and side tunnels
- Supports symmetrical maze patterns
- Provides walkability checking for collision detection
- Tile types: EMPTY (0), WALL (1), DOT (2)

### PacMan Class
- Tracks position and direction (UP, DOWN, LEFT, RIGHT)
- Smooth movement with direction queuing
- **Mouth Animation**: Uses time-based oscillation for smooth opening/closing
- Animated arc rendering using `fillArc()` with direction-based rotation
- Collision detection with game map walls
- Reset functionality for ghost collisions

### Ghost Class
- Random movement with wall collision detection
- Direction change when blocked by walls
- 4 unique colored ghosts (red, pink, cyan, orange)
- Moves at 50% speed of Pac-Man for balanced gameplay
- Advanced rendering with:
  - Rectangular body base
  - Semi-circle head
  - Wavy bottom pattern (3-wave design)
  - White eyes with black pupils
  - Outlined edges
- Collision detection with Pac-Man

### GamePanel Class
- Implements game loop with 100ms update interval
- Renders all game entities with Graphics2D
- Uses `BasicStroke` for thick wall rendering
- Handles keyboard input for Pac-Man control
- Updates ghost positions and collision detection each frame
- Displays score and game information

### GameFrame Class
- Main application window container
- Runs application on EDT (Event Dispatch Thread) for thread safety
- Sets window size based on maze dimensions

## Graphics Rendering

- **Graphics2D**: All graphics rendered using advanced Java 2D API
- **Antialiasing**: Smooth rendering of all shapes and lines
- **Stroke Control**: `BasicStroke(3)` for thick, rounded walls
- **Color Accuracy**: RGB colors match classic Pac-Man arcade style
- **Arc Rendering**: Used for Pac-Man mouth and ghost shapes

## Future Enhancements

- Score system with pellet collection
- Levels and difficulty progression
- Ghost AI with pathfinding
- Power-ups and special items
- Sound effects
- Multiple levels with increasing difficulty
- High score tracking

## Game Loop Architecture

- Single Timer running at 100ms intervals
- Updates Pac-Man position and direction each cycle
- Updates all 4 ghost positions each cycle
- Collision detection runs after all updates
- Single `repaint()` call per cycle for smooth rendering

