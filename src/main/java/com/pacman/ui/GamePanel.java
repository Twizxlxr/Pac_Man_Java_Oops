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
 * Implements advanced graphics rendering with Graphics2D for improved visuals.
 */
public class GamePanel extends JPanel implements KeyListener {
    private GameMap gameMap;
    private PacMan pacMan;
    private List<Ghost> ghosts;
    private Timer gameTimer;
    private static final int GAME_SPEED = 100; // milliseconds between Pac-Man updates
    
    // Score tracking
    private int score = 0;
    private static final int GHOST_COLLISION_PENALTY = 100; // Points lost on collision
    
    public GamePanel() {
        // Initialize game objects
        gameMap = new GameMap();
        pacMan = new PacMan(10, 9, gameMap);
        
        // Initialize 4 ghosts at strategic positions
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost("Blinky", Ghost.Color.RED, 9, 9, gameMap));      // Center - red
        ghosts.add(new Ghost("Pinky", Ghost.Color.PINK, 8, 8, gameMap));      // Top-left - pink
        ghosts.add(new Ghost("Inky", Ghost.Color.CYAN, 8, 10, gameMap));      // Top-right - cyan
        ghosts.add(new Ghost("Clyde", Ghost.Color.ORANGE, 10, 9, gameMap));   // Bottom - orange
        
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
            
