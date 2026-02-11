package com.pacman.ghost.strategy;

import com.pacman.core.Game;
import com.pacman.util.Utils;

/**
 * Pinky's strategy - targets 4 tiles ahead of PacMan for ambush.
 * Scatter: top-left corner.
 */
public class PinkyStrategy implements IGhostStrategy {
    @Override
    public int[] getChaseTargetPosition() {
        int[] position = new int[2];
        int[] pacmanFacingPosition = Utils.getPointDistanceDirection(
            Game.getPacman().getxPos(), 
            Game.getPacman().getyPos(), 
            64, 
            Utils.directionConverter(Game.getPacman().getDirection())
        );
        position[0] = pacmanFacingPosition[0];
        position[1] = pacmanFacingPosition[1];
        return position;
    }

    @Override
    public int[] getScatterTargetPosition() {
        int[] position = new int[2];
        position[0] = 0;
        position[1] = 0;
        return position;
    }
}
