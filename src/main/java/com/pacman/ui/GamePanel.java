package com.pacman.ui;

import com.pacman.model.Maze;
import com.pacman.model.PacMan;
import com.pacman.model.Ghost;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

/**
 * GamePanel handles rendering and game logic updates.
 * Uses a Timer to create a game loop for smooth movement and rendering.
 * Implements advanced graphics rendering with Graphics2D for improved visuals.
 */
public class GamePanel extends JPanel implements KeyListener {
    private Maze maze;
    private PacMan pacMan;
    private List<Ghost> ghosts;
    private Timer gameTimer;
    private static final int GAME_SPEED = 100; // milliseconds between Pac-Man updates
    private static final int CELL_SIZE = 20; // pixels per cell
    
    // Score tracking
    private int score = 0;
    private static final int GHOST_COLLISION_PENALTY = 100; // Points lost on collision
    private static final int PELLET_POINTS = 10; // Points for eating a regular pellet
    private static final int POWER_PELLET_POINTS = 50; // Points for eating a power pellet
    
    public GamePanel() {
        // Load maze from CSV file
        maze = new Maze("level.csv");
        
        // Initialize Pac-Man at spawn position (with fallback default)
        Point pacmanSpawn = maze.pacmanSpawn;
        if (pacmanSpawn != null) {
            pacMan = new PacMan(pacmanSpawn.y, pacmanSpawn.x, maze);
        } else {
            // Default spawn position if not found
            pacMan = new PacMan(16, 14, maze);
        }
        
        // Initialize 4 ghosts at positions from maze
        ghosts = new ArrayList<>();
        
        // Blinky (Red)
        Point blinkySpawn = maze.ghostSpawns.get("blinky");
        if (blinkySpawn != null) {
            ghosts.add(new Ghost("Blinky", Ghost.Color.RED, blinkySpawn.y, blinkySpawn.x, maze));
        } else {
            ghosts.add(new Ghost("Blinky", Ghost.Color.RED, 10, 12, maze));
        }
        
        // Pinky (Pink)
        Point pinkySpawn = maze.ghostSpawns.get("pinky");
        if (pinkySpawn != null) {
            ghosts.add(new Ghost("Pinky", Ghost.Color.PINK, pinkySpawn.y, pinkySpawn.x, maze));
        } else {
            ghosts.add(new Ghost("Pinky", Ghost.Color.PINK, 10, 13, maze));
        }
        
        // Inky (Cyan)
        Point inkySpawn = maze.ghostSpawns.get("inky");
        if (inkySpawn != null) {
            ghosts.add(new Ghost("Inky", Ghost.Color.CYAN, inkySpawn.y, inkySpawn.x, maze));
        } else {
            ghosts.add(new Ghost("Inky", Ghost.Color.CYAN, 10, 14, maze));
        }
        
        // Clyde (Orange)
        Point clydeSpawn = maze.ghostSpawns.get("clyde");
        if (clydeSpawn != null) {
            ghosts.add(new Ghost("Clyde", Ghost.Color.ORANGE, clydeSpawn.y, clydeSpawn.x, maze));
        } else {
            ghosts.add(new Ghost("Clyde", Ghost.Color.ORANGE, 10, 15, maze));
        }
        
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
            
            checkPelletCollisions();
            checkCollisions();
            repaint();
        });
        gameTimer.start();
    }
    
    /**
     * Returns the size of the maze in pixels.
     * @return Dimension object with width and height of the maze
     */
    public Dimension getMazeSize() {
        return new Dimension(maze.getCols() * CELL_SIZE, maze.getRows() * CELL_SIZE + 40);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable antialiasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        // Draw maze from CSV
        drawMaze(g2d);
        
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
     * Draws the maze based on CSV data.
     */
    private void drawMaze(Graphics2D g) {
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;
                char tile = maze.getTile(row, col);
                
                if (tile == 'x') {
                    // Draw walls in blue
                    g.setColor(new Color(0, 0, 255));
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
                
                if (tile == '.') {
                    // Draw regular pellets
                    g.setColor(Color.WHITE);
                    g.fillOval(x + 8, y + 8, 6, 6);
                }
                
                if (tile == 'o') {
                    // Draw power pellets
                    g.setColor(Color.WHITE);
                    g.fillOval(x + 4, y + 4, 14, 14);
                }
            }
        }
    }
    
    /**
     * Draws Pac-Man with animated mouth.
     * Mouth opens/closes based on mouthAngle and rotates based on direction.
     */
    private void drawPacMan(Graphics2D g) {
        int x = pacMan.getCol() * CELL_SIZE + 1;
        int y = pacMan.getRow() * CELL_SIZE + 1;
        int size = CELL_SIZE - 2;
        
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
        int eyeY = y + size / 4;
        g.fillOval(eyeX, eyeY, eyeSize, eyeSize);
    }
    
    /**
     * Draws a single ghost with details:
     * - Colored body (square base)
     * - Semicircle head on top
     * - White eyes with black pupils
     * - Special effect when frozen
     * 
     * @param g the graphics context
     * @param ghost the ghost to draw
     */
    private void drawGhost(Graphics2D g, Ghost ghost) {
        int x = ghost.getCol() * CELL_SIZE + 1;
        int y = ghost.getRow() * CELL_SIZE + 1;
        int size = CELL_SIZE - 2;
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
        
        // If frozen, make ghost semi-transparent blue
        if (ghost.isFrozen()) {
            g.setColor(new Color(100, 100, 255, 128)); // Blue with transparency
        } else {
            g.setColor(ghostBodyColor);
        }
        
        // Draw ghost head (semicircle)
        g.fillArc(x, y, size, headRadius * 2, 0, 180);

        // Draw ghost body (rectangular mid-section)
        int bodyY = y + headRadius;
        int waveHeight = Math.max(2, size / 8);
        int bodyRectHeight = bodyHeight - waveHeight;
        g.fillRect(x, bodyY, size, bodyRectHeight);

        // Draw wavy bottom using polygon
        int waveCount = 3;
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
            x + 3 * waveWidth,
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
            waveTop,
            waveTop
        };
        g.fillPolygon(xPoints, yPoints, xPoints.length);

        // Draw eyes (two white circles with black pupils)
        g.setColor(Color.WHITE);
        int eyeRadius = 2;
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
        int pupilRadius = 1;
        g.fillOval(leftEyeX + eyeRadius - pupilRadius, leftEyeY + eyeRadius - pupilRadius, 
                   pupilRadius * 2, pupilRadius * 2);
        g.fillOval(rightEyeX + eyeRadius - pupilRadius, rightEyeY + eyeRadius - pupilRadius, 
                   pupilRadius * 2, pupilRadius * 2);
    }
    
    /**
     * Checks if Pac-Man has eaten any pellets and removes them from the map.
     * Awards points for eating pellets and freezes ghosts for power pellets.
     */
    private void checkPelletCollisions() {
        int pacManRow = pacMan.getRow();
        int pacManCol = pacMan.getCol();
        
        // Check if Pac-Man is on a pellet
        if (maze.hasPellet(pacManRow, pacManCol)) {
            // Regular pellet
            maze.setTile(pacManRow, pacManCol, ' ');
            score += PELLET_POINTS;
            System.out.println("Pellet eaten! Score increased by " + PELLET_POINTS + ". New score: " + score);
        } else if (maze.hasPowerPellet(pacManRow, pacManCol)) {
            // Power pellet - freeze all ghosts
            maze.setTile(pacManRow, pacManCol, ' ');
            score += POWER_PELLET_POINTS;
            for (Ghost ghost : ghosts) {
                ghost.freeze();
            }
            System.out.println("Power Pellet eaten! All ghosts frozen for 8 seconds!");
            System.out.println("Score increased by " + POWER_PELLET_POINTS + ". New score: " + score);
        }
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

