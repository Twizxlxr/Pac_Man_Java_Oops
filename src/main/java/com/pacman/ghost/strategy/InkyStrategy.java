package com.pacman.ghost.strategy;

import com.pacman.core.Game;
import com.pacman.ghost.Ghost;
import com.pacman.ui.GamePanel;
import com.pacman.util.Utils;

/**
 * Inky's strategy - uses Blinky to calculate flanking position.
 * Scatter: bottom-right corner.
 */
public class InkyStrategy implements IGhostStrategy {
    private Ghost otherGhost;
    
    public InkyStrategy(Ghost ghost) {
        this.otherGhost = ghost;
    }

    @Override
    public int[] getChaseTargetPosition() {
        int[] position = new int[2];
        int[] pacmanFacingPosition = Utils.getPointDistanceDirection(
            Game.getPacman().getxPos(), 
            Game.getPacman().getyPos(), 
            32d, 
            Utils.directionConverter(Game.getPacman().getDirection())
        );
        double distanceOtherGhost = Utils.getDistance(
            pacmanFacingPosition[0], pacmanFacingPosition[1], 
            otherGhost.getxPos(), otherGhost.getyPos()
        );
        double directionOtherGhost = Utils.getDirection(
            otherGhost.getxPos(), otherGhost.getyPos(), 
            pacmanFacingPosition[0], pacmanFacingPosition[1]
        );
        int[] blinkyVectorPosition = Utils.getPointDistanceDirection(
            pacmanFacingPosition[0], pacmanFacingPosition[1], 
            distanceOtherGhost, directionOtherGhost
        );
        position[0] = blinkyVectorPosition[0];
        position[1] = blinkyVectorPosition[1];
        return position;
    }

    @Override
    public int[] getScatterTargetPosition() {
        int[] position = new int[2];
        position[0] = GamePanel.width;
        position[1] = GamePanel.height;
        return position;
    }
}
