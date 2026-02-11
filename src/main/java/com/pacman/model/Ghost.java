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
    private int speedRatio = 2; // Ghosts move every N game updates (adjustable per level)
    private int startRow;
    private int startCol;

    // Ghost States
    public enum State {
        WAITING, // Inside the ghost house
        EXITING, // Leaving the ghost house
        CHASING, // Chasing Pac-Man
        SCATTER, // Moving to corner (optional, used for variety)
        FRIGHTENED // Running away (blue)
    }

    private State state = State.WAITING;
    private int targetRow;
    private int targetCol;

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
     * 
     * @param name     the ghost's name
     * @param color    the ghost's color
     * @param startRow the starting row
     * @param startCol the starting column
     * @param gameMap  reference to the game map for wall collision checks
     */
    public Ghost(String name, Color color, int startRow, int startCol, GameMap gameMap) {
        this.name = name;
        this.ghostColor = color;
        this.row = startRow;
        this.col = startCol;
        this.startRow = startRow;
        this.startCol = startCol;
        this.gameMap = gameMap;
        this.random = new Random();
        this.currentDirection = random.nextInt(4); // Start with random direction
    }

    /**
     * Updates ghost position using simple random movement.
     * Ghosts move slower than Pac-Man (every SPEED_RATIO calls).
     * Updates ghost position based on state and AI.
     * 
     * @param pacMan reference to Pac-Man for targeting
     */
    public void update(PacMan pacMan) {
        updateCounter++;
        if (updateCounter < speedRatio) {
            return; // Skip this update to make ghost slower
        }
        updateCounter = 0;

        // Determine target based on state
        switch (state) {
            case WAITING:
                // Move up and down inside the house or stay put
                // For now, just stay put until state changes
                return;
            case EXITING:
                // Target the house exit (row 8, col 9)
                targetRow = 8;
                targetCol = 9;
                // If reached exit, switch to Chasing
                if (row == 8 && (col == 9 || col == 8 || col == 10)) {
                    state = State.CHASING;
                }
                break;
            case CHASING:
                targetRow = pacMan.getRow();
                targetCol = pacMan.getCol();
                break;
            case SCATTER:
                // Set target to corner based on color
                setScatterTarget();
                break;
            case FRIGHTENED:
                // Random movement handled separately or by setting random target
                break;
        }

        moveTowardsTarget();
    }

    /**
     * Move towards the target using simple pathfinding (Euclidean distance).
     * At intersections, choose the tile that minimizes distance to target.
     * Ghosts cannot reverse direction unless in specific states.
     */
    /**
     * Move towards the target using weighted random approach.
     * 70% chance: pick the direction closest to target (Euclidean distance).
     * 30% chance: pick a random valid direction.
     * This gives the classic Pac-Man "kinda random but chasing" feel.
     */
    private void moveTowardsTarget() {
        // Collect all valid directions (excluding reverse unless dead end)
        java.util.List<Integer> validDirs = new java.util.ArrayList<>();
        int bestDirection = -1;
        double minDistance = Double.MAX_VALUE;

        int[] directions = { UP, DOWN, LEFT, RIGHT };

        for (int dir : directions) {
            // Don't reverse unless dead end
            if (dir == getOppositeDirection(currentDirection) && countValidMoves() > 1) {
                continue;
            }
            if (canMoveInDirection(dir)) {
                validDirs.add(dir);
                int nextRow = row + getRowDelta(dir);
                int nextCol = col + getColDelta(dir);
                double distance = Math.sqrt(Math.pow(targetRow - nextRow, 2) + Math.pow(targetCol - nextCol, 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    bestDirection = dir;
                }
            }
        }

        if (validDirs.isEmpty()) {
            findNewDirection(); // Fallback
            return;
        }

        // 70% chase, 30% random
        if (random.nextDouble() < 0.7 && bestDirection != -1) {
            moveInDirection(bestDirection);
        } else {
            // Pick a random valid direction
            int randomDir = validDirs.get(random.nextInt(validDirs.size()));
            moveInDirection(randomDir);
        }
    }

    private int countValidMoves() {
        int count = 0;
        if (canMoveInDirection(UP))
            count++;
        if (canMoveInDirection(DOWN))
            count++;
        if (canMoveInDirection(LEFT))
            count++;
        if (canMoveInDirection(RIGHT))
            count++;
        return count;
    }

    private int getOppositeDirection(int dir) {
        if (dir == UP)
            return DOWN;
        if (dir == DOWN)
            return UP;
        if (dir == LEFT)
            return RIGHT;
        if (dir == RIGHT)
            return LEFT;
        return -1;
    }

    private void setScatterTarget() {
        // Simple corners for now
        switch (ghostColor) {
            case RED:
                targetRow = 0;
                targetCol = gameMap.getCols() - 1;
                break; // Top Right
            case PINK:
                targetRow = 0;
                targetCol = 0;
                break; // Top Left
            // Fallback for other colors
            default:
                targetRow = gameMap.getRows() - 1;
                targetCol = 0;
                break; // Bottom Left
        }
    }

    public void setState(State newState) {
        this.state = newState;
    }

    public State getState() {
        return state;
    }

    /**
     * Sets the ghost speed ratio. Lower = faster.
     * 
     * @param ratio the new speed ratio (min 1)
     */
    public void setSpeedRatio(int ratio) {
        this.speedRatio = Math.max(1, ratio);
    }

    /**
     * Resets ghost to its original start position and WAITING state.
     */
    public void resetPosition() {
        this.row = startRow;
        this.col = startCol;
        this.state = State.WAITING;
        this.currentDirection = random.nextInt(4);
        this.updateCounter = 0;
    }

    /**
     * Checks if the ghost can move in a specific direction without hitting a wall.
     * 
     * @param direction the direction to check (UP, DOWN, LEFT, RIGHT)
     * @return true if the direction is walkable
     */
    private boolean canMoveInDirection(int direction) {
        int newRow = row + getRowDelta(direction);
        int newCol = col + getColDelta(direction);
        return gameMap.isWalkable(newRow, newCol, true);
    }

    /**
     * Moves the ghost one cell in the specified direction.
     * 
     * @param direction the direction to move
     */
    private void moveInDirection(int direction) {
        row += getRowDelta(direction);
        col += getColDelta(direction);
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
     * 
     * @param direction the direction constant
     * @return -1 for UP, 1 for DOWN, 0 otherwise
     */
    private int getRowDelta(int direction) {
        if (direction == UP)
            return -1;
        if (direction == DOWN)
            return 1;
        return 0;
    }

    /**
     * Gets the column delta (horizontal change) for a given direction.
     * 
     * @param direction the direction constant
     * @return -1 for LEFT, 1 for RIGHT, 0 otherwise
     */
    private int getColDelta(int direction) {
        if (direction == LEFT)
            return -1;
        if (direction == RIGHT)
            return 1;
        return 0;
    }

    /**
     * Checks if this ghost collides with a given position.
     * Collision occurs when ghost and target occupy the same grid cell.
     * 
     * @param targetRow the row to check
     * @param targetCol the column to check
     * @return true if ghost occupies the same cell as target
     */
    public boolean collidesWith(int targetRow, int targetCol) {
        return this.row == targetRow && this.col == targetCol;
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
