package com.pacman.ui;

import com.pacman.model.GameMap;
import com.pacman.model.PacMan;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * GamePanel handles rendering and game logic updates.
 * Uses a Timer to create a game loop for smooth movement and rendering.
 */
public class GamePanel extends JPanel implements KeyListener {
    private GameMap gameMap;
    private PacMan pacMan;
    private Timer gameTimer;
    private static final int GAME_SPEED = 100; // milliseconds between updates
    
    public GamePanel() {
        // Initialize game objects
        gameMap = new GameMap();
        pacMan = new PacMan(10, 9, gameMap);
        
        // Set panel properties
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.BLACK);
        
        // Create and start game loop timer
        gameTimer = new Timer(GAME_SPEED, e -> {
            pacMan.update();
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
        
        // Draw Pac-Man
        drawPacMan(g2d);
        
        // Draw game info
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
     * Draws game information on the screen.
     */
    private void drawGameInfo(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
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
