package com.pacman.entity;

import java.awt.*;

/**
 * Base class for non-moving game entities.
 * 
 * <p>Static entities have fixed positions and 8x8 pixel size.
 * They are typically collected or act as collision boundaries.</p>
 * 
 * <p><b>Subclasses:</b></p>
 * <ul>
 *   <li>{@link Wall} - Maze boundaries (collision blocks movement)</li>
 *   <li>{@link GhostHouse} - Ghost spawn door (ghosts can pass through)</li>
 *   <li>{@link PacGum} - Regular pellet (10 points when eaten)</li>
 *   <li>{@link SuperPacGum} - Power pellet (100 points, triggers frightened mode)</li>
 * </ul>
 * 
 * @see Entity Parent class
 * @see MovingEntity For entities that can move
 */
public abstract class StaticEntity extends Entity {
    
    /** Collision hitbox for this entity */
    protected Rectangle hitbox;

    /**
     * Creates a static entity at the specified position.
     * @param size Entity size in pixels (typically 8)
     * @param xPos X position
     * @param yPos Y position
     */
    public StaticEntity(int size, int xPos, int yPos) {
        super(size, xPos, yPos);
        this.hitbox = new Rectangle(xPos, yPos, size, size);
    }

    @Override
    public Rectangle getHitbox() { return hitbox; }
}
