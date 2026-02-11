package com.pacman.ghost;

import com.pacman.core.Game;
import com.pacman.ghost.strategy.InkyStrategy;

/**
 * Inky (Cyan Ghost) - "Bashful"
 * Uses Blinky's position to calculate flanking target.
 */
public class Inky extends Ghost {
    public Inky(int xPos, int yPos) {
        super(xPos, yPos, "inky.png");
        setStrategy(new InkyStrategy(Game.getBlinky()));
    }
}
