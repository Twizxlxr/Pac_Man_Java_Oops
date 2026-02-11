package com.pacman.ghost.strategy;

import com.pacman.core.Game;
import com.pacman.ghost.Ghost;
import com.pacman.ui.GamePanel;
import com.pacman.util.Utils;

/**
 * Clyde's strategy - shy ghost that retreats when close to PacMan.
 * Scatter: bottom-left corner.
 */
public class ClydeStrategy implements IGhostStrategy {
    private Ghost ghost;
    
    public ClydeStrategy(Ghost ghost) {
        this.ghost = ghost;
    }

    @Override
    public int[] getChaseTargetPosition() {
        if (Utils.getDistance(ghost.getxPos(), ghost.getyPos(), 
            Game.getPacman().getxPos(), Game.getPacman().getyPos()) >= 256) {
            int[] position = new int[2];
            position[0] = Game.getPacman().getxPos();
            position[1] = Game.getPacman().getyPos();
            return position;
        } else {
            return getScatterTargetPosition();
        }
    }

    @Override
    public int[] getScatterTargetPosition() {
        int[] position = new int[2];
        position[0] = 0;
        position[1] = GamePanel.height;
        return position;
    }
}
