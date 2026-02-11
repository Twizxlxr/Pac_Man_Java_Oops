package com.pacman.ghost;

import com.pacman.ghost.strategy.PinkyStrategy;

/**
 * Pinky (Pink Ghost) - "Speedy"
 * Targets 4 tiles ahead of PacMan for ambush attacks.
 */
public class Pinky extends Ghost {
    public Pinky(int xPos, int yPos) {
        super(xPos, yPos, "pinky.png");
        setStrategy(new PinkyStrategy());
    }
}
