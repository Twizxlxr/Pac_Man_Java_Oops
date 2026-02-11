package com.pacman.ghost;

import com.pacman.ghost.strategy.ClydeStrategy;

/**
 * Clyde (Orange Ghost) - "Pokey"
 * Shy ghost that retreats when close to PacMan.
 */
public class Clyde extends Ghost {
    public Clyde(int xPos, int yPos) {
        super(xPos, yPos, "clyde.png");
        setStrategy(new ClydeStrategy(this));
    }
}
