package com.pacman.entity;

import java.awt.*;

/**
 * Regular pellet that PacMan can eat for 10 points.
 */
public class PacGum extends StaticEntity {
    public PacGum(int xPos, int yPos) {
        super(4, xPos + 8, yPos + 8);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(new Color(255, 183, 174));
        g.fillRect(xPos, yPos, size, size);
    }
}
