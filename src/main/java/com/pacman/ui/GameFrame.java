package com.pacman.ui;

import com.pacman.model.GameMap;

import javax.swing.JFrame;

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

        // Set frame size based on game grid
        // Create a temporary map to get dimensions
        GameMap tempMap = new GameMap();
        int width = tempMap.getCols() * tempMap.getCellSize();
        int height = tempMap.getRows() * tempMap.getCellSize() + GamePanel.getScoreboardHeight() + 40; // Grid +
                                                                                                       // scoreboard +
                                                                                                       // title bar
        setSize(width, height);

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
