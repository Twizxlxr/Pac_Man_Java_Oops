package com.pacman.core;

import com.pacman.entity.PacGum;
import com.pacman.entity.SuperPacGum;
import com.pacman.ghost.Ghost;
import com.pacman.ghost.state.FrightenedMode;

import javax.swing.*;
import java.awt.*;

/**
 * UI Panel that displays the score.
 * Implements Observer to receive game event notifications.
 */
public class UIPanel extends JPanel implements Observer {
    public static int width;
    public static int height;

    private int score = 0;
    private JLabel scoreLabel;

    public UIPanel(int width, int height) {
        UIPanel.width = width;
        UIPanel.height = height;
        setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(20.0F));
        scoreLabel.setForeground(Color.WHITE);
        this.add(scoreLabel, BorderLayout.WEST);
    }

    public void updateScore(int incrScore) {
        this.score += incrScore;
        this.scoreLabel.setText("Score: " + score);
    }

    public int getScore() {
        return score;
    }

    @Override
    public void updatePacGumEaten(PacGum pg) {
        updateScore(10);
    }

    @Override
    public void updateSuperPacGumEaten(SuperPacGum spg) {
        updateScore(100);
    }

    @Override
    public void updateGhostCollision(Ghost gh) {
        if (gh.getState() instanceof FrightenedMode) {
            updateScore(500);
        }
    }
}
