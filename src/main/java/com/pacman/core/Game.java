package com.pacman.core;

import com.pacman.entity.*;
import com.pacman.ghost.Blinky;
import com.pacman.ghost.Ghost;
import com.pacman.ghost.factory.*;
import com.pacman.ghost.state.EatenMode;
import com.pacman.ghost.state.FrightenedMode;
import com.pacman.util.CollisionDetector;
import com.pacman.util.CsvReader;
import com.pacman.util.KeyHandler;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main game controller managing all entities and game logic.
 * 
 * <p>
 * <b>Design Pattern:</b> Observer Pattern (as Observer)
 * </p>
 * 
 * <p>
 * This class is the central hub that:
 * </p>
 * <ul>
 * <li>Loads the level from CSV file</li>
 * <li>Creates and manages all game entities</li>
 * <li>Handles game loop updates and rendering</li>
 * <li>Responds to collision events (pellets eaten, ghost collisions)</li>
 * </ul>
 * 
 * <p>
 * <b>CSV Level Format:</b>
 * </p>
 * 
 * <pre>
 * Symbol | Entity Created
 * -------|---------------
 *   x    | Wall
 *   .    | PacGum (pellet, +10 points)
 *   o    | SuperPacGum (power pellet, +100 points)
 *   -    | GhostHouse (door ghosts can pass through)
 *   P    | PacMan spawn position
 *   b    | Blinky (red ghost) spawn
 *   p    | Pinky (pink ghost) spawn
 *   i    | Inky (cyan ghost) spawn
 *   c    | Clyde (orange ghost) spawn
 * </pre>
 * 
 * <p>
 * <b>Observer Callbacks:</b>
 * </p>
 * <ul>
 * <li>{@link #updatePacGumEaten(PacGum)} - Destroys the eaten pellet</li>
 * <li>{@link #updateSuperPacGumEaten(SuperPacGum)} - Destroys pellet, triggers
 * ghost Frightened mode</li>
 * <li>{@link #updateGhostCollision(Ghost)} - Game over or ghost eaten</li>
 * </ul>
 * 
 * @see Observer The interface this class implements
 * @see UIPanel Score display that also observes events
 */
public class Game implements Observer {

    /** All game entities (walls, pellets, ghosts, pacman) */
    private List<Entity> objects = new ArrayList<>();

    /** Quick reference to all ghosts for mode switching */
    private List<Ghost> ghosts = new ArrayList<>();

    /** Quick reference to walls for collision detection */
    private static List<Wall> walls = new ArrayList<>();

    /** Player character instance */
    private static PacMan pacman;

    /** Reference to Blinky (used by Inky's strategy) */
    private static Blinky blinky;

    /** Flag indicating if player has made first input */
    private static boolean firstInput = false;

    /** UI panel for score display */
    private UIPanel uiPanel;

    /** Spawn positions for reset */
    private int pacmanSpawnX, pacmanSpawnY;
    private int[] ghostSpawnX, ghostSpawnY;

    /** Game over flag */
    private static boolean gameOver = false;

    /** Game won flag */
    private static boolean gameWon = false;

    /** Grace period in frames after game restart to prevent immediate collisions */
    private static int graceFrames = 0;

    /**
     * Tracks consecutive ghosts eaten during one power pellet for escalating bonus
     */
    private int ghostsEatenThisPower = 0;

    /** Ghost release timing */
    private long lastGhostReleaseTime = 0;
    private int ghostReleaseIndex = 0;
    private boolean ghostsReleasedAtStart = false;

    public Game(UIPanel uiPanel) {
        this.uiPanel = uiPanel;
        graceFrames = 2; // Set grace period to prevent immediate collisions
        ghostSpawnX = new int[4];
        ghostSpawnY = new int[4];
        int ghostIndex = 0;

        // Load level from CSV
        List<List<String>> data = null;
        try {
            File csvFile = new File("level.csv");
            data = new CsvReader().parseCsv(csvFile.toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (data == null || data.isEmpty()) {
            System.err.println("Failed to load level.csv");
            return;
        }

        int cellsPerRow = data.get(0).size();
        int cellsPerColumn = data.size();
        int cellSize = 8;

        CollisionDetector collisionDetector = new CollisionDetector(this);
        AbstractGhostFactory abstractGhostFactory = null;

        // Parse CSV and create entities
        for (int xx = 0; xx < cellsPerRow; xx++) {
            for (int yy = 0; yy < cellsPerColumn; yy++) {
                if (yy >= data.size() || xx >= data.get(yy).size())
                    continue;

                String dataChar = data.get(yy).get(xx);

                if (dataChar.equals("x")) {
                    objects.add(new Wall(xx * cellSize, yy * cellSize));
                } else if (dataChar.equals("P")) {
                    pacmanSpawnX = xx * cellSize;
                    pacmanSpawnY = yy * cellSize;
                    pacman = new PacMan(pacmanSpawnX, pacmanSpawnY);
                    pacman.setCollisionDetector(collisionDetector);
                    if (uiPanel != null)
                        pacman.registerObserver(uiPanel);
                    pacman.registerObserver(this);
                } else if (dataChar.equals("b") || dataChar.equals("p") || dataChar.equals("i")
                        || dataChar.equals("c")) {
                    if (ghostIndex < 4) {
                        ghostSpawnX[ghostIndex] = xx * cellSize;
                        ghostSpawnY[ghostIndex] = yy * cellSize;
                    }
                    switch (dataChar) {
                        case "b":
                            abstractGhostFactory = new BlinkyFactory();
                            break;
                        case "p":
                            abstractGhostFactory = new PinkyFactory();
                            break;
                        case "i":
                            abstractGhostFactory = new InkyFactory();
                            break;
                        case "c":
                            abstractGhostFactory = new ClydeFactory();
                            break;
                    }
                    Ghost ghost = abstractGhostFactory.makeGhost(xx * cellSize, yy * cellSize);
                    ghosts.add(ghost);
                    ghostIndex++;
                    if (dataChar.equals("b")) {
                        blinky = (Blinky) ghost;
                    }
                } else if (dataChar.equals(".")) {
                    objects.add(new PacGum(xx * cellSize, yy * cellSize));
                } else if (dataChar.equals("o")) {
                    objects.add(new SuperPacGum(xx * cellSize, yy * cellSize));
                } else if (dataChar.equals("-")) {
                    objects.add(new GhostHouse(xx * cellSize, yy * cellSize));
                }
            }
        }

        if (pacman != null)
            objects.add(pacman);
        objects.addAll(ghosts);

        for (Entity o : objects) {
            if (o instanceof Wall) {
                walls.add((Wall) o);
            }
        }
    }

    public static List<Wall> getWalls() {
        return walls;
    }

    public List<Entity> getEntities() {
        return objects;
    }

    public void update() {
        // Decrement grace period frames
        if (graceFrames > 0) {
            graceFrames--;
        }

        // Release ghosts sequentially after first input with 3-second delay
        if (!ghostsReleasedAtStart && getFirstInput()) {
            long currentTime = System.currentTimeMillis();

            // Initialize release timer on first input
            if (ghostReleaseIndex == 0 && lastGhostReleaseTime == 0) {
                lastGhostReleaseTime = currentTime;
            }
            // Release ghosts every 3 seconds (including first ghost)
            else if (ghostReleaseIndex < ghosts.size() && (currentTime - lastGhostReleaseTime) >= 3000) {
                Ghost ghost = ghosts.get(ghostReleaseIndex);
                ghost.getState().outsideHouse();
                ghostReleaseIndex++;
                lastGhostReleaseTime = currentTime;
            }

            // Mark release sequence complete when all ghosts released
            if (ghostReleaseIndex >= ghosts.size()) {
                ghostsReleasedAtStart = true;
            }
        }

        for (Entity o : objects) {
            if (!o.isDestroyed())
                o.update();
        }
    }

    /** Passes keyboard input to PacMan */
    public void input(KeyHandler k) {
        if (pacman != null)
            pacman.input(k);
    }

    /** Renders all non-destroyed entities */
    public void render(Graphics2D g) {
        for (Entity o : objects) {
            if (!o.isDestroyed())
                o.render(g);
        }
    }

    // ==================== Static Getters ====================

    /** Returns PacMan instance (used by ghost strategies) */
    public static PacMan getPacman() {
        return pacman;
    }

    /** Returns Blinky instance (used by Inky's strategy) */
    public static Blinky getBlinky() {
        return blinky;
    }

    /** Returns true if game is in grace period (no ghost collisions allowed) */
    public static boolean isInGracePeriod() {
        return graceFrames > 0;
    }

    // ==================== Observer Callbacks ====================

    /**
     * Called when PacMan eats a regular pellet.
     * Destroys the pellet (score update handled by UIPanel).
     */
    @Override
    public void updatePacGumEaten(PacGum pg) {
        pg.destroy();
        checkWinCondition();
    }

    /**
     * Called when PacMan eats a power pellet.
     * Destroys the pellet and triggers Frightened mode for all ghosts.
     * Resets the escalating ghost bonus counter.
     */
    @Override
    public void updateSuperPacGumEaten(SuperPacGum spg) {
        spg.destroy();
        checkWinCondition();
        // Reset escalating bonus counter
        ghostsEatenThisPower = 0;
        // Trigger frightened mode for all ghosts
        for (Ghost gh : ghosts) {
            gh.getState().superPacGumEaten();
        }
    }

    /** Checks if player has won: all pellets eaten */
    private void checkWinCondition() {
        boolean allPelletsEaten = true;
        // Check for remaining PacGum or SuperPacGum
        for (Entity e : objects) {
            if (!e.isDestroyed() && (e instanceof PacGum || e instanceof SuperPacGum)) {
                allPelletsEaten = false;
                break;
            }
        }
        if (allPelletsEaten) {
            gameWon = true;
            if (uiPanel != null)
                uiPanel.repaint();
            System.out.println("You win! All pellets cleared.");
            advanceToNextLevel();
        }
    }

    /**
     * Called when PacMan collides with a ghost.
     * If ghost is frightened, ghost gets eaten with escalating bonus
     * (200→400→800→1600).
     * If ghost is normal/chasing, lose a life or game over.
     */
    @Override
    public void updateGhostCollision(Ghost gh) {
        if (gh.getState() instanceof FrightenedMode) {
            // Ghost is vulnerable - eat it with escalating bonus
            ghostsEatenThisPower++;
            int bonus = 200 * (int) Math.pow(2, ghostsEatenThisPower - 1);
            gh.getState().eaten();
            if (uiPanel != null) {
                uiPanel.updateScore(bonus);
            }
            System.out.println("Ghost eaten! Bonus: " + bonus + " (" + ghostsEatenThisPower + " ghosts this power)");
            checkWinCondition();
        } else if (!(gh.getState() instanceof EatenMode)) {
            // Ghost is not eaten (eyes) - lose a life
            if (uiPanel != null) {
                uiPanel.loseLife();
                if (uiPanel.isGameOver()) {
                    gameOver = true;
                    System.out.println("Game over!\nScore: " + uiPanel.getScore());
                } else {
                    // Reset positions
                    resetPositions();
                }
            }
        }
        // If ghost is in EatenMode (eyes), collision is ignored
    }

    /** Resets PacMan and ghosts to spawn positions after losing a life */
    private void resetPositions() {
        // Reset PacMan
        if (pacman != null) {
            pacman.setxPos(pacmanSpawnX);
            pacman.setyPos(pacmanSpawnY);
            pacman.setxSpd(0);
            pacman.setySpd(0);
        }

        // Reset ghosts
        for (int i = 0; i < ghosts.size() && i < 4; i++) {
            Ghost ghost = ghosts.get(i);
            ghost.setxPos(ghostSpawnX[i]);
            ghost.setyPos(ghostSpawnY[i]);
            ghost.setxSpd(0);
            ghost.setySpd(0);
            ghost.switchHouseMode();
        }

        // Reset ghost release timing
        lastGhostReleaseTime = 0;
        ghostReleaseIndex = 0;
        ghostsReleasedAtStart = false;
        // Reset first input flag so ghosts wait again
        firstInput = false;
    }

    // ==================== First Input Flag ====================

    /** Sets the first input flag (ghosts start moving after first input) */
    public static void setFirstInput(boolean b) {
        firstInput = b;
    }

    /** Returns true if player has made first input */
    public static boolean getFirstInput() {
        return firstInput;
    }

    /** Returns true if game is over */
    public static boolean isGameOver() {
        return gameOver;
    }

    /** Returns true if player won */
    public static boolean isGameWon() {
        return gameWon;
    }

    /** Resets the game over flag for restarting */
    public static void resetGameOver() {
        gameOver = false;
        gameWon = false;
        firstInput = false;
        walls.clear();
    }

    /** Advances to next level: increases speeds and resets game state */
    private void advanceToNextLevel() {
        // Advance level in configuration (increases multipliers and point values)
        LevelConfig.nextLevel();

        // Update PacMan speed for new level
        if (pacman != null) {
            pacman.updateSpeedForLevel();
        }

        // Update speed for all ghosts
        for (Ghost gh : ghosts) {
            gh.updateSpeedForLevel();
        }

        // Reset pellets and ghosts
        resetLevelEntities();
        gameWon = false;
        if (uiPanel != null)
            uiPanel.resetForNextLevel();
    }

    /** Resets pellets and ghosts for new level */
    private void resetLevelEntities() {
        // Restore all pellets
        for (Entity e : objects) {
            if (e instanceof PacGum || e instanceof SuperPacGum) {
                e.setDestroyed(false);
                e.setxPos(e.getSpawnX());
                e.setyPos(e.getSpawnY());
            }
        }
        // Restore all ghosts
        for (int i = 0; i < ghosts.size() && i < 4; i++) {
            Ghost ghost = ghosts.get(i);
            ghost.setxPos(ghostSpawnX[i]);
            ghost.setyPos(ghostSpawnY[i]);
            ghost.setxSpd(0);
            ghost.setySpd(0);
            ghost.switchHouseMode();
            ghost.setDestroyed(false);
        }
        // Reset ghost release timing
        lastGhostReleaseTime = 0;
        ghostReleaseIndex = 0;
        ghostsReleasedAtStart = false;
        // Reset first input flag so ghosts wait again
        firstInput = false;
    }
}
