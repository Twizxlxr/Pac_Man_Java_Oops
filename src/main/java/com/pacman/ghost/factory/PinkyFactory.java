package com.pacman.ghost.factory;

import com.pacman.ghost.Ghost;
import com.pacman.ghost.Pinky;

/**
 * Factory for creating Pinky (pink ghost).
 */
public class PinkyFactory extends AbstractGhostFactory {
    @Override
    public Ghost makeGhost(int xPos, int yPos) {
        return new Pinky(xPos, yPos);
    }
}
