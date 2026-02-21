package com.pacman.ui;

import com.pacman.core.UIPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * GameFrame is the main application window.
 * Dynamically scales to fit the screen resolution.
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

        // Calculate scale based on screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenH = screenSize.height;

        // Base game area is 448x496. Scale to fill ~70% of screen height.
        int baseGameW = 448;
        int baseGameH = 496;
        int toolbarH = 40;

        double scale = Math.min(
                (screenH * 0.7) / (baseGameH + toolbarH),
                3.0 // cap at 3x
        );
        scale = Math.max(scale, 1.0); // minimum 1x

        int scaledGameW = (int) (baseGameW * scale);
        int scaledGameH = (int) (baseGameH * scale);
        int scaledToolbarH = (int) (toolbarH * scale);

        // Use BorderLayout: toolbar on top, game in center
        JPanel gameWindow = new JPanel(new BorderLayout());

        // Create UI Panel as a thin top toolbar
        uiPanel = new UIPanel(scaledGameW, scaledToolbarH);

        // Create game panel with scaled dimensions
        try {
            gamePanel = new GamePanel(uiPanel, scaledGameW, scaledGameH);
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
