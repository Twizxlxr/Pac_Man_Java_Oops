package com.pacman.model;

import java.util.Random;

/**
 * Ghost represents an enemy character that moves around the maze.
 * Uses simple random movement logic with no complex AI or pathfinding.
 * Ghosts respect wall boundaries and stay within the maze.
 * 
 * Ghosts move slightly slower than Pac-Man and are displayed with:
 * - A colored body (red, pink, cyan, or orange)
 * - A semicircle head with wavy bottom pattern
 * - Two white eyes with black pupils
 */
public class Ghost {
    private int row;
    private int col;
    private GameMap gameMap;
    private Random random;
    private int currentDirection;
    private String name;
    private Color ghostColor;
    
    private int updateCounter = 0; // Counter to make ghosts move slower than Pac-Man
    private static final int SPEED_RATIO = 2; // Ghosts move every 2 game updates (slower)
    
    // Freeze state tracking
    private boolean isFrozen = false;
    private long freezeEndTime = 0;
    private static final long FREEZE_DURATION = 8000; // 8 seconds in milliseconds
    
    // Direction constants (same as PacMan)
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    
    // Ghost color types
    public enum Color {
        RED, PINK, CYAN, ORANGE
    }
    
    /**
     * Creates a ghost at a specific starting position.
     * @param name the ghost's name
     * @param color the ghost's color
     * @param startRow the starting row
     * @param startCol the starting column
     * @param gameMap reference to the game map for wall collision checks
     */
    public Ghost(String name, Color color, int startRow, int startCol, GameMap gameMap) {
        this.name = name;
        this.ghostColor = color;
        this.row = startRow;
        this.col = startCol;
        this.gameMap = gameMap;
        this.random = new Random();
        this.currentDirection = random.nextInt(4); // Start with random direction
    }
    
    /**
     * Updates ghost position using simple random movement.
     * Ghosts move slower than Pac-Man (every SPEED_RATIO calls).
     * If frozen (power pellet effect), ghost stays in place until freeze ends.
     * 
     * Logic:
     * 1. Check if freeze duration has expired and unfreeze if needed
     * 2. If not frozen, try to move in the current direction
     * 3. If blocked by a wall, pick a new random direction
     * 4. Move in the new direction if possible
     */
    public void update() {
        // Check if freeze duration has expired
        if (isFrozen && System.currentTimeMillis() >= freezeEndTime) {
            isFrozen = false;
        }
        
        // If frozen, don't move
        if (isFrozen) {
            return;
        }
        
        updateCounter++;
        if (updateCounter < SPEED_RATIO) {
            return; // Skip this update to make ghost slower
        }
        updateCounter = 0;
        
        // Try to move in current direction
        if (canMoveInDirection(currentDirection)) {
            moveInDirection(currentDirection);
        } else {
            // If blocked, find a new random direction that is walkable
            findNewDirection();
        }
    }
    
    /**
     * Checks if the ghost can move in a specific direction without hitting a wall.
     * @param direction the direction to check (UP, DOWN, LEFT, RIGHT)
     * @return true if the direction is walkable
     */
    private boolean canMoveInDirection(int direction) {
        int newRow = row + getRowDelta(direction);
        int newCol = col + getColDelta(direction);
        if (row == GameMap.TUNNEL_ROW) {
            if (direction == LEFT && col == 0) {
                return true;
            }
            if (direction == RIGHT && col == gameMap.getCols() - 1) {
                return true;
            }
        }
        return gameMap.isWalkable(newRow, newCol);
    }
    
    /**
     * Moves the ghost one cell in the specified direction.
     * @param direction the direction to move
     */
    private void moveInDirection(int direction) {
        row += getRowDelta(direction);
        col += getColDelta(direction);
        if (col < 0) {
            col = gameMap.getCols() - 1;
        } else if (col >= gameMap.getCols()) {
            col = 0;
        }
        currentDirection = direction;
    }
    
    /**
     * Finds a new valid random direction when blocked.
     * Tries up to 4 random directions to find a walkable path.
     * If no direction works, the ghost stays in place (typically won't happen).
     */
    private void findNewDirection() {
        int attempts = 0;
        int maxAttempts = 4;
        
        while (attempts < maxAttempts) {
            int newDirection = random.nextInt(4);
            if (canMoveInDirection(newDirection)) {
                moveInDirection(newDirection);
                return;
            }
            attempts++;
        }
        // If trapped, stay in place (very rare in typical maze design)
    }
    
    /**
     * Gets the row delta (vertical change) for a given direction.
     * @param direction the direction constant
     * @return -1 for UP, 1 for DOWN, 0 otherwise
     */
    private int getRowDelta(int direction) {
        if (direction == UP) return -1;
        if (direction == DOWN) return 1;
        return 0;
    }
    
    /**
     * Gets the column delta (horizontal change) for a given direction.
     * @param direction the direction constant
     * @return -1 for LEFT, 1 for RIGHT, 0 otherwise
     */
    private int getColDelta(int direction) {
        if (direction == LEFT) return -1;
        if (direction == RIGHT) return 1;
        return 0;
    }
    
    /**
     * Checks if this ghost collides with a given position.
     * Collision occurs when ghost and target occupy the same grid cell.
     * @param targetRow the row to check
     * @param targetCol the column to check
     * @return true if ghost occupies the same cell as target
     */
    public boolean collidesWith(int targetRow, int targetCol) {
        return this.row == targetRow && this.col == targetCol;
    }
    
    /**
     * Freezes the ghost for a fixed duration.
     * Used when Pac-Man eats a power pellet.
     */
    public void freeze() {
        this.isFrozen = true;
        this.freezeEndTime = System.currentTimeMillis() + FREEZE_DURATION;
    }
    
    /**
     * Checks if the ghost is currently frozen.
     * @return true if ghost is frozen
     */
    public boolean isFrozen() {
        return isFrozen;
    }
    
    /**
     * Unfreezes the ghost immediately.
     */
    public void unfreeze() {
        this.isFrozen = false;
    }
    
    // Getter methods
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public String getName() {
        return name;
    }
    
    public Color getGhostColor() {
        return ghostColor;
    }
    
    public int getCurrentDirection() {
        return currentDirection;
    }
}

