package com.pacman.entity;

import java.awt.*;

/**
 * Abstract base class for all game entities.
 * 
 * <p><b>Entity Hierarchy:</b></p>
 * <pre>
 * Entity (abstract)
 * ├── StaticEntity (non-moving)
 * │   ├── Wall         - Maze boundaries
 * │   ├── GhostHouse   - Ghost spawn door
 * │   ├── PacGum       - Regular pellet (+10 points)
 * │   └── SuperPacGum  - Power pellet (+100 points, frightens ghosts)
 * │
 * └── MovingEntity (can move)
 *     ├── PacMan       - Player character
 *     └── Ghost        - Enemy characters
 *         ├── Blinky (Red)   - Targets PacMan directly
 *         ├── Pinky (Pink)   - Targets ahead of PacMan
 *         ├── Inky (Cyan)    - Uses Blinky's position
 *         └── Clyde (Orange) - Distance-based targeting
 * </pre>
 * 
 * <p><b>Coordinate System:</b> Position (xPos, yPos) is top-left corner.
 * Size is 8px for static entities, 32px for moving entities.</p>
 * 
 * @see StaticEntity For non-moving entities
 * @see MovingEntity For entities that can move
 */
public abstract class Entity {
    
    /** Entity size in pixels (8px for static, 32px for moving) */
    protected int size;
    
    /** X position (top-left corner) */
    protected int xPos;
    
    /** Y position (top-left corner) */
    protected int yPos;
    
    /** Whether this entity has been destroyed/collected */
    protected boolean destroyed = false;

    /**
     * Creates a new entity at the specified position.
     * @param size Entity size in pixels
     * @param xPos X position (top-left corner)
     * @param yPos Y position (top-left corner)
     */
    public Entity(int size, int xPos, int yPos) {
        this.size = size;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    /**
     * Updates entity state each frame. Override in subclasses.
     */
    public void update() {}
    
    /**
     * Renders the entity. Override in subclasses.
     * @param g Graphics context
     */
    public void render(Graphics2D g) {}

    /**
     * Marks this entity as destroyed and moves it off-screen.
     */
    public void destroy() {
        this.xPos = -32;
        this.yPos = -32;
        destroyed = true;
    }

    // ==================== Getters ====================
    
    public boolean isDestroyed() { return destroyed; }
    public int getSize() { return size; }
    public int getxPos() { return xPos; }
    public int getyPos() { return yPos; }
    public void setxPos(int xPos) { this.xPos = xPos; }
    public void setyPos(int yPos) { this.yPos = yPos; }
    
    /**
     * Returns the collision hitbox for this entity.
     * @return Rectangle representing the hitbox
     */
    public abstract Rectangle getHitbox();
}
