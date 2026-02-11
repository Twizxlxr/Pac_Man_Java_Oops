package com.pacman.ghost.strategy;

/**
 * Strategy interface for ghost targeting behavior.
 * 
 * <p><b>Design Pattern:</b> Strategy Pattern</p>
 * 
 * <p>Each ghost has a unique targeting strategy:
 * <ul>
 *   <li><b>Blinky (Red):</b> Targets PacMan's current position directly</li>
 *   <li><b>Pinky (Pink):</b> Targets 4 tiles ahead of PacMan</li>
 *   <li><b>Inky (Cyan):</b> Uses vector from Blinky to 2 tiles ahead of PacMan</li>
 *   <li><b>Clyde (Orange):</b> Targets PacMan if far, retreats if close</li>
 * </ul>
 * </p>
 * 
 * <p><b>Scatter Mode Targets (corners):</b></p>
 * <pre>
 *   Pinky ┌─────────────────────┐ Blinky
 *   (0,0) │                     │ (448,0)
 *         │                     │
 *         │                     │
 *   Clyde └─────────────────────┘ Inky
 *   (0,496)                       (448,496)
 * </pre>
 * 
 * @see BlinkyStrategy Direct pursuit strategy
 * @see PinkyStrategy Ambush strategy
 * @see InkyStrategy Flanking strategy
 * @see ClydeStrategy Shy/retreat strategy
 */
public interface IGhostStrategy {
    
    /**
     * Returns target position when ghost is in Chase mode.
     * @return int[2] with {x, y} target coordinates
     */
    int[] getChaseTargetPosition();
    
    /**
     * Returns target position when ghost is in Scatter mode.
     * Each ghost targets a different corner.
     * @return int[2] with {x, y} target coordinates
     */
    int[] getScatterTargetPosition();
}
