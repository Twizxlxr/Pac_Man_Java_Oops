package com.pacman.core;

import com.pacman.entity.PacGum;
import com.pacman.entity.SuperPacGum;
import com.pacman.ghost.Ghost;

/**
 * Observer interface for the Observer Design Pattern.
 * 
 * <p><b>Design Pattern:</b> Observer Pattern</p>
 * 
 * <p>Classes implementing this interface receive notifications when game events occur:</p>
 * <ul>
 *   <li>PacMan eats a PacGum (pellet) - triggers score increase</li>
 *   <li>PacMan eats a SuperPacGum (power pellet) - triggers frightened mode for ghosts</li>
 *   <li>PacMan collides with a ghost - triggers game over or ghost eaten</li>
 * </ul>
 * 
 * <p><b>Implementations:</b></p>
 * <ul>
 *   <li>{@link Game} - Updates game state (destroys pellets, triggers ghost modes)</li>
 *   <li>{@link UIPanel} - Updates score display</li>
 * </ul>
 * 
 * @see Sujet The Subject interface that notifies observers
 * @see Game Main game controller implementing this interface
 * @see UIPanel Score panel implementing this interface
 */
public interface Observer {
    
    /**
     * Called when PacMan eats a regular pellet.
     * @param pg The PacGum that was eaten
     */
    void updatePacGumEaten(PacGum pg);
    
    /**
     * Called when PacMan eats a power pellet.
     * @param spg The SuperPacGum that was eaten
     */
    void updateSuperPacGumEaten(SuperPacGum spg);
    
    /**
     * Called when PacMan collides with a ghost.
     * @param gh The Ghost that was collided with
     */
    void updateGhostCollision(Ghost gh);
}
