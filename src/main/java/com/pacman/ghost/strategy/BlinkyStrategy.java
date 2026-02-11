package com.pacman.ghost.strategy;

import com.pacman.core.Game;
import com.pacman.ui.GamePanel;

/**
 * Blinky's strategy - directly pursues PacMan.
 * Scatter: top-right corner.
 */
public class BlinkyStrategy implements IGhostStrategy {
    @Override
    public int[] getChaseTargetPosition() {
        int[] position = new int[2];
        position[0] = Game.getPacman().getxPos();
        position[1] = Game.getPacman().getyPos();
        return position;
    }

    @Override
    public int[] getScatterTargetPosition() {
        int[] position = new int[2];
        position[0] = GamePanel.width;
        position[1] = 0;
        return position;
    }
}
