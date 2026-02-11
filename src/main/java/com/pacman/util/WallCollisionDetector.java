package com.pacman.util;

import com.pacman.core.Game;
import com.pacman.entity.Entity;
import com.pacman.entity.GhostHouse;
import com.pacman.entity.Wall;

import java.awt.*;

/**
 * Detects collisions with walls for pathfinding.
 */
public class WallCollisionDetector {
    public static boolean checkWallCollision(Entity obj, int dx, int dy) {
        Rectangle r = new Rectangle(obj.getxPos() + dx, obj.getyPos() + dy, obj.getSize(), obj.getSize());
        for (Wall w : Game.getWalls()) {
            if (w.getHitbox().intersects(r)) return true;
        }
        return false;
    }

    public static boolean checkWallCollision(Entity obj, int dx, int dy, boolean ignoreGhostHouses) {
        Rectangle r = new Rectangle(obj.getxPos() + dx, obj.getyPos() + dy, obj.getSize(), obj.getSize());
        for (Wall w : Game.getWalls()) {
            if (!(ignoreGhostHouses && w instanceof GhostHouse) && w.getHitbox().intersects(r)) return true;
        }
        return false;
    }
}
