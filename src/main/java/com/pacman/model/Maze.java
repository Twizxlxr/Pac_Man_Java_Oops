package com.pacman.model;

import java.awt.Point;
import java.io.*;
import java.util.*;

/**
 * Maze loads and manages the game maze from a CSV file.
 * Supports loading maze layouts with walls, pellets, power pellets, and ghost/Pac-Man spawn points.
 */
public class Maze {

    private char[][] grid;
    private int rows;
    private int cols;

    public Point pacmanSpawn;
    public Map<String, Point> ghostSpawns = new HashMap<>();

    public Maze(String filePath) {
        loadCSV(filePath);
    }

    /**
     * Loads the maze from a CSV file.
     * File format:
     * x = walls
     * (space) = empty walkable space
     * . = regular pellets
     * o = super pellets (power pellets)
     * b, p, i, c = ghost spawn positions (Blinky, Pinky, Inky, Clyde)
     * - = ghost house door
     * P = Pac-Man spawn position
     */
    private void loadCSV(String filePath) {
        List<char[]> tempGrid = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int row = 0;
            int maxCols = 0;

            while ((line = br.readLine()) != null) {
                char[] rowData = line.toCharArray();
                maxCols = Math.max(maxCols, rowData.length);

                for (int col = 0; col < rowData.length; col++) {
                    char tile = rowData[col];

                    switch (tile) {
                        case 'b':
                            ghostSpawns.put("blinky", new Point(col, row));
                            rowData[col] = ' ';
                            break;
                        case 'p':
                            ghostSpawns.put("pinky", new Point(col, row));
                            rowData[col] = ' ';
                            break;
                        case 'i':
                            ghostSpawns.put("inky", new Point(col, row));
                            rowData[col] = ' ';
                            break;
                        case 'c':
                            ghostSpawns.put("clyde", new Point(col, row));
                            rowData[col] = ' ';
                            break;
                        case 'P':
                            pacmanSpawn = new Point(col, row);
                            rowData[col] = ' ';
                            break;
                    }
                }

                tempGrid.add(rowData);
                row++;
            }

            if (tempGrid.isEmpty()) {
                System.err.println("Maze file is empty!");
                return;
            }

            rows = tempGrid.size();
            cols = maxCols;
            grid = new char[rows][cols];

            // Fill grid with spaces first
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    grid[r][c] = ' ';
                }
            }

            // Copy data from tempGrid
            for (int r = 0; r < rows; r++) {
                char[] rowData = tempGrid.get(r);
                for (int c = 0; c < rowData.length; c++) {
                    grid[r][c] = rowData[c];
                }
            }

        } catch (IOException e) {
            System.err.println("Error loading maze from " + filePath);
            e.printStackTrace();
        }

        System.out.println("Maze loaded: " + rows + " rows, " + cols + " cols");
        System.out.println("Pac-Man spawn: " + pacmanSpawn);
        System.out.println("Ghost spawns: " + ghostSpawns);
    }

    /**
     * Checks if a position is a wall.
     * @param row the row position
     * @param col the column position
     * @return true if the tile is a wall
     */
    public boolean isWall(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return true; // Out of bounds treated as wall
        }
        return grid[row][col] == 'x';
    }

    /**
     * Checks if a position has a pellet.
     * @param row the row position
     * @param col the column position
     * @return true if the tile has a pellet
     */
    public boolean hasPellet(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        return grid[row][col] == '.';
    }

    /**
     * Checks if a position has a power pellet.
     * @param row the row position
     * @param col the column position
     * @return true if the tile has a power pellet
     */
    public boolean hasPowerPellet(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        return grid[row][col] == 'o';
    }

    /**
     * Gets the tile character at a specific position.
     * @param row the row position
     * @param col the column position
     * @return the tile character
     */
    public char getTile(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return 'x'; // Out of bounds treated as wall
        }
        return grid[row][col];
    }

    /**
     * Sets a tile at a specific position.
     * @param row the row position
     * @param col the column position
     * @param tile the tile character to set
     */
    public void setTile(int row, int col, char tile) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            grid[row][col] = tile;
        }
    }

    /**
     * Checks if a position is walkable (not a wall).
     * @param row the row position
     * @param col the column position
     * @return true if the position is walkable
     */
    public boolean isWalkable(int row, int col) {
        return !isWall(row, col);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
