package com.pacman.ui;

import com.pacman.model.GameMap;
import com.pacman.model.PacMan;
import com.pacman.model.Ghost;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * GamePanel handles rendering and game logic updates.
 * Uses a Timer to create a game loop for smooth movement and rendering.
 */
public class GamePanel extends JPanel implements KeyListener {
    private GameMap gameMap;
    private PacMan pacMan;
    private List<Ghost> ghosts;
    private Timer gameTimer;
    private static final int GAME_SPEED = 100; // milliseconds between updates
    
    // Score tracking
    private int score = 0;
    private static final int PELLET_POINTS = 10;
    private static final int GHOST_PENALTY = 100; // Points lost when hitting a ghost
    
    public GamePanel() {
        // Initialize game objects
        gameMap = new GameMap();
        pacMan = new PacMan(10, 9, gameMap);
        
        // Initialize ghosts with different starting positions and colors
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost("Blinky", Ghost.Color.RED, 5, 5, gameMap));
        ghosts.add(new Ghost("Pinky", Ghost.Color.PINK, 5, 13, gameMap));
        ghosts.add(new Ghost("Inky", Ghost.Color.CYAN, 10, 5, gameMap));
        ghosts.add(new Ghost("Clyde", Ghost.Color.ORANGE, 10, 13, gameMap));
        
        // Set panel properties
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.BLACK);
        
        // Create and start game loop timer
        gameTimer = new Timer(GAME_SPEED, e -> {
            pacMan.update();
            // Update all ghosts
            for (Ghost ghost : ghosts) {
                ghost.update();
            }
            // Check for collisions between Pac-Man and ghosts
            checkCollisions();
            repaint();
        });
        gameTimer.start();
    }
    
    /**
     * Paintcomponent - renders the game board and Pac-Man.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable antialiasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw the game grid
        drawGrid(g2d);
        
        // Draw all ghosts
        for (Ghost ghost : ghosts) {
            drawGhost(g2d, ghost);
        }
        
        // Draw Pac-Man
        drawPacMan(g2d);
        
        // Draw game info (score and instructions)
        drawGameInfo(g2d);
    }
    
    /**
     * Draws the game grid (walls, empty spaces, and dots).
     */
    private void drawGrid(Graphics2D g) {
        int cellSize = gameMap.getCellSize();
        
        for (int row = 0; row < gameMap.getRows(); row++) {
            for (int col = 0; col < gameMap.getCols(); col++) {
                int x = col * cellSize;
                int y = row * cellSize;
                int tileType = gameMap.getTile(row, col);
                
                if (tileType == GameMap.WALL) {
                    // Draw walls in blue
                    g.setColor(Color.BLUE);
                    g.fillRect(x, y, cellSize, cellSize);
                    g.setColor(Color.CYAN);
                    g.setStroke(new BasicStroke(2));
                    g.drawRect(x, y, cellSize, cellSize);
                } else if (tileType == GameMap.DOT) {
                    // Draw dots in white
                    g.setColor(Color.WHITE);
                    int dotSize = 4;
                    g.fillOval(x + cellSize / 2 - dotSize / 2, y + cellSize / 2 - dotSize / 2, dotSize, dotSize);
                }
            }
        }
    }
    
    /**
     * Draws Pac-Man on the screen.
     * Uses a simple circle representation for now.
     */
    private void drawPacMan(Graphics2D g) {
        int cellSize = gameMap.getCellSize();
        int x = pacMan.getCol() * cellSize + 2;
        int y = pacMan.getRow() * cellSize + 2;
        int size = cellSize - 4;
        
        // Draw Pac-Man as a yellow circle
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, size, size);
        
        // Draw a simple mouth based on direction
        g.setColor(Color.BLACK);
        int centerX = x + size / 2;
        int centerY = y + size / 2;
        
        switch (pacMan.getCurrentDirection()) {
            case PacMan.RIGHT:
                g.fillPolygon(new int[]{centerX, centerX + 3, centerX + 3}, 
                             new int[]{centerY - 3, centerY, centerY + 3}, 3);
                break;
            case PacMan.LEFT:
                g.fillPolygon(new int[]{centerX, centerX - 3, centerX - 3}, 
                             new int[]{centerY - 3, centerY, centerY + 3}, 3);
                break;
            case PacMan.UP:
                g.fillPolygon(new int[]{centerX - 3, centerX + 3, centerX}, 
                             new int[]{centerX - 3, centerX - 3, centerY - 3}, 3);
                break;
            case PacMan.DOWN:
                g.fillPolygon(new int[]{centerX - 3, centerX + 3, centerX}, 
                             new int[]{centerY + 3, centerY + 3, centerY + 6}, 3);
                break;
        }
    }
    
    /**
     * Draws a single ghost on the screen.
     * Each ghost is rendered in a different color based on its type.
     * @param g the graphics context
     * @param ghost the ghost to draw
     */
    private void drawGhost(Graphics2D g, Ghost ghost) {
        int cellSize = gameMap.getCellSize();
        int x = ghost.getCol() * cellSize + 2;
        int y = ghost.getRow() * cellSize + 2;
        int size = cellSize - 4;
        
        // Set ghost color based on type
        switch (ghost.getGhostColor()) {
            case RED:
                g.setColor(Color.RED);
                break;
            case PINK:
                g.setColor(new Color(255, 184, 255)); // Light pink
                break;
            case CYAN:
                g.setColor(Color.CYAN);
                break;
            case ORANGE:
                g.setColor(new Color(255, 165, 0)); // Orange
                break;
        }
        
        // Draw ghost body as a rounded rectangle
        g.fillRoundRect(x, y, size, size, 5, 5);
        
        // Draw eyes
        g.setColor(Color.WHITE);
        int eyeSize = 2;
        g.fillOval(x + size / 4 - eyeSize / 2, y + size / 3 - eyeSize / 2, eyeSize, eyeSize);
        g.fillOval(x + 3 * size / 4 - eyeSize / 2, y + size / 3 - eyeSize / 2, eyeSize, eyeSize);
    }
    
    /**
     * Checks for collisions between Pac-Man and any ghost.
     * When a collision is detected, logs the event and applies penalty.
     * In future versions, this could trigger game-over or other events.
     */
    private void checkCollisions() {
        for (Ghost ghost : ghosts) {
            if (ghost.collidesWith(pacMan.getRow(), pacMan.getCol())) {
                // Log collision
                System.out.println("COLLISION! Pac-Man hit " + ghost.getName() + "!");
                
                // Apply penalty to score
                score = Math.max(0, score - GHOST_PENALTY);
                
                // Reset Pac-Man position (for now, restart at center)
                // In future versions, this could reduce lives or trigger game-over
                pacMan.resetPosition(10, 9);
                
                System.out.println("Score after collision: " + score);
            }
        }
    }
    
    /**
     * Draws game information on the screen (score and instructions).
     */
    private void drawGameInfo(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Score: " + score, 10, getHeight() - 25);
        g.drawString("Use arrow keys to move", 10, getHeight() - 10);
    }
    
    /**
     * KeyListener implementation - handles arrow key input.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        switch (keyCode) {
            case KeyEvent.VK_UP:
                pacMan.setNextDirection(PacMan.UP);
                break;
            case KeyEvent.VK_DOWN:
                pacMan.setNextDirection(PacMan.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                pacMan.setNextDirection(PacMan.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                pacMan.setNextDirection(PacMan.RIGHT);
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Not used for this simple implementation
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used for this simple implementation
    }
}
