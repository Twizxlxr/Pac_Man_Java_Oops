package com.pacman.ghost.factory;

import com.pacman.ghost.Blinky;
import com.pacman.ghost.Ghost;

/**
 * Factory for creating Blinky (red ghost).
 */
public class BlinkyFactory extends AbstractGhostFactory {
    @Override
    public Ghost makeGhost(int xPos, int yPos) {
        return new Blinky(xPos, yPos);
    }
}
