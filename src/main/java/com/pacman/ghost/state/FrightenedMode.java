package com.pacman.ghost.state;

import com.pacman.core.Game;
import com.pacman.entity.PacMan;
import com.pacman.ghost.Ghost;
import com.pacman.util.Utils;

/**
 * Frightened mode - ghost flees from PacMan and can be eaten.
 */
public class FrightenedMode extends GhostState {
    public FrightenedMode(Ghost ghost) {
        super(ghost);
    }

    @Override
    public void eaten() {
        ghost.switchEatenMode();
    }

    @Override
    public void timerFrightenedModeOver() {
        ghost.switchChaseModeOrScatterMode();
    }

    @Override
    public int[] getTargetPosition() {
        // Flee from PacMan: target the opposite direction
        PacMan pacman = Game.getPacman();
        int[] position = new int[2];
        if (pacman != null) {
            // Vector from PacMan to ghost (flee direction)
            int dx = ghost.getxPos() - pacman.getxPos();
            int dy = ghost.getyPos() - pacman.getyPos();
            // Target a point far away in the flee direction
            position[0] = ghost.getxPos() + dx;
            position[1] = ghost.getyPos() + dy;
        } else {
            // Fallback to random if PacMan not available
            boolean randomAxis = Utils.randomBool();
            position[0] = ghost.getxPos() + (randomAxis ? Utils.randomInt(-1, 1) * 32 : 0);
            position[1] = ghost.getyPos() + (!randomAxis ? Utils.randomInt(-1, 1) * 32 : 0);
        }
        return position;
    }
}