            checkCollisions();
            repaint();
        });
        gameTimer.start();
    }
    
    /**
     * Paintcomponent - renders the game board and all game entities.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable antialiasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        // Draw the game grid and walls
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
     * Draws the game grid (walls, empty spaces, and pellets).
     * Uses thick blue lines for walls to closely match classic Pac-Man style.
     */
    private void drawGrid(Graphics2D g) {
        int cellSize = gameMap.getCellSize();
        
        // Set up stroke for thick walls
        BasicStroke wallStroke = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(wallStroke);
        
        for (int row = 0; row < gameMap.getRows(); row++) {
            for (int col = 0; col < gameMap.getCols(); col++) {
                int x = col * cellSize;
                int y = row * cellSize;
                int tileType = gameMap.getTile(row, col);
                
                if (tileType == GameMap.WALL) {
                    // Draw walls with thick blue lines and rounded edges
                    g.setColor(new Color(33, 66, 255)); // Classic Pac-Man blue
                    g.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
                    g.setColor(Color.CYAN);
                    g.drawRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
                } else if (tileType == GameMap.DOT) {
                    // Draw regular pellets (white dots)
                    // Check if this is a corner position for power pellets
                    if (isCornerPosition(row, col)) {
                        // Power pellet - larger
                        g.setColor(Color.WHITE);
                        int dotSize = 8;
                        g.fillOval(x + cellSize / 2 - dotSize / 2, y + cellSize / 2 - dotSize / 2, dotSize, dotSize);
                    } else {
                        // Regular pellet - smaller
                        g.setColor(Color.WHITE);
                        int dotSize = 3;
                        g.fillOval(x + cellSize / 2 - dotSize / 2, y + cellSize / 2 - dotSize / 2, dotSize, dotSize);
                    }
                }
            }
        }
    }
    
    /**
     * Determines if a position is in a corner (for power pellet placement).
     */
    private boolean isCornerPosition(int row, int col) {
        int rows = gameMap.getRows();
        int cols = gameMap.getCols();
        int cornerThreshold = 3;
        
        // Check if near any corner
        return (row <= cornerThreshold || row >= rows - cornerThreshold - 1) &&
               (col <= cornerThreshold || col >= cols - cornerThreshold - 1);
    }
    
    /**
     * Draws Pac-Man with animated mouth.
     * Mouth opens/closes based on mouthAngle and rotates based on direction.
     */
    private void drawPacMan(Graphics2D g) {
        int cellSize = gameMap.getCellSize();
        int x = pacMan.getCol() * cellSize + 2;
        int y = pacMan.getRow() * cellSize + 2;
        int size = cellSize - 4;
        int centerX = x + size / 2;
        int centerY = y + size / 2;
        
        // Draw Pac-Man body as a filled circle with mouth opening
        g.setColor(Color.YELLOW);
        double mouthAngle = pacMan.getMouthAngle();
        
        // Draw filled arc (Pac-Man body) with mouth opening
        int startAngle = 0;
        int arcAngle = 360;
        
        switch (pacMan.getCurrentDirection()) {
            case PacMan.RIGHT:
                startAngle = (int) mouthAngle;
                arcAngle = (int) (360 - 2 * mouthAngle);
                break;
            case PacMan.LEFT:
                startAngle = (int) (180 - mouthAngle);
                arcAngle = (int) (360 - 2 * mouthAngle);
                break;
            case PacMan.UP:
                startAngle = (int) (270 - mouthAngle);
                arcAngle = (int) (360 - 2 * mouthAngle);
                break;
            case PacMan.DOWN:
                startAngle = (int) (90 - mouthAngle);
                arcAngle = (int) (360 - 2 * mouthAngle);
                break;
        }
        
        g.fillArc(x, y, size, size, startAngle, arcAngle);
        
        // Draw eye
        g.setColor(Color.BLACK);
        int eyeSize = 2;
        int eyeX = x + size / 3;
        int eyeY = y + size / 3;
        g.fillOval(eyeX, eyeY, eyeSize, eyeSize);
    }
    
    /**
     * Draws a single ghost with details:
     * - Colored body (square base)
     * - Semicircle head on top
     * - White eyes with black pupils
     * 
     * @param g the graphics context
     * @param ghost the ghost to draw
     */
    private void drawGhost(Graphics2D g, Ghost ghost) {
        int cellSize = gameMap.getCellSize();
        int x = ghost.getCol() * cellSize + 2;
        int y = ghost.getRow() * cellSize + 2;
        int size = cellSize - 4;
        int bodyHeight = size / 2;
        
        // Set ghost color based on type
        java.awt.Color ghostBodyColor;
        switch (ghost.getGhostColor()) {
            case RED:
                ghostBodyColor = Color.RED;
                break;
            case PINK:
                ghostBodyColor = new Color(255, 184, 255); // Light pink
                break;
            case CYAN:
                ghostBodyColor = Color.CYAN;
                break;
            case ORANGE:
                ghostBodyColor = new Color(255, 165, 0); // Orange
                break;
            default:
                ghostBodyColor = Color.WHITE;
        }
        
        g.setColor(ghostBodyColor);
        
        // Draw ghost body (rectangular bottom)
        g.fillRect(x, y + bodyHeight / 2, size, bodyHeight);
        
        // Draw ghost head (semicircle on top with wavy bottom)
        g.fillArc(x, y - bodyHeight / 2, size, bodyHeight, 0, 180);
        
        // Draw wavy bottom of head using small arcs
        int waveCount = 3;
        int waveWidth = size / waveCount;
        for (int i = 0; i < waveCount; i++) {
            int waveX = x + (i * waveWidth);
            int waveY = y + bodyHeight / 2;
            g.fillArc(waveX, waveY, waveWidth, waveWidth / 2, 0, 180);
        }
        
        // Draw eyes (two white circles with black pupils)
        g.setColor(Color.WHITE);
        int eyeRadius = 2;
        int eyeSpacing = size / 3;
        
        // Left eye
        int leftEyeX = x + eyeSpacing - eyeRadius;
        int leftEyeY = y + bodyHeight / 4 - eyeRadius;
        g.fillOval(leftEyeX, leftEyeY, eyeRadius * 2, eyeRadius * 2);
        
        // Right eye
        int rightEyeX = x + size - eyeSpacing - eyeRadius;
        int rightEyeY = y + bodyHeight / 4 - eyeRadius;
        g.fillOval(rightEyeX, rightEyeY, eyeRadius * 2, eyeRadius * 2);
        
        // Draw pupils (black dots inside white eyes)
        g.setColor(Color.BLACK);
        int pupilRadius = 1;
        g.fillOval(leftEyeX + eyeRadius - pupilRadius, leftEyeY + eyeRadius - pupilRadius, 
                   pupilRadius * 2, pupilRadius * 2);
        g.fillOval(rightEyeX + eyeRadius - pupilRadius, rightEyeY + eyeRadius - pupilRadius, 
                   pupilRadius * 2, pupilRadius * 2);
        
        // Draw ghost outline
        g.setColor(ghostBodyColor.darker());
        g.setStroke(new BasicStroke(1));
        g.drawRect(x, y + bodyHeight / 2, size, bodyHeight);
        g.drawArc(x, y - bodyHeight / 2, size, bodyHeight, 0, 180);
    }
    
    /**
     * Checks for collisions between Pac-Man and all ghosts.
     * Collision occurs when they occupy the same grid cell.
     * On collision, penalizes score and resets Pac-Man position.
     */
    private void checkCollisions() {
        for (Ghost ghost : ghosts) {
            if (ghost.collidesWith(pacMan.getRow(), pacMan.getCol())) {
                // Log collision to console
                System.out.println("COLLISION! Pac-Man hit " + ghost.getName() + "!");
                
                // Apply penalty to score
                score = Math.max(0, score - GHOST_COLLISION_PENALTY);
                System.out.println("Score reduced by " + GHOST_COLLISION_PENALTY + ". New score: " + score);
                
                // Reset Pac-Man to center position
                pacMan.resetPosition(10, 9);
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
        g.drawString("Use arrow keys to move | Avoid ghosts!", 10, getHeight() - 10);
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

