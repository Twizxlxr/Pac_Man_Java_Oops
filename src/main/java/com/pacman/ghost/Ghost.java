package com.pacman.ghost;

import com.pacman.core.Game;
import com.pacman.entity.MovingEntity;
import com.pacman.ghost.state.*;
import com.pacman.ghost.strategy.IGhostStrategy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Abstract base class for all ghost enemies.
 * 
 * <p><b>Design Patterns Used:</b></p>
 * <ul>
 *   <li><b>State Pattern:</b> Ghost behavior changes based on current state
 *       (Chase, Scatter, Frightened, Eaten, House)</li>
 *   <li><b>Strategy Pattern:</b> Each ghost subclass has unique targeting
 *       behavior via {@link IGhostStrategy}</li>
 * </ul>
 * 
 * <p><b>Ghost Types:</b></p>
 * <ul>
 *   <li>{@link Blinky} (Red) - "Shadow" - Directly pursues PacMan</li>
 *   <li>{@link Pinky} (Pink) - "Speedy" - Targets 4 tiles ahead of PacMan</li>
 *   <li>{@link Inky} (Cyan) - "Bashful" - Uses Blinky's position to flank</li>
 *   <li>{@link Clyde} (Orange) - "Pokey" - Shy, retreats when close</li>
 * </ul>
 * 
 * <p><b>State Transitions:</b></p>
 * <pre>
 * HouseMode -> ScatterMode <-> ChaseMode (timer-based, 7s/20s cycles)
 *                    |             |
 *                    v             v
 *              FrightenedMode (SuperPacGum eaten, 7s duration)
 *                    |
 *                    v
 *               EatenMode (eaten by PacMan, returns to house)
 *                    |
 *                    v
 *                HouseMode
 * </pre>
 * 
 * <p><b>Timers:</b></p>
 * <ul>
 *   <li>Mode Timer: Alternates between Chase (20s) and Scatter (7s)</li>
 *   <li>Frightened Timer: 7 seconds of vulnerability after SuperPacGum</li>
 * </ul>
 * 
 * @see GhostState Base class for ghost states
 * @see IGhostStrategy Strategy interface for targeting
 */
public abstract class Ghost extends MovingEntity {
    
    /** Current behavioral state */
    protected GhostState state;

    // ==================== Ghost States (State Pattern) ====================
    protected final GhostState chaseMode;
    protected final GhostState scatterMode;
    protected final GhostState frightenedMode;
    protected final GhostState eatenMode;
    protected final GhostState houseMode;

    /** Timer for Chase/Scatter mode switching */
    protected int modeTimer = 0;
    
    /** Timer for Frightened mode duration */
    protected int frightenedTimer = 0;
    
    /** True when in Chase mode, false when in Scatter mode */
    protected boolean isChasing = false;

    // ==================== Shared Sprites ====================
    protected static BufferedImage frightenedSprite1;
    protected static BufferedImage frightenedSprite2;
    protected static BufferedImage eatenSprite;

    /** Targeting strategy (Strategy Pattern) */
    protected IGhostStrategy strategy;

    /**
     * Creates a ghost at the specified position.
     * @param xPos X spawn position
     * @param yPos Y spawn position
     * @param spriteName Sprite sheet filename (e.g., "blinky.png")
     */
    public Ghost(int xPos, int yPos, String spriteName) {
        super(32, xPos, yPos, 2, spriteName, 2, 0.1f);

        // Initialize all state objects (State Pattern)
        chaseMode = new ChaseMode(this);
        scatterMode = new ScatterMode(this);
        frightenedMode = new FrightenedMode(this);
        eatenMode = new EatenMode(this);
        houseMode = new HouseMode(this);

        // Ghosts start inside the house
        state = houseMode;

        // Load shared sprites (only once for all ghosts)
        try {
            frightenedSprite1 = ImageIO.read(new File("ghost_frightened.png"));
            frightenedSprite2 = ImageIO.read(new File("ghost_frightened_2.png"));
            eatenSprite = ImageIO.read(new File("ghost_eaten.png"));
        } catch (IOException e) {
            System.err.println("Could not load ghost sprites");
        }
    }

