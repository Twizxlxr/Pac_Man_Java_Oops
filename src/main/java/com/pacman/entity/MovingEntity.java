package com.pacman.entity;

import com.pacman.ui.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Base class for entities that can move (PacMan and Ghosts).
 * 
 * <p>Moving entities have:</p>
 * <ul>
 *   <li>32x32 pixel sprites with animation frames</li>
 *   <li>Speed values for X and Y movement</li>
 *   <li>Direction tracking (0=right, 1=left, 2=up, 3=down)</li>
 *   <li>Sprite sheet animation with configurable frame count</li>
 *   <li>Screen wrap-around at maze boundaries</li>
 * </ul>
 * 
 * <p><b>Sprite Sheet Format:</b></p>
 * <pre>
 * |frame0|frame1|frame2|frame3| ... (repeated for each direction)
 * |------ direction 0 (right) ------||------ direction 1 (left) ------|...
 * </pre>
 * 
 * <p><b>Subclasses:</b></p>
 * <ul>
 *   <li>{@link PacMan} - Player character</li>
 *   <li>{@link com.pacman.ghost.Ghost} - Enemy characters</li>
 * </ul>
 * 
 * @see Entity Parent class
 * @see StaticEntity Alternative for non-moving entities
 */
public abstract class MovingEntity extends Entity {
    
    /** Base movement speed (pixels per frame) */
    protected int spd;
    
    /** Current X velocity */
    protected int xSpd = 0;
    
    /** Current Y velocity */
    protected int ySpd = 0;
    
    /** Sprite sheet image */
    protected BufferedImage sprite;
    
    /** Current animation frame (float for smooth animation) */
    protected float subimage = 0;
    
    /** Number of animation frames per direction */
    protected int nbSubimagesPerCycle;
    
    /** Current direction: 0=right, 1=left, 2=up, 3=down */
    protected int direction = 0;
    
    /** Animation speed (frames advanced per game tick) */
    protected float imageSpd = 0.2f;

    /**
     * Creates a moving entity with sprite animation.
     * @param size Entity size in pixels (typically 32)
     * @param xPos Initial X position
     * @param yPos Initial Y position
     * @param spd Movement speed in pixels per frame
     * @param spriteName Filename of sprite sheet
     * @param nbSubimagesPerCycle Animation frames per direction
     * @param imageSpd Animation speed multiplier
     */
    public MovingEntity(int size, int xPos, int yPos, int spd, String spriteName, int nbSubimagesPerCycle, float imageSpd) {
        super(size, xPos, yPos);
        this.spd = spd;
        try {
            this.sprite = ImageIO.read(new File(spriteName));
            this.nbSubimagesPerCycle = nbSubimagesPerCycle;
            this.imageSpd = imageSpd;
        } catch (IOException e) {
            System.err.println("Could not load sprite: " + spriteName);
        }
    }

    @Override
    public void update() {
        updatePosition();
    }

    /**
     * Updates position based on current velocity and handles animation.
     * Also handles screen wrap-around at maze boundaries.
     */
    public void updatePosition() {
        if (!(xSpd == 0 && ySpd == 0)) {
            xPos += xSpd;
            yPos += ySpd;

            // Update direction based on velocity
            if (xSpd > 0) direction = 0;      // Right
            else if (xSpd < 0) direction = 1; // Left
            else if (ySpd < 0) direction = 2; // Up
            else if (ySpd > 0) direction = 3; // Down

            // Advance animation frame
            subimage += imageSpd;
            if (subimage >= nbSubimagesPerCycle) subimage = 0;
        }

        // Screen wrap-around (tunnel effect)
        if (xPos > GamePanel.width) xPos = 0 - size + spd;
        if (xPos < 0 - size + spd) xPos = GamePanel.width;
        if (yPos > GamePanel.height) yPos = 0 - size + spd;
        if (yPos < 0 - size + spd) yPos = GamePanel.height;
    }

    @Override
    public void render(Graphics2D g) {
        if (sprite != null && sprite.getWidth() >= (int)subimage * size + direction * size * nbSubimagesPerCycle + size) {
            g.drawImage(sprite.getSubimage((int)subimage * size + direction * size * nbSubimagesPerCycle, 0, size, size), this.xPos, this.yPos, null);
        }
    }

    // ==================== Grid & Bounds Checks ====================
    
    /** Returns true if entity is aligned to the 8px grid (required for turning) */
    public boolean onTheGrid() { return (xPos % 8 == 0 && yPos % 8 == 0); }
    
    /** Returns true if entity is within the gameplay area */
    public boolean onGameplayWindow() { return !(xPos <= 0 || xPos >= GamePanel.width || yPos <= 0 || yPos >= GamePanel.height); }
    
    @Override
    public Rectangle getHitbox() { return new Rectangle(xPos, yPos, size, size); }

    // ==================== Getters & Setters ====================
    public BufferedImage getSprite() { return sprite; }
    public void setSprite(BufferedImage sprite) { this.sprite = sprite; }
    public void setSprite(String spriteName) {
        try { this.sprite = ImageIO.read(new File(spriteName)); } 
        catch (IOException e) { e.printStackTrace(); }
    }
    public float getSubimage() { return subimage; }
    public void setSubimage(float subimage) { this.subimage = subimage; }
    public int getNbSubimagesPerCycle() { return nbSubimagesPerCycle; }
    public void setNbSubimagesPerCycle(int n) { this.nbSubimagesPerCycle = n; }
    public int getDirection() { return direction; }
    public void setDirection(int direction) { this.direction = direction; }
    public int getxSpd() { return xSpd; }
    public void setxSpd(int xSpd) { this.xSpd = xSpd; }
    public int getySpd() { return ySpd; }
    public void setySpd(int ySpd) { this.ySpd = ySpd; }
    public int getSpd() { return spd; }
}
