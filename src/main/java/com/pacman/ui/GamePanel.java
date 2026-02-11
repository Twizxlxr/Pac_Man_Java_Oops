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
        pacMan = new PacMan(16, 9, gameMap);
        
        // Initialize 4 ghosts at strategic positions
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost("Blinky", Ghost.Color.RED, 10, 9, gameMap));      // Center - red
        ghosts.add(new Ghost("Pinky", Ghost.Color.PINK, 10, 8, gameMap));      // Left - pink
        ghosts.add(new Ghost("Inky", Ghost.Color.CYAN, 10, 10, gameMap));      // Right - cyan
        ghosts.add(new Ghost("Clyde", Ghost.Color.ORANGE, 9, 9, gameMap));     // Top - orange
        
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
        
        // Draw maze walls and pellets
        drawMazeWalls(g2d);
        drawPellets(g2d);
        
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
     * Returns the top-left offset used to center the maze in the window.
     */
    private int[] getMazeOffset() {
        int cellSize = gameMap.getCellSize();
        int mazeWidth = gameMap.getCols() * cellSize;
        int mazeHeight = gameMap.getRows() * cellSize;
        int infoHeight = 30;
        int offsetX = Math.max(0, (getWidth() - mazeWidth) / 2);
        int offsetY = Math.max(0, (getHeight() - infoHeight - mazeHeight) / 2);
        return new int[] { offsetX, offsetY };
    }

    /**
     * Draws the classic Pac-Man maze walls using continuous blue lines.
     */
    private void drawMazeWalls(Graphics2D g) {
        int cellSize = gameMap.getCellSize();
        int[] offset = getMazeOffset();
        int x0 = offset[0];
        int y0 = offset[1];
        int width = gameMap.getCols() * cellSize;
        int height = gameMap.getRows() * cellSize;

        g.setColor(new Color(0, 0, 255));
        g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Outer rounded rectangle border
        g.drawRoundRect(x0 + 3, y0 + 3, width - 6, height - 6, cellSize, cellSize);

        // Long horizontal corridors (top and bottom)
        g.drawLine(x0 + cellSize * 2, y0 + cellSize * 4, x0 + width - cellSize * 2, y0 + cellSize * 4);
        g.drawLine(x0 + cellSize * 2, y0 + cellSize * 16, x0 + width - cellSize * 2, y0 + cellSize * 16);

        // Symmetrical vertical connectors
        g.drawLine(x0 + cellSize * 4, y0 + cellSize * 4, x0 + cellSize * 4, y0 + cellSize * 7);
        g.drawLine(x0 + cellSize * 14, y0 + cellSize * 4, x0 + cellSize * 14, y0 + cellSize * 7);
        g.drawLine(x0 + cellSize * 4, y0 + cellSize * 13, x0 + cellSize * 4, y0 + cellSize * 16);
        g.drawLine(x0 + cellSize * 14, y0 + cellSize * 13, x0 + cellSize * 14, y0 + cellSize * 16);

        // Inner corner blocks with rounded corners
        g.drawRoundRect(x0 + cellSize * 2, y0 + cellSize * 2, cellSize * 5, cellSize * 3, cellSize, cellSize);
        g.drawRoundRect(x0 + cellSize * 12, y0 + cellSize * 2, cellSize * 5, cellSize * 3, cellSize, cellSize);
        g.drawRoundRect(x0 + cellSize * 2, y0 + cellSize * 14, cellSize * 5, cellSize * 3, cellSize, cellSize);
        g.drawRoundRect(x0 + cellSize * 12, y0 + cellSize * 14, cellSize * 5, cellSize * 3, cellSize, cellSize);

        // Accent arcs for rounded inner corners
        g.drawArc(x0 + cellSize * 2, y0 + cellSize * 2, cellSize * 2, cellSize * 2, 90, 90);
        g.drawArc(x0 + cellSize * 15, y0 + cellSize * 2, cellSize * 2, cellSize * 2, 0, 90);
        g.drawArc(x0 + cellSize * 2, y0 + cellSize * 15, cellSize * 2, cellSize * 2, 180, 90);
        g.drawArc(x0 + cellSize * 15, y0 + cellSize * 15, cellSize * 2, cellSize * 2, 270, 90);

        // Mid-side blocks
        g.drawRoundRect(x0 + cellSize * 2, y0 + cellSize * 7, cellSize * 3, cellSize * 4, cellSize, cellSize);
        g.drawRoundRect(x0 + cellSize * 14, y0 + cellSize * 7, cellSize * 3, cellSize * 4, cellSize, cellSize);

        // Central vertical connectors
        g.drawLine(x0 + cellSize * 9, y0 + cellSize * 4, x0 + cellSize * 9, y0 + cellSize * 8);
        g.drawLine(x0 + cellSize * 9, y0 + cellSize * 12, x0 + cellSize * 9, y0 + cellSize * 16);

        // Central ghost box
        g.drawRoundRect(x0 + cellSize * 7, y0 + cellSize * 9, cellSize * 5, cellSize * 3, cellSize, cellSize);

        // Opening in ghost box (erase small section)
        g.setColor(Color.BLACK);
        g.drawLine(x0 + cellSize * 8, y0 + cellSize * 9, x0 + cellSize * 10, y0 + cellSize * 9);
        g.setColor(new Color(0, 0, 255));

        // Side tunnel openings (erase outer border sections)
        g.setColor(Color.BLACK);
        g.drawLine(x0, y0 + cellSize * 10, x0 + cellSize, y0 + cellSize * 10);
        g.drawLine(x0 + width - cellSize, y0 + cellSize * 10, x0 + width, y0 + cellSize * 10);
        g.setColor(new Color(0, 0, 255));
    }

    /**
     * Draws pellets along walkable paths.
     */
    private void drawPellets(Graphics2D g) {
        int cellSize = gameMap.getCellSize();
        int[] offset = getMazeOffset();
        int offsetX = offset[0];
        int offsetY = offset[1];

        for (int row = 0; row < gameMap.getRows(); row++) {
            for (int col = 0; col < gameMap.getCols(); col++) {
                int tileType = gameMap.getTile(row, col);
                if (tileType == GameMap.DOT) {
                    int x = offsetX + col * cellSize;
                    int y = offsetY + row * cellSize;
                    int dotSize = isPowerPellet(row, col) ? 14 : 6;
                    g.setColor(Color.WHITE);
                    g.fillOval(x + cellSize / 2 - dotSize / 2, y + cellSize / 2 - dotSize / 2, dotSize, dotSize);
                }
            }
        }
    }

    private boolean isPowerPellet(int row, int col) {
        int lastRow = gameMap.getRows() - 2;
        int lastCol = gameMap.getCols() - 2;
        return (row == 1 && col == 1)
            || (row == 1 && col == lastCol)
            || (row == lastRow && col == 1)
            || (row == lastRow && col == lastCol);
    }
    
    /**
     * Draws Pac-Man with animated mouth.
     * Mouth opens/closes based on mouthAngle and rotates based on direction.
     */
    private void drawPacMan(Graphics2D g) {
        int cellSize = gameMap.getCellSize();
        int[] offset = getMazeOffset();
        int x = offset[0] + pacMan.getCol() * cellSize + 3;
        int y = offset[1] + pacMan.getRow() * cellSize + 3;
        int size = cellSize - 6;
        
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
        int eyeSize = 3;
        int eyeX = x + size / 3;
        int eyeY = y + size / 4;
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
        int[] offset = getMazeOffset();
        int x = offset[0] + ghost.getCol() * cellSize + 3;
        int y = offset[1] + ghost.getRow() * cellSize + 3;
        int size = cellSize - 6;
        int headRadius = size / 2;
        int bodyHeight = size - headRadius;
        
        // Set ghost color based on type
        java.awt.Color ghostBodyColor;
        switch (ghost.getGhostColor()) {
            case RED:
                ghostBodyColor = new Color(255, 0, 0);
                break;
            case PINK:
                ghostBodyColor = new Color(255, 105, 180);
                break;
            case CYAN:
                ghostBodyColor = new Color(0, 255, 255);
                break;
            case ORANGE:
                ghostBodyColor = new Color(255, 165, 0); // Orange
                break;
            default:
                ghostBodyColor = Color.WHITE;
        }
        
        g.setColor(ghostBodyColor);
        
        // Draw ghost head (semicircle)
        g.fillArc(x, y, size, headRadius * 2, 0, 180);

        // Draw ghost body (rectangular mid-section)
        int bodyY = y + headRadius;
        int waveHeight = Math.max(4, size / 6);
        int bodyRectHeight = bodyHeight - waveHeight;
        g.fillRect(x, bodyY, size, bodyRectHeight);

        // Draw wavy bottom using polygon
        int waveCount = 4;
        int waveWidth = size / waveCount;
        int waveTop = bodyY + bodyRectHeight;
        int waveBase = waveTop + waveHeight;
        int[] xPoints = new int[] {
            x,
            x + waveWidth / 2,
            x + waveWidth,
            x + waveWidth + waveWidth / 2,
            x + 2 * waveWidth,
            x + 2 * waveWidth + waveWidth / 2,
            x + 3 * waveWidth,
            x + 3 * waveWidth + waveWidth / 2,
            x + 4 * waveWidth,
            x + 4 * waveWidth,
            x
        };
        int[] yPoints = new int[] {
            waveTop,
            waveBase,
            waveTop,
            waveBase,
            waveTop,
            waveBase,
            waveTop,
            waveBase,
            waveTop,
            waveTop,
            waveTop
        };
        g.fillPolygon(xPoints, yPoints, xPoints.length);

        // Draw eyes (two white circles with black pupils)
        g.setColor(Color.WHITE);
        int eyeRadius = 3;
        int eyeSpacing = size / 3;
        
        // Left eye
        int leftEyeX = x + eyeSpacing - eyeRadius;
        int leftEyeY = y + headRadius / 2 - eyeRadius / 2;
        g.fillOval(leftEyeX, leftEyeY, eyeRadius * 2, eyeRadius * 2);
        
        // Right eye
        int rightEyeX = x + size - eyeSpacing - eyeRadius;
        int rightEyeY = y + headRadius / 2 - eyeRadius / 2;
        g.fillOval(rightEyeX, rightEyeY, eyeRadius * 2, eyeRadius * 2);
        
        // Draw pupils (black dots inside white eyes)
        g.setColor(Color.BLACK);
        int pupilRadius = 2;
        g.fillOval(leftEyeX + eyeRadius - pupilRadius, leftEyeY + eyeRadius - pupilRadius, 
                   pupilRadius * 2, pupilRadius * 2);
        g.fillOval(rightEyeX + eyeRadius - pupilRadius, rightEyeY + eyeRadius - pupilRadius, 
                   pupilRadius * 2, pupilRadius * 2);
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
                pacMan.resetPosition(16, 9);
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

