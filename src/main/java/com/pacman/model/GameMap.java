package com.pacman.model;

/**
 * GameMap represents the game grid for Pac-Man.
 * The grid is a 2D array where:
 * 0 = empty space
 * 1 = wall
 * 2 = dot/pellet
 */
public class GameMap {
    private int[][] grid;
    private static final int ROWS = 21;
    private static final int COLS = 19;
    
    // Tile types
    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int DOT = 2;
    public static final int CELL_SIZE = 30; // pixels per cell
    
    public GameMap() {
        grid = new int[ROWS][COLS];
        initializeMap();
    }
    
    /**
     * Initializes the game map with walls and dots.
     * Creates a border of walls and some internal walls for gameplay.
     */
    private void initializeMap() {
        // Fill entire grid with empty spaces
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = EMPTY;
            }
        }
        
        // Create border walls
        for (int row = 0; row < ROWS; row++) {
            grid[row][0] = WALL;
            grid[row][COLS - 1] = WALL;
        }
        for (int col = 0; col < COLS; col++) {
            grid[0][col] = WALL;
            grid[ROWS - 1][col] = WALL;
        }
        
        // Create some internal walls to make the maze interesting
        createInternalWalls();
        
        // Fill remaining empty spaces with dots
        for (int row = 1; row < ROWS - 1; row++) {
            for (int col = 1; col < COLS - 1; col++) {
                if (grid[row][col] == EMPTY) {
                    grid[row][col] = DOT;
                }
            }
        }
    }
    
    /**
     * Creates internal walls to form a simple maze pattern.
     */
    private void createInternalWalls() {
        // Horizontal wall segments
        for (int col = 3; col < COLS - 3; col += 5) {
            for (int row = 5; row <= 15; row += 5) {
                if (row < ROWS - 1 && col < COLS - 1) {
                    grid[row][col] = WALL;
                    grid[row][col + 1] = WALL;
                    grid[row][col + 2] = WALL;
                }
            }
        }
        
        // Vertical wall segments
        for (int row = 3; row < ROWS - 3; row += 5) {
            for (int col = 5; col <= 15; col += 5) {
                if (row < ROWS - 1 && col < COLS - 1) {
                    grid[row][col] = WALL;
                }
            }
        }
    }
    
    /**
     * Checks if a position is walkable (not a wall and within bounds).
     * @param row the row position
     * @param col the column position
     * @return true if the position is not a wall and within bounds
     */
    public boolean isWalkable(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return false; // Out of bounds
        }
        return grid[row][col] != WALL;
    }
    
    /**
     * Gets the tile type at a specific position.
     * @param row the row position
     * @param col the column position
     * @return the tile type (EMPTY, WALL, or DOT)
     */
    public int getTile(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return WALL; // Out of bounds treated as wall
        }
        return grid[row][col];
    }
    
    /**
     * Sets a tile at a specific position.
     * @param row the row position
     * @param col the column position
     * @param type the tile type to set
     */
    public void setTile(int row, int col, int type) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            grid[row][col] = type;
        }
    }
    
    public int getRows() {
        return ROWS;
    }
    
    public int getCols() {
        return COLS;
    }
    
    public int getCellSize() {
        return CELL_SIZE;
    }
}
