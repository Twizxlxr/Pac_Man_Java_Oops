package com.pacman.entity;

/**
 * Ghost house door entity.
 * Ghosts can pass through when exiting or entering the house.
 */
public class GhostHouse extends Wall {
    public GhostHouse(int xPos, int yPos) {
        super(xPos, yPos);
    }
}
