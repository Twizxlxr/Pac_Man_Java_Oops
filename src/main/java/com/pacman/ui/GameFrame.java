package com.pacman.ui;

import com.pacman.core.UIPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * GameFrame is the main application window.
 * Uses BorderLayout with UIPanel as a top toolbar and GamePanel as center.
 */
public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private UIPanel uiPanel;

    public GameFrame() {
        super("Pac-Man Game");

        // Configure frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Use BorderLayout: toolbar on top, game in center
        JPanel gameWindow = new JPanel(new BorderLayout());

        // Create UI Panel as a thin top toolbar
        uiPanel = new UIPanel(GamePanel.width, 40);

        // Create game panel
        try {
            gamePanel = new GamePanel(uiPanel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gameWindow.add(uiPanel, BorderLayout.NORTH);
        gameWindow.add(gamePanel, BorderLayout.CENTER);

        setContentPane(gameWindow);
        pack();

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