    // ==================== State Transitions ====================
    
    /** Switch to Chase mode (pursuing PacMan) */
    public void switchChaseMode() { state = chaseMode; }
    
    /** Switch to Scatter mode (targeting corner) */
    public void switchScatterMode() { state = scatterMode; }
    
    /** Switch to Frightened mode (vulnerable, random movement) */
    public void switchFrightenedMode() { frightenedTimer = 0; state = frightenedMode; }
    
    /** Switch to Eaten mode (returning to house) */
    public void switchEatenMode() { state = eatenMode; }
    
    /** Switch to House mode (inside ghost house) */
    public void switchHouseMode() { state = houseMode; }

    /** Switch to Chase or Scatter based on current mode flag */
    public void switchChaseModeOrScatterMode() {
        if (isChasing) switchChaseMode();
        else switchScatterMode();
    }

    // ==================== Getters & Setters ====================
    
    public IGhostStrategy getStrategy() { return this.strategy; }
    public void setStrategy(IGhostStrategy strategy) { this.strategy = strategy; }
    public GhostState getState() { return state; }

    // ==================== Game Loop ====================

    /**
     * Updates ghost state, timers, and position each frame.
     * Handles speed reduction for FrightenedMode using a temporary speed variable.
     */
    @Override
    public void update() {
        // Don't move until player makes first input
        if (!Game.getFirstInput()) return;

        // Save original speed
        int originalSpd = 2;
        if (state == frightenedMode) {
            // Reduce speed only in FrightenedMode
            if (spd != 1) spd = 1;
            frightenedTimer++;
            if (frightenedTimer >= (60 * 7)) { // 7 seconds
                state.timerFrightenedModeOver();
            }
        } else {
            // Restore normal speed if not frightened
            if (spd != originalSpd) spd = originalSpd;
        }

        // Chase/Scatter mode timer - alternate between modes
        if (state == chaseMode || state == scatterMode) {
            modeTimer++;
            // Chase: 20s, Scatter: 5s (using 7s in spec but 5s in code)
            if ((isChasing && modeTimer >= (60 * 20)) || (!isChasing && modeTimer >= (60 * 5))) {
                state.timerModeOver();
                isChasing = !isChasing;
            }
        }

        // Check ghost house entry/exit positions
        if (xPos == 208 && yPos == 168) {
            state.outsideHouse(); // Just exited ghost house
        }
        if (xPos == 208 && yPos == 200) {
            state.insideHouse(); // Just entered ghost house
        }

        // Compute next direction and move
        state.computeNextDir();
        updatePosition();
    }

    /**
     * Renders the ghost with appropriate sprite based on state.
     * 
     * <p>Sprites used:</p>
     * <ul>
     *   <li>Normal: Ghost's own colored sprite</li>
     *   <li>Frightened: Blue/white sprite (flashing when ending)</li>
     *   <li>Eaten: Eyes-only sprite</li>
     * </ul>
     */
    @Override
    public void render(Graphics2D g) {
        if (state == frightenedMode) {
            // Blue sprite, flashes white in last 2 seconds
            if (frightenedSprite1 != null && (frightenedTimer <= (60 * 5) || frightenedTimer % 20 > 10)) {
                g.drawImage(frightenedSprite1.getSubimage((int)subimage * size, 0, size, size), this.xPos, this.yPos, null);
            } else if (frightenedSprite2 != null) {
                g.drawImage(frightenedSprite2.getSubimage((int)subimage * size, 0, size, size), this.xPos, this.yPos, null);
            }
        } else if (state == eatenMode) {
            // Eyes only - directional sprite
            if (eatenSprite != null) {
                g.drawImage(eatenSprite.getSubimage(direction * size, 0, size, size), this.xPos, this.yPos, null);
            }
        } else {
            if (sprite != null) {
                g.drawImage(sprite.getSubimage((int)subimage * size + direction * size * nbSubimagesPerCycle, 0, size, size), this.xPos, this.yPos, null);
            }
        }
    }
}
