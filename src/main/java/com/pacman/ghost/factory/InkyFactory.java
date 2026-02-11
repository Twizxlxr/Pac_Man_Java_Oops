package com.pacman.ghost.factory;

import com.pacman.ghost.Ghost;
import com.pacman.ghost.Inky;

/**
 * Factory for creating Inky (cyan ghost).
 */
public class InkyFactory extends AbstractGhostFactory {
    @Override
    public Ghost makeGhost(int xPos, int yPos) {
        return new Inky(xPos, yPos);
    }
}
