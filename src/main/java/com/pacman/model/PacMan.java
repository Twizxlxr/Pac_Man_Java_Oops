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
    private double mouthAngle = 0.0; // Angle for mouth opening (10-40 degrees)
    private static final double MOUTH_MIN_ANGLE = 10.0;
    private static final double MOUTH_MAX_ANGLE = 40.0;
    
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
        
        // Wrap through side tunnels
        if (tempCol < 0) {
            tempCol = gameMap.getCols() - 1;
        } else if (tempCol >= gameMap.getCols()) {
            tempCol = 0;
        }

        // Update position
        row = tempRow;
        col = tempCol;
        
        // Update mouth animation
        updateMouthAnimation();
    }
    
    /**
     * Updates mouth animation - oscillates between MOUTH_MIN_ANGLE and MOUTH_MAX_ANGLE.
     */
    private void updateMouthAnimation() {
        double cycle = (System.currentTimeMillis() / 50) % 100; // 100ms cycle
        double normalized = Math.sin(cycle * Math.PI / 100);
        double amplitude = (MOUTH_MAX_ANGLE - MOUTH_MIN_ANGLE) / 2.0;
        double midpoint = MOUTH_MIN_ANGLE + amplitude;
        mouthAngle = midpoint + amplitude * normalized;
        if (mouthAngle < MOUTH_MIN_ANGLE) {
            mouthAngle = MOUTH_MIN_ANGLE;
        }
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
        if (fromRow == GameMap.TUNNEL_ROW) {
            if (direction == LEFT && fromCol == 0) {
                return true;
            }
            if (direction == RIGHT && fromCol == gameMap.getCols() - 1) {
                return true;
            }
        }
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
