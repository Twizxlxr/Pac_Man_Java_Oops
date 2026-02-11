package com.pacman.ghost.state;

import com.pacman.ghost.Ghost;
import com.pacman.util.Utils;
import com.pacman.util.WallCollisionDetector;

/**
 * Eaten mode - ghost returns to the house after being eaten.
 */
public class EatenMode extends GhostState {
    public EatenMode(Ghost ghost) {
        super(ghost);
    }

    @Override
    public void insideHouse() {
        ghost.switchHouseMode();
    }

    @Override
    public int[] getTargetPosition() {
        int[] position = new int[2];
        position[0] = 208;
        position[1] = 200;
        return position;
    }

    @Override
    public void computeNextDir() {
        int new_xSpd = 0;
        int new_ySpd = 0;

        if (!ghost.onTheGrid()) return;
        if (!ghost.onGameplayWindow()) return;

        double minDist = Double.MAX_VALUE;

        if (ghost.getxSpd() <= 0 && !WallCollisionDetector.checkWallCollision(ghost, -ghost.getSpd(), 0, true)) {
            double distance = Utils.getDistance(ghost.getxPos() - ghost.getSpd(), ghost.getyPos(), getTargetPosition()[0], getTargetPosition()[1]);
            if (distance < minDist) {
                new_xSpd = -ghost.getSpd();
                new_ySpd = 0;
                minDist = distance;
            }
        }
        if (ghost.getxSpd() >= 0 && !WallCollisionDetector.checkWallCollision(ghost, ghost.getSpd(), 0, true)) {
            double distance = Utils.getDistance(ghost.getxPos() + ghost.getSpd(), ghost.getyPos(), getTargetPosition()[0], getTargetPosition()[1]);
            if (distance < minDist) {
                new_xSpd = ghost.getSpd();
                new_ySpd = 0;
                minDist = distance;
            }
        }
        if (ghost.getySpd() <= 0 && !WallCollisionDetector.checkWallCollision(ghost, 0, -ghost.getSpd(), true)) {
            double distance = Utils.getDistance(ghost.getxPos(), ghost.getyPos() - ghost.getSpd(), getTargetPosition()[0], getTargetPosition()[1]);
            if (distance < minDist) {
                new_xSpd = 0;
                new_ySpd = -ghost.getSpd();
                minDist = distance;
            }
        }
        if (ghost.getySpd() >= 0 && !WallCollisionDetector.checkWallCollision(ghost, 0, ghost.getSpd(), true)) {
            double distance = Utils.getDistance(ghost.getxPos(), ghost.getyPos() + ghost.getSpd(), getTargetPosition()[0], getTargetPosition()[1]);
            if (distance < minDist) {
                new_xSpd = 0;
                new_ySpd = ghost.getSpd();
                minDist = distance;
            }
        }

        if (new_xSpd == 0 && new_ySpd == 0) return;

        if (Math.abs(new_xSpd) != Math.abs(new_ySpd)) {
            ghost.setxSpd(new_xSpd);
            ghost.setySpd(new_ySpd);
        } else {
            if (ghost.getxSpd() != 0) {
                ghost.setxSpd(0);
                ghost.setySpd(new_ySpd);
            } else {
                ghost.setxSpd(new_xSpd);
                ghost.setySpd(0);
            }
        }
    }
}
