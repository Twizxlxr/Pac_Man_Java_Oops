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
    public static final int GATE = 3; // Ghost house gate
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

        // Set the ghost house gate
        grid[8][9] = GATE;

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
     * Creates a classic Pac-Man arcade maze with:
     * - Outer boundary loop
     * - Central ghost spawn box
     * - Long horizontal corridors
     * - Vertical connectors
     * - Side tunnels
     * - Symmetrical layout
     */
    private void createInternalWalls() {
        // ===== OUTER LOOP =====
        // Already created by border walls

        // ===== CENTRAL GHOST SPAWN BOX =====
        // 4x4 box in center (rows 9-10, cols 8-10)
        for (int row = 9; row <= 10; row++) {
            for (int col = 8; col <= 10; col++) {
                grid[row][col] = EMPTY; // Clear for ghost spawn area
            }
        }
        // Draw box walls
        for (int col = 7; col <= 11; col++) {
            grid[8][col] = WALL;
            grid[11][col] = WALL;
        }
        for (int row = 8; row <= 11; row++) {
            grid[row][7] = WALL;
            grid[row][11] = WALL;
        }

        // ===== LEFT SIDE VERTICAL CORRIDORS =====
        createSymmetricalPattern(2, 16);

        // ===== RIGHT SIDE VERTICAL CORRIDORS =====
        // Mirror pattern on right (done by symmetrical function)

        // ===== HORIZONTAL CORRIDORS =====
        // Top horizontal corridor (row 5)
        createHorizontalCorridor(5, 2, 8);
        createHorizontalCorridor(5, 10, 16);

        // Middle-upper corridor (row 9)
        createHorizontalCorridor(9, 1, 7);
        createHorizontalCorridor(9, 11, 17);

        // Middle-lower corridor (row 11)
        createHorizontalCorridor(11, 1, 7);
        createHorizontalCorridor(11, 11, 17);

        // Bottom corridor (row 16)
        createHorizontalCorridor(16, 2, 8);
        createHorizontalCorridor(16, 10, 16);

        // ===== VERTICAL CONNECTORS =====
        createVerticalCorridor(2, 3, 4); // Left
        createVerticalCorridor(2, 14, 16); // Right

        createVerticalCorridor(7, 3, 4);
        createVerticalCorridor(7, 14, 16);

        createVerticalCorridor(12, 3, 4);
        createVerticalCorridor(12, 14, 16);

        createVerticalCorridor(17, 3, 4);
        createVerticalCorridor(17, 14, 16);

        // ===== LEFT AND RIGHT SIDE TUNNELS =====
        // Left tunnel (open ends for wrap-around)
        for (int row = 9; row <= 11; row++) {
            grid[row][1] = EMPTY;
        }

        // Right tunnel mirror
        for (int row = 9; row <= 11; row++) {
            grid[row][17] = EMPTY;
        }

        // ===== INTERNAL MAZE PATTERNS =====
        // Top-left quadrant walls
        createMazeBlock(3, 7, 4, 7);
        createMazeBlock(3, 7, 7, 5);

        // Top-right quadrant walls (mirror)
        createMazeBlock(3, 11, 4, 11);
        createMazeBlock(3, 11, 7, 13);

        // Bottom-left quadrant walls
        createMazeBlock(16, 7, 17, 7);
        createMazeBlock(16, 7, 13, 5);

        // Bottom-right quadrant walls (mirror)
        createMazeBlock(16, 11, 17, 11);
        createMazeBlock(16, 11, 13, 13);
    }

    /**
     * Creates a horizontal corridor (empty spaces between walls).
     * 
     * @param row      the row for the corridor
     * @param colStart starting column
     * @param colEnd   ending column
     */
    private void createHorizontalCorridor(int row, int colStart, int colEnd) {
        for (int col = colStart; col <= colEnd; col++) {
            grid[row][col] = EMPTY;
        }
    }

    /**
     * Creates a vertical corridor.
     * 
     * @param col      the column for the corridor
     * @param rowStart starting row
     * @param rowEnd   ending row
     */
    private void createVerticalCorridor(int col, int rowStart, int rowEnd) {
        for (int row = rowStart; row <= rowEnd; row++) {
            grid[row][col] = EMPTY;
        }
    }

    /**
     * Creates maze wall blocks for internal pattern.
     * 
     * @param row1 first row
     * @param col1 first column
     * @param row2 second row
     * @param col2 second column
     */
    private void createMazeBlock(int row1, int col1, int row2, int col2) {
        int minRow = Math.min(row1, row2);
        int maxRow = Math.max(row1, row2);
        int minCol = Math.min(col1, col2);
        int maxCol = Math.max(col1, col2);

        for (int row = minRow; row <= maxRow; row++) {
            if (row >= 0 && row < ROWS) {
                for (int col = minCol; col <= maxCol; col++) {
                    if (col >= 0 && col < COLS && grid[row][col] != EMPTY) {
                        grid[row][col] = WALL;
                    }
                }
            }
        }
    }

    /**
     * Creates a symmetrical pattern for maze structure.
     * 
     * @param colStart starting column for left side
     * @param colEnd   ending column for left side
     */
    private void createSymmetricalPattern(int colStart, int colEnd) {
        // Create walls in multiple columns for visual maze pattern
        for (int row = 3; row < ROWS - 3; row += 3) {
            for (int col = colStart; col <= (colStart + 2); col++) {
                if (grid[row][col] != EMPTY) {
                    grid[row][col] = WALL;
                }
            }
            for (int col = (colEnd - 2); col <= colEnd; col++) {
                if (grid[row][col] != EMPTY) {
                    grid[row][col] = WALL;
                }
            }
        }
    }

    /**
     * Checks if a position is walkable (not a wall and within bounds).
     * 
     * @param row the row position
     * @param col the column position
     * @return true if the position is not a wall and within bounds
     */
    /**
     * Checks if a position is walkable.
     * by default assumes it's Pac-Man (cannot pass gate).
     * 
     * @param row the row position
     * @param col the column position
     * @return true if walkable
     */
    public boolean isWalkable(int row, int col) {
        return isWalkable(row, col, false);
    }

    /**
     * Checks if a position is walkable for a specific entity.
     * 
     * @param row     the row position
     * @param col     the column position
     * @param isGhost true if checking for a ghost, false for Pac-Man
     * @return true if the position is not a wall and within bounds
     */
    public boolean isWalkable(int row, int col, boolean isGhost) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return false; // Out of bounds
        }

        int tile = grid[row][col];

        if (tile == WALL) {
            return false;
        }

        if (tile == GATE) {
            return isGhost; // Only ghosts can pass the gate
        }

        return true;
    }

    /**
     * Gets the tile type at a specific position.
     * 
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
     * 
     * @param row  the row position
     * @param col  the column position
     * @param type the tile type to set
     */
    public void setTile(int row, int col, int type) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            grid[row][col] = type;
        }
    }

    /**
     * Tries to eat a dot at the specified position.
     * 
     * @param row the row position
     * @param col the column position
     * @return true if a dot was eaten, false otherwise
     */
    public boolean eatDot(int row, int col) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            if (grid[row][col] == DOT) {
                grid[row][col] = EMPTY;
                return true;
            }
        }
        return false;
    }

    /**
     * Counts the remaining dots on the map.
     * 
     * @return the number of dots remaining
     */
    public int countDots() {
        int count = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] == DOT) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Resets all empty spaces back to dots for a new level.
     * Preserves walls, gate, and ghost spawn area.
     */
    public void resetDots() {
        for (int row = 1; row < ROWS - 1; row++) {
            for (int col = 1; col < COLS - 1; col++) {
                if (grid[row][col] == EMPTY) {
                    grid[row][col] = DOT;
                }
            }
        }
        // Ensure ghost spawn area stays empty
        for (int r = 9; r <= 10; r++) {
            for (int c = 8; c <= 10; c++) {
                grid[r][c] = EMPTY;
            }
        }
        // Ensure gate stays as gate
        grid[8][9] = GATE;
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
