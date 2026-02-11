package com.pacman.ghost.state;

import com.pacman.ghost.Ghost;
import com.pacman.util.Utils;
import com.pacman.util.WallCollisionDetector;

/**
 * Abstract base class for ghost behavioral states.
 * 
 * <p><b>Design Pattern:</b> State Pattern</p>
 * 
 * <p>Ghosts change behavior based on their current state. This pattern allows
 * the ghost to change its behavior at runtime without large conditionals.</p>
 * 
 * <p><b>State Diagram:</b></p>
 * <pre>
 *                    ┌─────────────┐
 *                    │  HouseMode  │ (Start)
 *                    └──────┬──────┘
 *                           │ exit house
 *                           ▼
 *           ┌────────────────────────────┐
 *           │                            │
 *     ┌─────┴──────┐    timer    ┌──────┴───────┐
 *     │  ChaseMode  │◄──────────►│ ScatterMode │
 *     └──────┬──────┘    timer    └──────┬───────┘
 *            │                            │
 *            └────────────────────────────┘
 *                           │
 *                           │ SuperPacGum eaten
 *                           ▼
 *                ┌────────────────┐
 *                │ FrightenedMode │ (random movement)
 *                └────────┬───────┘
 *                         │ eaten by PacMan
 *                         ▼
 *                  ┌────────────┐
 *                  │ EatenMode  │ (return to house)
 *                  └──────┬─────┘
 *                         │ enter house
 *                         ▼
 *                    HouseMode
 * </pre>
 * 
 * <p><b>Pathfinding:</b> The {@link #computeNextDir()} method uses shortest-distance
 * pathfinding to choose the next direction toward the target position.</p>
 * 
 * @see ChaseMode Pursues PacMan using ghost's strategy
 * @see ScatterMode Moves to corner using ghost's strategy
 * @see FrightenedMode Random movement, can be eaten
 * @see EatenMode Returns to ghost house
 * @see HouseMode Waiting/exiting ghost house
 */
public abstract class GhostState {
    
    /** The ghost this state belongs to */
    protected Ghost ghost;

    public GhostState(Ghost ghost) {
        this.ghost = ghost;
    }

    // ==================== State Transition Hooks ====================
    
    /** Called when SuperPacGum is eaten - transition to FrightenedMode */
    public void superPacGumEaten() {}
    
    /** Called when chase/scatter timer expires - switch modes */
    public void timerModeOver() {}
    
    /** Called when frightened timer expires - return to chase/scatter */
    public void timerFrightenedModeOver() {}
    
    /** Called when ghost is eaten by PacMan - transition to EatenMode */
    public void eaten() {}
    
    /** Called when ghost exits the house - transition to Chase/Scatter */
    public void outsideHouse() {}
    
    /** Called when ghost enters the house - transition to HouseMode */
    public void insideHouse() {}

    // ==================== Targeting ====================
    
    /**
     * Returns the target position for pathfinding.
     * Override in subclasses to provide state-specific targets.
     * @return int[2] with {x, y} target coordinates
     */
    public int[] getTargetPosition() {
        return new int[2];
    }

    /**
     * Computes the next direction using shortest-distance pathfinding.
     * 
     * <p>Algorithm: For each valid direction (no wall, not reversing),
     * calculate distance to target and choose the direction with
     * minimum distance.</p>
     */
    public void computeNextDir() {
        int new_xSpd = 0;
        int new_ySpd = 0;

        if (!ghost.onTheGrid()) return;
        if (!ghost.onGameplayWindow()) return;

        double minDist = Double.MAX_VALUE;

        if (ghost.getxSpd() <= 0 && !WallCollisionDetector.checkWallCollision(ghost, -ghost.getSpd(), 0)) {
            double distance = Utils.getDistance(ghost.getxPos() - ghost.getSpd(), ghost.getyPos(), getTargetPosition()[0], getTargetPosition()[1]);
            if (distance < minDist) {
                new_xSpd = -ghost.getSpd();
                new_ySpd = 0;
                minDist = distance;
            }
        }

        if (ghost.getxSpd() >= 0 && !WallCollisionDetector.checkWallCollision(ghost, ghost.getSpd(), 0)) {
            double distance = Utils.getDistance(ghost.getxPos() + ghost.getSpd(), ghost.getyPos(), getTargetPosition()[0], getTargetPosition()[1]);
            if (distance < minDist) {
                new_xSpd = ghost.getSpd();
                new_ySpd = 0;
                minDist = distance;
            }
        }

        if (ghost.getySpd() <= 0 && !WallCollisionDetector.checkWallCollision(ghost, 0, -ghost.getSpd())) {
            double distance = Utils.getDistance(ghost.getxPos(), ghost.getyPos() - ghost.getSpd(), getTargetPosition()[0], getTargetPosition()[1]);
            if (distance < minDist) {
                new_xSpd = 0;
                new_ySpd = -ghost.getSpd();
                minDist = distance;
            }
        }

        if (ghost.getySpd() >= 0 && !WallCollisionDetector.checkWallCollision(ghost, 0, ghost.getSpd())) {
            double distance = Utils.getDistance(ghost.getxPos(), ghost.getyPos() + ghost.getSpd(), getTargetPosition()[0], getTargetPosition()[1]);
            if (distance < minDist) {
                new_xSpd = 0;
                new_ySpd = ghost.getSpd();
                minDist = distance;
            }
        }

        if (new_xSpd == 0 && new_ySpd == 0) return;

        if (Math.abs(new_xSpd) != Math.abs(new_ySpd)) {
            ghost.setxSpd(new_xSpd);
            ghost.setySpd(new_ySpd);
        } else {
            if (new_xSpd != 0) {
                ghost.setxSpd(0);
                ghost.setySpd(new_ySpd);
            } else {
                ghost.setxSpd(new_xSpd);
                ghost.setySpd(0);
            }
        }
    }
}
