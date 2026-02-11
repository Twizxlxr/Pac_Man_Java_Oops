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
 * <p><b>Design Pattern:</b> Observer Pattern (as Observer)</p>
 * 
 * <p>This class is the central hub that:</p>
 * <ul>
 *   <li>Loads the level from CSV file</li>
 *   <li>Creates and manages all game entities</li>
 *   <li>Handles game loop updates and rendering</li>
 *   <li>Responds to collision events (pellets eaten, ghost collisions)</li>
 * </ul>
 * 
 * <p><b>CSV Level Format:</b></p>
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
 * <p><b>Observer Callbacks:</b></p>
 * <ul>
 *   <li>{@link #updatePacGumEaten(PacGum)} - Destroys the eaten pellet</li>
 *   <li>{@link #updateSuperPacGumEaten(SuperPacGum)} - Destroys pellet, triggers ghost Frightened mode</li>
 *   <li>{@link #updateGhostCollision(Ghost)} - Game over or ghost eaten</li>
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

    public Game(UIPanel uiPanel) {
        this.uiPanel = uiPanel;
        
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
                if (yy >= data.size() || xx >= data.get(yy).size()) continue;
                
                String dataChar = data.get(yy).get(xx);
                
                if (dataChar.equals("x")) {
                    objects.add(new Wall(xx * cellSize, yy * cellSize));
                } else if (dataChar.equals("P")) {
                    pacman = new PacMan(xx * cellSize, yy * cellSize);
                    pacman.setCollisionDetector(collisionDetector);
                    if (uiPanel != null) pacman.registerObserver(uiPanel);
                    pacman.registerObserver(this);
                } else if (dataChar.equals("b") || dataChar.equals("p") || dataChar.equals("i") || dataChar.equals("c")) {
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

        if (pacman != null) objects.add(pacman);
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
        for (Entity o : objects) {
            if (!o.isDestroyed()) o.update();
        }
    }

    /** Passes keyboard input to PacMan */
    public void input(KeyHandler k) {
        if (pacman != null) pacman.input(k);
    }

    /** Renders all non-destroyed entities */
    public void render(Graphics2D g) {
        for (Entity o : objects) {
            if (!o.isDestroyed()) o.render(g);
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

    // ==================== Observer Callbacks ====================

    /**
     * Called when PacMan eats a regular pellet.
     * Destroys the pellet (score update handled by UIPanel).
     */
    @Override
    public void updatePacGumEaten(PacGum pg) {
        pg.destroy();
    }

    /**
     * Called when PacMan eats a power pellet.
     * Destroys the pellet and triggers Frightened mode for all ghosts.
     */
    @Override
    public void updateSuperPacGumEaten(SuperPacGum spg) {
        spg.destroy();
        // Trigger frightened mode for all ghosts
        for (Ghost gh : ghosts) {
            gh.getState().superPacGumEaten();
        }
    }

    /**
     * Called when PacMan collides with a ghost.
     * If ghost is frightened, ghost gets eaten (+500 points via UIPanel).
     * If ghost is normal/chasing, game over.
     */
    @Override
    public void updateGhostCollision(Ghost gh) {
        if (gh.getState() instanceof FrightenedMode) {
            // Ghost is vulnerable - eat it
            gh.getState().eaten();
        } else if (!(gh.getState() instanceof EatenMode)) {
            // Ghost is not eaten (eyes) - game over
            System.out.println("Game over!\nScore: " + (uiPanel != null ? uiPanel.getScore() : 0));
            System.exit(0);
        }
        // If ghost is in EatenMode (eyes), collision is ignored
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
}
