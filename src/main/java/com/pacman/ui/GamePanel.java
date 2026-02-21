package com.pacman.ui;

import com.pacman.core.Game;
import com.pacman.core.LevelConfig;
import com.pacman.core.UIPanel;
import com.pacman.util.KeyHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Main game panel handling rendering and game loop.
 */
public class GamePanel extends JPanel implements Runnable {
    public static int width = 448;
    public static int height = 496;

    private Thread thread;
    private boolean running = false;

    private BufferedImage img;
    private Graphics2D g;
    private Image backgroundImage;
    private Image gameOverImage;
    private Image youWinImage;

    private KeyHandler key;
    private Game game;
    private UIPanel uiPanel;

    public GamePanel(UIPanel uiPanel) throws IOException {
        this.uiPanel = uiPanel;
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        requestFocus();

        try {
            backgroundImage = ImageIO.read(new File("background.png"));
        } catch (IOException e) {
            System.err.println("Could not load background.png");
        }

        try {
            gameOverImage = ImageIO.read(new File("GameOver.png"));
        } catch (IOException e) {
            System.err.println("Could not load GameOver.png");
        }

        try {
            youWinImage = ImageIO.read(new File("youwin.png"));
        } catch (IOException e) {
            System.err.println("Could not load youwin.png");
        }

        // Set restart callback on UIPanel
        uiPanel.setRestartCallback(() -> restartGame());
    }

    /** Restarts the game by reinitializing everything */
    private void restartGame() {
        Game.resetGameOver();
        LevelConfig.resetToLevel1();
        uiPanel.reset();
        game = new Game(uiPanel);
        uiPanel.repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();

        if (thread == null) {
            thread = new Thread(this, "GameThread");
            thread.start();
        }
    }

    public void init() {
        running = true;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) img.getGraphics();

        key = new KeyHandler(this);
        game = new Game(uiPanel);
    }

    public void update() {
        // Check for restart key during game over or win
        if ((Game.isGameOver() || Game.isGameWon()) && key != null && key.k_restart.isPressed) {
            restartGame();
            key.k_restart.toggle(false);
            return;
        }
        game.update();
    }

    public void input(KeyHandler key) {
        game.input(key);
    }

    public void render() {
        if (g != null) {
            // Draw background
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, width, height, null);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, width, height);
            }

            // Check for game over or win
            if (Game.isGameOver() || Game.isGameWon()) {
                // Semi-transparent overlay
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, width, height);

                // Select which image to show
                Image displayImage = Game.isGameWon() ? youWinImage : gameOverImage;
                String fallbackText = Game.isGameWon() ? "YOU WIN!" : "GAME OVER";
                Color fallbackColor = Game.isGameWon() ? Color.GREEN : Color.RED;

                // Draw image scaled to fit
                if (displayImage != null) {
                    // Scale image to fit within the panel (with padding)
                    int maxWidth = width - 40;
                    int maxHeight = height - 100;
                    int imgWidth = displayImage.getWidth(null);
                    int imgHeight = displayImage.getHeight(null);

                    if (imgWidth > 0 && imgHeight > 0) {
                        // Calculate scale to fit
                        double scale = Math.min((double) maxWidth / imgWidth, (double) maxHeight / imgHeight);
                        int scaledWidth = (int) (imgWidth * scale);
                        int scaledHeight = (int) (imgHeight * scale);

                        int x = (width - scaledWidth) / 2;
                        int y = (height - scaledHeight) / 2;
                        g.drawImage(displayImage, x, y, scaledWidth, scaledHeight, null);
                    }
                } else {
                    // Fallback text if image not found
                    g.setColor(fallbackColor);
                    g.setFont(new Font("Arial", Font.BOLD, 48));
                    FontMetrics fm = g.getFontMetrics();
                    int textX = (width - fm.stringWidth(fallbackText)) / 2;
                    int textY = (height + fm.getAscent()) / 2;
                    g.drawString(fallbackText, textX, textY);
                }

                // Draw restart prompt
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                FontMetrics fmRestart = g.getFontMetrics();
                String restartText = "Press R to Restart";
                int restartX = (width - fmRestart.stringWidth(restartText)) / 2;
                g.drawString(restartText, restartX, height - 30);
            } else {
                // Render all entities
                game.render(g);
            }
        }
    }

    public void draw() {
        Graphics g2 = this.getGraphics();
        if (g2 != null) {
            g2.drawImage(img, 0, 0, width, height, null);
            g2.dispose();
        }
    }

    @Override
    public void run() {
        init();

        // 60 FPS game loop
        final double GAME_HERTZ = 60.0;
        final double TBU = 1000000000 / GAME_HERTZ;
        final int MUBR = 5;

        double lastUpdateTime = System.nanoTime();
        double lastRenderTime;

        final double TARGET_FPS = 60.0;
        final double TTBR = 1000000000 / TARGET_FPS;

        int frameCount = 0;
        int lastSecondTime = (int) (lastUpdateTime / 1000000000);

        while (running) {
            double now = System.nanoTime();
            int updateCount = 0;

            while ((now - lastUpdateTime) > TBU && (updateCount < MUBR)) {
                if (!Game.isGameOver() && !Game.isGameWon()) {
                    input(key);
                    update();
                }
                lastUpdateTime += TBU;
                updateCount++;
            }

            if (now - lastUpdateTime > TBU) {
                lastUpdateTime = now - TBU;
            }

            render();
            draw();
            lastRenderTime = now;
            frameCount++;

            int thisSecond = (int) (lastUpdateTime / 1000000000);
            if (thisSecond > lastSecondTime) {
                frameCount = 0;
                lastSecondTime = thisSecond;
            }

            while ((now - lastRenderTime < TTBR) && (now - lastUpdateTime < TBU)) {
                Thread.yield();
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    System.err.println("Error yielding thread");
                }
                now = System.nanoTime();
            }
        }
    }
}
