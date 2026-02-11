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
    public static final int CELL_SIZE = 28; // pixels per cell
    public static final int TUNNEL_ROW = 10;
    
    public GameMap() {
        grid = new int[ROWS][COLS];
        initializeMap();
    }
    
    /**
     * Initializes the game map using a classic arcade-style layout.
     * Rendering draws continuous walls; this grid is for collision logic.
     */
    private void initializeMap() {
        // Start with all walls, then carve corridors.
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = WALL;
            }
        }

        // Outer loop corridors (inside the boundary walls)
        carveHorizontal(1, 1, COLS - 2);
        carveHorizontal(ROWS - 2, 1, COLS - 2);
        carveVertical(1, 1, ROWS - 2);
        carveVertical(COLS - 2, 1, ROWS - 2);

        // Long horizontal corridors near top and bottom
        carveHorizontal(4, 2, COLS - 3);
        carveHorizontal(ROWS - 5, 2, COLS - 3);

        // Mid corridors
        carveHorizontal(7, 2, 6);
        carveHorizontal(7, COLS - 7, COLS - 3);
        carveHorizontal(13, 2, 6);
        carveHorizontal(13, COLS - 7, COLS - 3);

        // Central corridor split for ghost box
        carveHorizontal(10, 2, 7);
        carveHorizontal(10, COLS - 8, COLS - 3);

        // Symmetrical vertical connectors
        carveVertical(4, 4, 7);
        carveVertical(COLS - 5, 4, 7);
        carveVertical(4, 13, ROWS - 5);
        carveVertical(COLS - 5, 13, ROWS - 5);

        carveVertical(9, 4, 8);
        carveVertical(9, 12, ROWS - 5);

        // Side tunnels (connect left and right edges)
        grid[10][0] = EMPTY;
        grid[10][COLS - 1] = EMPTY;

        // Central ghost box
        createGhostBox(9, 7, 3, 5);

        // Fill remaining empty spaces with dots, excluding ghost box interior
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] == EMPTY && !isGhostBoxInterior(row, col)) {
                    grid[row][col] = DOT;
                }
            }
        }
    }

    private void carveHorizontal(int row, int colStart, int colEnd) {
        for (int col = colStart; col <= colEnd; col++) {
            grid[row][col] = EMPTY;
        }
    }

    private void carveVertical(int col, int rowStart, int rowEnd) {
        for (int row = rowStart; row <= rowEnd; row++) {
            grid[row][col] = EMPTY;
        }
    }

    private void createGhostBox(int topRow, int leftCol, int height, int width) {
        int bottomRow = topRow + height - 1;
        int rightCol = leftCol + width - 1;

        // Box walls
        for (int col = leftCol; col <= rightCol; col++) {
            grid[topRow][col] = WALL;
            grid[bottomRow][col] = WALL;
        }
        for (int row = topRow; row <= bottomRow; row++) {
            grid[row][leftCol] = WALL;
            grid[row][rightCol] = WALL;
        }

        // Opening at the top center
        int openingCol = leftCol + width / 2;
        grid[topRow][openingCol] = EMPTY;

        // Interior space
        for (int row = topRow + 1; row <= bottomRow - 1; row++) {
            for (int col = leftCol + 1; col <= rightCol - 1; col++) {
                grid[row][col] = EMPTY;
            }
        }
    }

    private boolean isGhostBoxInterior(int row, int col) {
        return row >= 10 && row <= 11 && col >= 8 && col <= 10;
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
