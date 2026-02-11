package com.pacman.entity;

import java.awt.*;

/**
 * Power pellet that PacMan can eat for 100 points.
 * Triggers Frightened mode for all ghosts.
 * Blinks on and off for visibility.
 */
public class SuperPacGum extends StaticEntity {
    private int frameCount = 0;

    public SuperPacGum(int xPos, int yPos) {
        super(16, xPos, yPos);
    }

    @Override
    public void render(Graphics2D g) {
        if (frameCount % 60 < 30) {
            g.setColor(new Color(255, 183, 174));
            g.fillOval(this.xPos, this.yPos, this.size, this.size);
        }
    }

    @Override
    public void update() {
        frameCount++;
    }
}
