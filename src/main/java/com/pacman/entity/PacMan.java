package com.pacman.entity;

import com.pacman.core.Game;
import com.pacman.core.Observer;
import com.pacman.core.Sujet;
import com.pacman.ghost.Ghost;
import com.pacman.util.CollisionDetector;
import com.pacman.util.KeyHandler;
import com.pacman.util.WallCollisionDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * The player character class implementing the Subject pattern.
 * Notifies observers when eating pellets or colliding with ghosts.
 */
public class PacMan extends MovingEntity implements Sujet {
    private CollisionDetector collisionDetector;
    private List<Observer> observerCollection;

    public PacMan(int xPos, int yPos) {
        super(32, xPos, yPos, 2, "pacman.png", 4, 0.3f);
        observerCollection = new ArrayList<>();
    }

    public void input(KeyHandler k) {
        int new_xSpd = 0;
        int new_ySpd = 0;

        if (!onTheGrid()) return;
        if (!onGameplayWindow()) return;

        if (k.k_left.isPressed && xSpd >= 0 && !WallCollisionDetector.checkWallCollision(this, -spd, 0)) {
            new_xSpd = -spd;
        }
        if (k.k_right.isPressed && xSpd <= 0 && !WallCollisionDetector.checkWallCollision(this, spd, 0)) {
            new_xSpd = spd;
        }
        if (k.k_up.isPressed && ySpd >= 0 && !WallCollisionDetector.checkWallCollision(this, 0, -spd)) {
            new_ySpd = -spd;
        }
        if (k.k_down.isPressed && ySpd <= 0 && !WallCollisionDetector.checkWallCollision(this, 0, spd)) {
            new_ySpd = spd;
        }

        if (new_xSpd == 0 && new_ySpd == 0) return;

        if (!Game.getFirstInput()) Game.setFirstInput(true);

        if (Math.abs(new_xSpd) != Math.abs(new_ySpd)) {
            xSpd = new_xSpd;
            ySpd = new_ySpd;
        } else {
            if (xSpd != 0) {
                xSpd = 0;
                ySpd = new_ySpd;
            } else {
                xSpd = new_xSpd;
                ySpd = 0;
            }
        }
    }

    @Override
    public void update() {
        if (collisionDetector != null) {
            PacGum pg = (PacGum) collisionDetector.checkCollision(this, PacGum.class);
            if (pg != null) {
                notifyObserverPacGumEaten(pg);
            }

            SuperPacGum spg = (SuperPacGum) collisionDetector.checkCollision(this, SuperPacGum.class);
            if (spg != null) {
                notifyObserverSuperPacGumEaten(spg);
            }

            Ghost gh = (Ghost) collisionDetector.checkCollision(this, Ghost.class);
            if (gh != null) {
                notifyObserverGhostCollision(gh);
            }
        }

        if (!WallCollisionDetector.checkWallCollision(this, xSpd, ySpd)) {
            updatePosition();
        }
    }

    public void setCollisionDetector(CollisionDetector collisionDetector) {
        this.collisionDetector = collisionDetector;
    }

    @Override
    public void registerObserver(Observer observer) {
        observerCollection.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observerCollection.remove(observer);
    }

    @Override
    public void notifyObserverPacGumEaten(PacGum pg) {
        observerCollection.forEach(obs -> obs.updatePacGumEaten(pg));
    }

    @Override
    public void notifyObserverSuperPacGumEaten(SuperPacGum spg) {
        observerCollection.forEach(obs -> obs.updateSuperPacGumEaten(spg));
    }

    @Override
    public void notifyObserverGhostCollision(Ghost gh) {
        observerCollection.forEach(obs -> obs.updateGhostCollision(gh));
    }
}
