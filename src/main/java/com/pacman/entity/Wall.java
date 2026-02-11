package com.pacman.entity;

/**
 * Wall entity representing maze boundaries.
 * Blocks movement for both PacMan and Ghosts.
 */
public class Wall extends StaticEntity {
    public Wall(int xPos, int yPos) {
        super(8, xPos, yPos);
    }
}
