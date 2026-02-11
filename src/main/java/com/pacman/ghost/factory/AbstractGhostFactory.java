package com.pacman.ghost.factory;

import com.pacman.ghost.Ghost;

/**
 * Abstract factory for creating Ghost instances.
 * 
 * <p><b>Design Pattern:</b> Abstract Factory Pattern</p>
 * 
 * <p>Each ghost type has its own concrete factory that creates the appropriate
 * ghost with its unique strategy (targeting behavior).</p>
 * 
 * <p><b>Concrete Factories:</b></p>
 * <ul>
 *   <li>{@link BlinkyFactory} - Creates Blinky (red ghost)</li>
 *   <li>{@link PinkyFactory} - Creates Pinky (pink ghost)</li>
 *   <li>{@link InkyFactory} - Creates Inky (cyan ghost)</li>
 *   <li>{@link ClydeFactory} - Creates Clyde (orange ghost)</li>
 * </ul>
 * 
 * <p><b>Usage in Game.java:</b></p>
 * <pre>
 * AbstractGhostFactory factory = new BlinkyFactory();
 * Ghost ghost = factory.makeGhost(x, y);
 * </pre>
 * 
 * @see Ghost The product created by factories
 * @see com.pacman.ghost.strategy.IGhostStrategy Strategy assigned to each ghost
 */
public abstract class AbstractGhostFactory {
    
    /**
     * Creates a ghost at the specified position.
     * @param xPos X spawn position
     * @param yPos Y spawn position
     * @return The created Ghost with its unique strategy
     */
    public abstract Ghost makeGhost(int xPos, int yPos);
}
