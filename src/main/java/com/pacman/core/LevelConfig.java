package com.pacman.core;

/**
 * Manages level-based configuration and difficulty scaling.
 * 
 * <p>This class provides centralized control over:</p>
 * <ul>
 *   <li>Current level number</li>
 *   <li>Ghost speed multiplier (increases 10% per level)</li>
 *   <li>PacMan speed multiplier (increases 5% per level)</li>
 *   <li>Point values for pellets, power pellets, and eaten ghosts</li>
 * </ul>
 * 
 * <p>Speed scales as: Base Speed * Multiplier</p>
 * <p>Points scale using moderate formula: Base Points + (Level - 1) * Increment</p>
 */
public class LevelConfig {
    
    /** Current game level */
    private static int currentLevel = 1;
    
    /** Speed multiplier for ghosts (increases 10% per level) */
    private static float ghostSpeedMultiplier = 1.0f;
    
    /** Speed multiplier for PacMan (increases 5% per level) */
    private static float pacmanSpeedMultiplier = 1.0f;
    
    /** Base points for regular pellet */
    private static final int BASE_PACGUM_POINTS = 10;
    
    /** Base points for power pellet */
    private static final int BASE_SUPER_PACGUM_POINTS = 100;
    
    /** Base points for eating a ghost */
    private static final int BASE_GHOST_POINTS = 500;
    
    /** Increment per level for regular pellet (moderate scaling) */
    private static final int PACGUM_INCREMENT = 2;
    
    /** Increment per level for power pellet (moderate scaling) */
    private static final int SUPER_PACGUM_INCREMENT = 20;
    
    /** Increment per level for ghost points (moderate scaling) */
    private static final int GHOST_INCREMENT = 100;
    
    /**
     * Advances to the next level.
     * Updates speed multipliers and point values.
     */
    public static void nextLevel() {
        currentLevel++;
        
        // Ghost speed increases by 10% per level
        ghostSpeedMultiplier = 1.0f + (currentLevel - 1) * 0.1f;
        
        // PacMan speed increases by 5% per level
        pacmanSpeedMultiplier = 1.0f + (currentLevel - 1) * 0.05f;
        
        System.out.println("Advanced to Level " + currentLevel + 
                          " - Ghost Speed: " + String.format("%.2f", ghostSpeedMultiplier) + 
                          "x, PacMan Speed: " + String.format("%.2f", pacmanSpeedMultiplier) + "x");
    }
    
    /**
     * Resets configuration to Level 1.
     * Called when player restarts the game.
     */
    public static void resetToLevel1() {
        currentLevel = 1;
        ghostSpeedMultiplier = 1.0f;
        pacmanSpeedMultiplier = 1.0f;
    }
    
    /**
     * Gets the current level number.
     * @return current level (1-indexed)
     */
    public static int getCurrentLevel() {
        return currentLevel;
    }
    
    /**
     * Gets the ghost speed multiplier for the current level.
     * @return speed multiplier (e.g., 1.1 for 10% faster)
     */
    public static float getGhostSpeedMultiplier() {
        return ghostSpeedMultiplier;
    }
    
    /**
     * Gets the PacMan speed multiplier for the current level.
     * @return speed multiplier (e.g., 1.05 for 5% faster)
     */
    public static float getPacmanSpeedMultiplier() {
        return pacmanSpeedMultiplier;
    }
    
    /**
     * Gets the point value for a regular pellet at current level.
     * Uses moderate scaling: 10 + (level - 1) * 2
     * @return points for eating a regular pellet
     */
    public static int getPacGumPoints() {
        return BASE_PACGUM_POINTS + (currentLevel - 1) * PACGUM_INCREMENT;
    }
    
    /**
     * Gets the point value for a power pellet at current level.
     * Uses moderate scaling: 100 + (level - 1) * 20
     * @return points for eating a power pellet
     */
    public static int getSuperPacGumPoints() {
        return BASE_SUPER_PACGUM_POINTS + (currentLevel - 1) * SUPER_PACGUM_INCREMENT;
    }
    
    /**
     * Gets the point value for eating a ghost at current level.
     * Uses moderate scaling: 500 + (level - 1) * 100
     * @return points for eating a frightened ghost
     */
    public static int getGhostPoints() {
        return BASE_GHOST_POINTS + (currentLevel - 1) * GHOST_INCREMENT;
    }
    
    /**
     * Gets difficulty description for UI display.
     * @return formatted string showing difficulty multiplier
     */
    public static String getDifficultyDescription() {
        float avgMultiplier = (ghostSpeedMultiplier + pacmanSpeedMultiplier) / 2;
        return String.format("Level: %d (%.1fx)", currentLevel, avgMultiplier);
    }
}
