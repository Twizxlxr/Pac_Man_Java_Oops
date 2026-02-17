package com.pacman.core;

import com.pacman.entity.PacGum;
import com.pacman.entity.SuperPacGum;
import com.pacman.ghost.Ghost;
import com.pacman.ghost.state.FrightenedMode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * UI Panel that displays the score.
 * Implements Observer to receive game event notifications.
 */
public class UIPanel extends JPanel implements Observer {
    public static int width;
    public static int height;

    private int score = 0;
    private int lives = 3;
    private JLabel scoreLabel;
    private BufferedImage livesIcon;
    
    // Restart callback
    private Runnable restartCallback;
    
    // Button bounds
    private Rectangle yesButton = new Rectangle();
    private Rectangle noButton = new Rectangle();

    public UIPanel(int width, int height) {
        UIPanel.width = width;
        UIPanel.height = height;
        setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(20.0F));
        scoreLabel.setForeground(Color.WHITE);
        this.add(scoreLabel, BorderLayout.WEST);
        
        // Load lives icon
        try {
            livesIcon = ImageIO.read(new File("pacman.png"));
        } catch (IOException e) {
            System.err.println("Could not load pacman.png for lives display");
        }
        
        // Add mouse listener for restart buttons
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Game.isGameOver() || Game.isGameWon()) {
                    if (yesButton.contains(e.getPoint())) {
                        if (restartCallback != null) {
                            restartCallback.run();
                        }
                    } else if (noButton.contains(e.getPoint())) {
                        System.exit(0);
                    }
                }
            }
        });
    }
    
    /** Sets the callback to run when restart is clicked */
    public void setRestartCallback(Runnable callback) {
        this.restartCallback = callback;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw lives icons
        if (livesIcon != null) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Lives:", 10, 60);
            for (int i = 0; i < lives; i++) {
                // Draw first frame of pacman sprite (facing right)
                g.drawImage(livesIcon.getSubimage(0, 0, 32, 32), 70 + i * 36, 40, 32, 32, null);
            }
        }
        
        // Draw level display
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String levelText = "Level: " + LevelConfig.getCurrentLevel();
        g.drawString(levelText, 10, height - 20);
        
        // Draw restart UI when game is over or won
        if (Game.isGameOver() || Game.isGameWon()) {
            Graphics2D g2 = (Graphics2D) g;
            
            // Draw restart prompt - centered
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fmText = g.getFontMetrics();
            String line1 = "Would you like";
            String line2 = "to restart?";
            int line1X = (width - fmText.stringWidth(line1)) / 2;
            int line2X = (width - fmText.stringWidth(line2)) / 2;
            int centerY = height / 2;
            g.drawString(line1, line1X, centerY - 40);
            g.drawString(line2, line2X, centerY - 20);
            
            // Draw buttons - centered
            int buttonWidth = 70;
            int buttonHeight = 30;
            int buttonY = centerY;
            int totalButtonsWidth = buttonWidth * 2 + 20; // 20px gap between buttons
            int startX = (width - totalButtonsWidth) / 2;
            int yesX = startX;
            int noX = startX + buttonWidth + 20;
            
            yesButton.setBounds(yesX, buttonY, buttonWidth, buttonHeight);
            noButton.setBounds(noX, buttonY, buttonWidth, buttonHeight);
            
            // Yes button
            g.setColor(new Color(0, 150, 0));
            g2.fillRoundRect(yesX, buttonY, buttonWidth, buttonHeight, 10, 10);
            g.setColor(Color.WHITE);
            g2.drawRoundRect(yesX, buttonY, buttonWidth, buttonHeight, 10, 10);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g.getFontMetrics();
            g.drawString("YES", yesX + (buttonWidth - fm.stringWidth("YES")) / 2, buttonY + 20);
            
            // No button
            g.setColor(new Color(150, 0, 0));
            g2.fillRoundRect(noX, buttonY, buttonWidth, buttonHeight, 10, 10);
            g.setColor(Color.WHITE);
            g2.drawRoundRect(noX, buttonY, buttonWidth, buttonHeight, 10, 10);
            g.drawString("NO", noX + (buttonWidth - fm.stringWidth("NO")) / 2, buttonY + 20);
        }
    }

    public void updateScore(int incrScore) {
        this.score += incrScore;
        this.scoreLabel.setText("Score: " + score);
    }

    public int getScore() {
        return score;
    }
    
    public int getLives() {
        return lives;
    }
    
    public void loseLife() {
        lives--;
        repaint();
    }
    
    public boolean isGameOver() {
        return lives <= 0;
    }
    
    /** Resets the UI panel for a new game */
    public void reset() {
        score = 0;
        lives = 3;
        scoreLabel.setText("Score: " + score);
        repaint();
    }
    
    /** Resets for next level while preserving score and lives */
    public void resetForNextLevel() {
        repaint();
    }

    @Override
    public void updatePacGumEaten(PacGum pg) {
        updateScore(LevelConfig.getPacGumPoints());
    }

    @Override
    public void updateSuperPacGumEaten(SuperPacGum spg) {
        updateScore(LevelConfig.getSuperPacGumPoints());
    }

    @Override
    public void updateGhostCollision(Ghost gh) {
        if (gh.getState() instanceof FrightenedMode) {
            updateScore(LevelConfig.getGhostPoints());
        }
    }
}
