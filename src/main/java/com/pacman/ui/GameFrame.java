package com.pacman.ui;

import com.pacman.core.UIPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * GameFrame is the main application window.
 * Sets up the window and contains the GamePanel and UIPanel.
 */
public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private UIPanel uiPanel;
    
    public GameFrame() {
        super("Pac-Man Game");
        
        // Configure frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel gameWindow = new JPanel();
        gameWindow.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        // Create UI Panel for score
        uiPanel = new UIPanel(256, 496);

        // Create game panel
        try {
            gamePanel = new GamePanel(uiPanel);
            gameWindow.add(gamePanel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gameWindow.add(uiPanel);

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
