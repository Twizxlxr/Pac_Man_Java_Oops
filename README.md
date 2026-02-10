# Pac-Man Game (Java Swing)

A simple, beginner-friendly Pac-Man game implementation using Java Swing.

## Features

- **2D Grid-Based Map**: 19x21 grid with walls and dots
- **Pac-Man Movement**: Control Pac-Man with arrow keys
- **Wall Collision Detection**: Pac-Man cannot pass through walls
- **Simple Maze**: Pre-generated maze pattern for gameplay
- **Java Swing GUI**: Clean graphical interface

## Project Structure

```
src/main/java/com/pacman/
├── game/          # Game logic will be added here
├── model/         # Game data models
│   ├── GameMap.java    # 2D grid representation with walls and dots
│   └── PacMan.java     # Player character with movement logic
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
- Close the window to exit

## Game Details

- **Blue squares**: Walls (boundaries and maze)
- **White dots**: Pellets to collect (future functionality)
- **Yellow circle**: Pac-Man (the player)
- Pac-Man automatically moves in the direction you select if no wall is blocking
- Smooth movement with built-in collision detection

## Implementation Notes

### GameMap Class
- Initializes a grid with borders, internal walls, and dots
- Provides methods to check if a position is walkable
- Tile types: EMPTY (0), WALL (1), DOT (2)

### PacMan Class
- Tracks current position (row, col)
- Handles movement in four directions (UP, DOWN, LEFT, RIGHT)
- Collision detection prevents walking through walls
- Smooth movement: queues next direction while current move is in progress

### GamePanel Class
- Renders the game board and Pac-Man
- Implements KeyListener for arrow key input
- Game loop runs at 100ms intervals for smooth animation

### GameFrame Class
- Main application window container
- Runs the application on the Event Dispatch Thread (EDT) for thread safety

## Future Enhancements

- Ghosts with AI pathfinding
- Score system and pellet collection
- Levels and difficulty progression
- Sound effects
- Power-ups and special items
