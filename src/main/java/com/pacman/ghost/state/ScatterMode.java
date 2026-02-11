package com.pacman.ghost.state;

import com.pacman.ghost.Ghost;

/**
 * Scatter mode - ghost moves to its corner using strategy.
 */
public class ScatterMode extends GhostState {
    public ScatterMode(Ghost ghost) {
        super(ghost);
    }

    @Override
    public void superPacGumEaten() {
        ghost.switchFrightenedMode();
    }

    @Override
    public void timerModeOver() {
        ghost.switchChaseMode();
    }

    @Override
    public int[] getTargetPosition() {
        return ghost.getStrategy().getScatterTargetPosition();
    }
}
