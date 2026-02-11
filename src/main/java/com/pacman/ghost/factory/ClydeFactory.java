package com.pacman.ghost.factory;

import com.pacman.ghost.Clyde;
import com.pacman.ghost.Ghost;

/**
 * Factory for creating Clyde (orange ghost).
 */
public class ClydeFactory extends AbstractGhostFactory {
    @Override
    public Ghost makeGhost(int xPos, int yPos) {
        return new Clyde(xPos, yPos);
    }
}
