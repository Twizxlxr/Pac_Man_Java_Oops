package com.pacman.core;

import com.pacman.entity.PacGum;
import com.pacman.entity.SuperPacGum;
import com.pacman.ghost.Ghost;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * UI toolbar panel displayed at the top of the game window.
 * Shows score, lives, and level in a single horizontal bar.
 * Implements Observer to receive game event notifications.
 */
public class UIPanel extends JPanel implements Observer {
    public static int width;
    public static int height;

    private int score = 0;
    private int lives = 3;
    private BufferedImage livesIcon;

    // Restart callback
    private Runnable restartCallback;

    public UIPanel(int width, int height) {
        UIPanel.width = width;
        UIPanel.height = height;
        setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);

        // Load lives icon
        try {
            livesIcon = ImageIO.read(new File("pacman.png"));
        } catch (IOException e) {
            System.err.println("Could not load pacman.png for lives display");
        }
    }

    /** Sets the callback to run when restart is clicked */
    public void setRestartCallback(Runnable callback) {
        this.restartCallback = callback;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int y = height / 2 + 5; // Vertical center for text

        // === LEFT: Score ===
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, y);

        // === CENTER: Lives icons ===
        if (livesIcon != null) {
            int livesStartX = width / 2 - (lives * 22) / 2;
            for (int i = 0; i < lives; i++) {
                g.drawImage(livesIcon.getSubimage(0, 0, 32, 32),
                        livesStartX + i * 22, (height - 20) / 2, 20, 20, null);
            }
        } else {
            // Fallback text if icon not loaded
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            String livesText = "Lives: " + lives;
            FontMetrics fm = g.getFontMetrics();
            g.drawString(livesText, (width - fm.stringWidth(livesText)) / 2, y);
        }

        // === RIGHT: Level ===
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String levelText = "Lv " + LevelConfig.getCurrentLevel();
        FontMetrics fm = g.getFontMetrics();
        g.drawString(levelText, width - fm.stringWidth(levelText) - 10, y);
    }

    public void updateScore(int incrScore) {
        this.score += incrScore;
        repaint();
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
        // Ghost score handled by Game.java (escalating bonus: 200->400->800->1600)
    }
}
