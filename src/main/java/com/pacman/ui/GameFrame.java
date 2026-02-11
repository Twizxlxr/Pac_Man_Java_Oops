package com.pacman.ui;

import javax.swing.JFrame;
import java.awt.Dimension;

/**
 * GameFrame is the main application window.
 * Sets up the window and contains the GamePanel.
 */
public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    
    public GameFrame() {
        super("Pac-Man Game");
        
        // Configure frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Create and add game panel
        gamePanel = new GamePanel();
        add(gamePanel);
        
        // Set window size based on maze
        Dimension mazeSize = gamePanel.getMazeSize();
        setSize(mazeSize.width + 16, mazeSize.height + 60);
        
        // Center window on screen
        setLocationRelativeTo(null);
        
        // Make window visible
        setVisible(true);
    }
    
    public static void main(String[] args) {
        // Run on EDT (Event Dispatch Thread)
        javax.swing.SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
}
