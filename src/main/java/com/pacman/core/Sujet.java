package com.pacman.core;

import com.pacman.entity.PacGum;
import com.pacman.entity.SuperPacGum;
import com.pacman.ghost.Ghost;

/**
 * Subject interface for the Observer Design Pattern.
 * 
 * <p><b>Design Pattern:</b> Observer Pattern (Subject/Publisher)</p>
 * 
 * <p>Known in French as "Sujet" (Subject). Classes implementing this interface
 * can register observers and notify them of game events.</p>
 * 
 * <p><b>Implementation:</b> {@link com.pacman.entity.PacMan} is the primary subject,
 * as it detects collisions with pellets and ghosts.</p>
 * 
 * <p><b>Event Flow:</b></p>
 * <pre>
 * PacMan (Sujet) --notifies--> Observer(s)
 *                              ├── Game (updates entity states)
 *                              └── UIPanel (updates score display)
 * </pre>
 * 
 * @see Observer The Observer interface that receives notifications
 * @see com.pacman.entity.PacMan The main implementation of this interface
 */
public interface Sujet {
    
    /**
     * Registers an observer to receive game event notifications.
     * @param observer The observer to register
     */
    void registerObserver(Observer observer);
    
    /**
     * Removes an observer from the notification list.
     * @param observer The observer to remove
     */
    void removeObserver(Observer observer);
    
    /**
     * Notifies all observers that a PacGum was eaten.
     * @param pg The PacGum that was eaten
     */
    void notifyObserverPacGumEaten(PacGum pg);
    
    /**
     * Notifies all observers that a SuperPacGum was eaten.
     * @param spg The SuperPacGum that was eaten
     */
    void notifyObserverSuperPacGumEaten(SuperPacGum spg);
    
    /**
     * Notifies all observers of a ghost collision.
     * @param gh The Ghost that was collided with
     */
    void notifyObserverGhostCollision(Ghost gh);
}
