package com.pacman.ghost;

import com.pacman.ghost.strategy.BlinkyStrategy;

/**
 * Blinky (Red Ghost) - "Shadow"
 * Directly pursues PacMan's current position.
 */
public class Blinky extends Ghost {
    public Blinky(int xPos, int yPos) {
        super(xPos, yPos, "blinky.png");
        setStrategy(new BlinkyStrategy());
    }
}
