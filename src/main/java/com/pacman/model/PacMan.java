package com.pacman.model;

/**
 * PacMan represents the player character in the game.
 * Handles position, movement, collision detection, and mouth animation.
 */
public class PacMan {
    private int row;
    private int col;
    private int nextRow;
    private int nextCol;
    private GameMap gameMap;
    
    // Direction constants
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    
    private int currentDirection = RIGHT;
    private int nextDirection = RIGHT;
    
    // Mouth animation
    private double mouthAngle = 0.0; // Angle for mouth opening (0-45 degrees)
    private static final double MOUTH_ANIMATION_SPEED = 0.1; // How fast the mouth opens/closes
    private static final double MOUTH_MAX_ANGLE = 45.0; // Maximum mouth opening in degrees
    
    public PacMan(int startRow, int startCol, GameMap gameMap) {
        this.row = startRow;
        this.col = startCol;
        this.nextRow = startRow;
        this.nextCol = startCol;
        this.gameMap = gameMap;
    }
    
    /**
     * Sets the intended direction for the next movement.
     * This allows smooth movement - the player can queue up the next direction.
     * @param direction the direction to move (UP, DOWN, LEFT, or RIGHT)
     */
    public void setNextDirection(int direction) {
        this.nextDirection = direction;
    }
    
    /**
     * Updates Pac-Man's position based on the current or next direction.
     * First tries to move in the next direction, then falls back to current direction.
     * Collision checking ensures Pac-Man doesn't walk through walls.
     * Also updates mouth animation.
     */
    public void update() {
        // Calculate next position based on nextDirection
        int tempRow = row;
        int tempCol = col;
        
        // Apply nextDirection
        if (canMoveInDirection(nextDirection, tempRow, tempCol)) {
            tempRow += getRowDelta(nextDirection);
            tempCol += getColDelta(nextDirection);
            currentDirection = nextDirection;
        } 
        // If nextDirection is blocked, try currentDirection
        else if (canMoveInDirection(currentDirection, row, col)) {
            tempRow = row + getRowDelta(currentDirection);
            tempCol = col + getColDelta(currentDirection);
        }
        
        // Update position
        row = tempRow;
        col = tempCol;
        
        // Update mouth animation
        updateMouthAnimation();
    }
    
    /**
     * Updates mouth animation - oscillates between 0 and MOUTH_MAX_ANGLE.
     */
    private void updateMouthAnimation() {
        mouthAngle += MOUTH_ANIMATION_SPEED;
        if (mouthAngle > MOUTH_MAX_ANGLE) {
            mouthAngle = MOUTH_MAX_ANGLE;
            // Reverse direction
            if (MOUTH_ANIMATION_SPEED > 0) {
                // We need a way to reverse, use a simple approach:
                // Just alternate by checking current value
            }
        }
        // Simple oscillation using modulo
        double cycle = (System.currentTimeMillis() / 50) % 100; // 100ms cycle
        mouthAngle = MOUTH_MAX_ANGLE * Math.sin(cycle * Math.PI / 100);
        if (mouthAngle < 0) mouthAngle = Math.abs(mouthAngle);
    }
    
    /**
     * Checks if Pac-Man can move in a specific direction from a given position.
     * @param direction the direction to check
     * @param fromRow the starting row
     * @param fromCol the starting column
     * @return true if the move is valid (not blocked by a wall)
     */
    private boolean canMoveInDirection(int direction, int fromRow, int fromCol) {
        int newRow = fromRow + getRowDelta(direction);
        int newCol = fromCol + getColDelta(direction);
        return gameMap.isWalkable(newRow, newCol);
    }
    
    /**
     * Gets the row delta (change in row) for a given direction.
     * @param direction the direction
     * @return -1 for UP, 1 for DOWN, 0 otherwise
     */
    private int getRowDelta(int direction) {
        if (direction == UP) return -1;
        if (direction == DOWN) return 1;
        return 0;
    }
    
    /**
     * Gets the column delta (change in column) for a given direction.
     * @param direction the direction
     * @return -1 for LEFT, 1 for RIGHT, 0 otherwise
     */
    private int getColDelta(int direction) {
        if (direction == LEFT) return -1;
        if (direction == RIGHT) return 1;
        return 0;
    }
    
    // Getter methods
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public int getCurrentDirection() {
        return currentDirection;
    }
    
    /**
     * Gets the current mouth angle for animation rendering.
     * @return the mouth opening angle in degrees
     */
    public double getMouthAngle() {
        return Math.abs(mouthAngle);
    }
    
    /**
     * Resets Pac-Man to a specified position and direction.
     * Used when Pac-Man collides with a ghost.
     * @param newRow the new row position
     * @param newCol the new column position
     */
    public void resetPosition(int newRow, int newCol) {
        this.row = newRow;
        this.col = newCol;
        this.currentDirection = RIGHT;
        this.nextDirection = RIGHT;
    }
}
