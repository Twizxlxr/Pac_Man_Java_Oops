package com.pacman.ui;

import com.pacman.model.GameMap;
import com.pacman.model.PacMan;
import com.pacman.model.Ghost;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * GamePanel handles rendering and game logic updates.
 * Uses a Timer to create a game loop for smooth movement and rendering.
 * Implements advanced graphics rendering with Graphics2D for improved visuals.
 */
public class GamePanel extends JPanel implements KeyListener {
    private GameMap gameMap;
    private PacMan pacMan;
    private List<Ghost> ghosts;
    private Timer gameTimer;

    // Game speed (adjustable per level)
    private int gameSpeed = 150; // Start very slow (150ms) for level 1
    private static final int MIN_SPEED = 60;
    private static final int SPEED_DECREASE_PER_LEVEL = 8;

    // Score tracking
    private int score = 0;

    // Lives system
    private int lives = 3;
    private boolean gameOver = false;

    // Level system
    private int level = 1;
    private boolean showingLevelCard = false;
    private long levelCardStartTime = 0;
    private static final long LEVEL_CARD_DURATION = 2500; // 2.5 seconds

    // Ghost release tracking
    private long frameCount = 0;

    // Scoreboard height
    private static final int SCOREBOARD_HEIGHT = 44;

    // Wall glow animation
    private float wallGlow = 0f;
    private boolean wallGlowUp = true;

    public GamePanel() {
        gameMap = new GameMap();
        pacMan = new PacMan(10, 9, gameMap);

        // Initialize ghosts inside the ghost house
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost("Blinky", Ghost.Color.RED, 9, 9, gameMap));
        ghosts.add(new Ghost("Pinky", Ghost.Color.PINK, 9, 8, gameMap));
        ghosts.add(new Ghost("Inky", Ghost.Color.CYAN, 9, 10, gameMap));
        ghosts.add(new Ghost("Clyde", Ghost.Color.ORANGE, 10, 9, gameMap));

        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.BLACK);

        startGameTimer();

        // Blinky starts chasing but slowly — level 1 ghosts are very slow
        if (!ghosts.isEmpty()) {
            ghosts.get(0).setState(Ghost.State.CHASING);
        }
        // Level 1: ghosts move very slowly
        for (Ghost ghost : ghosts) {
            ghost.setSpeedRatio(4); // Move every 4th frame — very slow
        }
    }

    /**
     * Creates and starts the game loop timer.
     */
    private void startGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        gameTimer = new Timer(gameSpeed, e -> {
            if (gameOver) {
                repaint();
                return;
            }

            if (showingLevelCard) {
                if (System.currentTimeMillis() - levelCardStartTime >= LEVEL_CARD_DURATION) {
                    showingLevelCard = false;
                }
                repaint();
                return;
            }

            // Wall glow animation
            if (wallGlowUp) {
                wallGlow += 0.03f;
                if (wallGlow >= 1f) {
                    wallGlow = 1f;
                    wallGlowUp = false;
                }
            } else {
                wallGlow -= 0.03f;
                if (wallGlow <= 0f) {
                    wallGlow = 0f;
                    wallGlowUp = true;
                }
            }

            pacMan.update();

            // Pellet collection
            if (gameMap.eatDot(pacMan.getRow(), pacMan.getCol())) {
                score += 10;
            }

            // Level completion
            if (gameMap.countDots() == 0) {
                advanceLevel();
                return;
            }

            // Ghost release
            handleGhostRelease();

            // Update ghosts
            for (Ghost ghost : ghosts) {
                ghost.update(pacMan);
            }

            checkCollisions();
            repaint();
        });
        gameTimer.start();
    }

    /**
     * Releases ghosts one by one based on frame count.
     * Level 1 has longer delays to give the player time.
     */
    private void handleGhostRelease() {
        frameCount++;

        // Adjust release timing based on level (faster releases at higher levels)
        int pinkyRelease = Math.max(30, 80 - (level * 5)); // Level 1: ~12s, gets shorter
        int inkyRelease = pinkyRelease * 2;
        int clydeRelease = pinkyRelease * 3;

        if (frameCount == pinkyRelease && ghosts.size() > 1) {
            ghosts.get(1).setState(Ghost.State.EXITING);
        }
        if (frameCount == inkyRelease && ghosts.size() > 2) {
            ghosts.get(2).setState(Ghost.State.EXITING);
        }
        if (frameCount == clydeRelease && ghosts.size() > 3) {
            ghosts.get(3).setState(Ghost.State.EXITING);
        }
    }

    /**
     * Advances to the next level.
     */
    private void advanceLevel() {
        level++;

        showingLevelCard = true;
        levelCardStartTime = System.currentTimeMillis();

        // Reset map
        gameMap.resetDots();
        pacMan.resetPosition(10, 9);

        for (Ghost ghost : ghosts) {
            ghost.resetPosition();
        }
        if (!ghosts.isEmpty()) {
            ghosts.get(0).setState(Ghost.State.CHASING);
        }

        frameCount = 0;

        // Increase speed gradually
        gameSpeed = Math.max(MIN_SPEED, gameSpeed - SPEED_DECREASE_PER_LEVEL);
        startGameTimer();

        // Ghosts speed up: level 1=4, level 2=3, level 4+=2, level 7+=1
        int ghostSpeed = Math.max(1, 4 - (level / 2));
        for (Ghost ghost : ghosts) {
            ghost.setSpeedRatio(ghostSpeed);
        }
    }

    /**
     * Restarts the entire game from level 1.
     */
    private void restartGame() {
        score = 0;
        lives = 3;
        level = 1;
        gameOver = false;
        showingLevelCard = false;
        frameCount = 0;
        gameSpeed = 150;

        // Re-init map
        gameMap = new GameMap();
        pacMan = new PacMan(17, 9, gameMap); // Spawn far from ghost house

        ghosts.clear();
        ghosts.add(new Ghost("Blinky", Ghost.Color.RED, 9, 9, gameMap));
        ghosts.add(new Ghost("Pinky", Ghost.Color.PINK, 9, 8, gameMap));
        ghosts.add(new Ghost("Inky", Ghost.Color.CYAN, 9, 10, gameMap));
        ghosts.add(new Ghost("Clyde", Ghost.Color.ORANGE, 10, 9, gameMap));

        if (!ghosts.isEmpty()) {
            ghosts.get(0).setState(Ghost.State.CHASING);
        }
        for (Ghost ghost : ghosts) {
            ghost.setSpeedRatio(4);
        }

        startGameTimer();
    }

    // ==================== RENDERING ====================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Scoreboard
        drawScoreboard(g2d);

        // Shift down for scoreboard
        g2d.translate(0, SCOREBOARD_HEIGHT);

        // Grid
        drawGrid(g2d);

        // Ghosts
        for (Ghost ghost : ghosts) {
            drawGhost(g2d, ghost);
        }

        // Pac-Man
        drawPacMan(g2d);

        g2d.translate(0, -SCOREBOARD_HEIGHT);

        // Level card overlay
        if (showingLevelCard) {
            drawLevelCard(g2d);
        }

        // Game over overlay
        if (gameOver) {
            drawGameOver(g2d);
        }
    }

    /**
     * Draws the scoreboard at the top of the screen.
     */
    private void drawScoreboard(Graphics2D g) {
        // Gradient background
        GradientPaint grad = new GradientPaint(0, 0, new Color(10, 10, 35), 0, SCOREBOARD_HEIGHT,
                new Color(20, 20, 50));
        g.setPaint(grad);
        g.fillRect(0, 0, getWidth(), SCOREBOARD_HEIGHT);

        // Blue divider line
        g.setColor(new Color(33, 100, 255));
        g.fillRect(0, SCOREBOARD_HEIGHT - 2, getWidth(), 2);

        // Score (left)
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.setColor(new Color(150, 150, 180));
        g.drawString("SCORE", 12, 14);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.setColor(Color.YELLOW);
        g.drawString(String.format("%06d", score), 12, 35);

        // Level (center)
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.setColor(new Color(150, 150, 180));
        String levelLabel = "LEVEL";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(levelLabel, (getWidth() - fm.stringWidth(levelLabel)) / 2, 14);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.setColor(Color.CYAN);
        String levelNum = String.valueOf(level);
        fm = g.getFontMetrics();
        g.drawString(levelNum, (getWidth() - fm.stringWidth(levelNum)) / 2, 35);

        // Lives (right) - small pac-man icons
        g.setColor(Color.YELLOW);
        int livesStartX = getWidth() - 15;
        for (int i = 0; i < lives; i++) {
            g.fillArc(livesStartX - (i * 22), 14, 16, 16, 35, 290);
        }
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g.setColor(new Color(150, 150, 180));
        g.drawString("LIVES", getWidth() - 70, 12);
    }

    /**
     * Draws the game grid with visible paths, walls, and pellets.
     */
    private void drawGrid(Graphics2D g) {
        int cellSize = gameMap.getCellSize();

        for (int row = 0; row < gameMap.getRows(); row++) {
            for (int col = 0; col < gameMap.getCols(); col++) {
                int x = col * cellSize;
                int y = row * cellSize;
                int tileType = gameMap.getTile(row, col);

                if (tileType == GameMap.WALL) {
                    drawWall(g, x, y, cellSize, row, col);
                } else if (tileType == GameMap.GATE) {
                    // Path background + magenta gate line
                    g.setColor(new Color(10, 10, 25));
                    g.fillRect(x, y, cellSize, cellSize);
                    g.setColor(new Color(255, 100, 200));
                    g.setStroke(new BasicStroke(3));
                    g.drawLine(x + 2, y + cellSize / 2, x + cellSize - 2, y + cellSize / 2);
                } else {
                    // Walkable path — dark background
                    g.setColor(new Color(10, 10, 25));
                    g.fillRect(x, y, cellSize, cellSize);

                    // Subtle grid lines for path visibility
                    g.setColor(new Color(18, 18, 38));
                    g.setStroke(new BasicStroke(1));
                    g.drawRect(x, y, cellSize, cellSize);

                    if (tileType == GameMap.DOT) {
                        if (isCornerPosition(row, col)) {
                            // Power pellet - larger, glowing
                            int dotSize = 8;
                            g.setColor(new Color(255, 255, 220, 200));
                            g.fillOval(x + cellSize / 2 - dotSize / 2 - 1, y + cellSize / 2 - dotSize / 2 - 1,
                                    dotSize + 2, dotSize + 2);
                            g.setColor(Color.WHITE);
                            g.fillOval(x + cellSize / 2 - dotSize / 2, y + cellSize / 2 - dotSize / 2, dotSize,
                                    dotSize);
                        } else {
                            // Regular pellet
                            g.setColor(new Color(255, 230, 180));
                            int dotSize = 3;
                            g.fillOval(x + cellSize / 2 - dotSize / 2, y + cellSize / 2 - dotSize / 2, dotSize,
                                    dotSize);
                        }
                    }
                }
            }
        }
    }

    /**
     * Draws a wall tile with nice styling — rounded inner edges and glow.
     */
    private void drawWall(Graphics2D g, int x, int y, int cellSize, int row, int col) {
        // Wall body — dark blue fill
        int glowR = (int) (33 + wallGlow * 15);
        int glowG = (int) (66 + wallGlow * 25);
        int glowB = (int) (255);
        g.setColor(new Color(glowR, glowG, glowB));
        g.fillRoundRect(x + 1, y + 1, cellSize - 2, cellSize - 2, 6, 6);

        // Wall border — lighter cyan
        int borderB = (int) (200 + wallGlow * 55);
        g.setColor(new Color(0, (int) (180 + wallGlow * 40), borderB));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x + 1, y + 1, cellSize - 2, cellSize - 2, 6, 6);
    }

    /**
     * Determines if a position is in a corner (for power pellet placement).
     */
    private boolean isCornerPosition(int row, int col) {
        int rows = gameMap.getRows();
        int cols = gameMap.getCols();
        int cornerThreshold = 3;
        return (row <= cornerThreshold || row >= rows - cornerThreshold - 1) &&
                (col <= cornerThreshold || col >= cols - cornerThreshold - 1);
    }

    /**
     * Draws Pac-Man with animated mouth.
     */
    private void drawPacMan(Graphics2D g) {
        int cellSize = gameMap.getCellSize();
        int x = pacMan.getCol() * cellSize + 2;
        int y = pacMan.getRow() * cellSize + 2;
        int size = cellSize - 4;

        // Glow effect behind Pac-Man
        g.setColor(new Color(255, 255, 0, 40));
        g.fillOval(x - 3, y - 3, size + 6, size + 6);

        g.setColor(Color.YELLOW);
        double mouthAngle = pacMan.getMouthAngle();

        int startAngle = 0;
        int arcAngle = 360;

        switch (pacMan.getCurrentDirection()) {
            case PacMan.RIGHT:
                startAngle = (int) mouthAngle;
                arcAngle = (int) (360 - 2 * mouthAngle);
                break;
            case PacMan.LEFT:
                startAngle = (int) (180 - mouthAngle);
                arcAngle = (int) (360 - 2 * mouthAngle);
                break;
            case PacMan.UP:
                startAngle = (int) (270 - mouthAngle);
                arcAngle = (int) (360 - 2 * mouthAngle);
                break;
            case PacMan.DOWN:
                startAngle = (int) (90 - mouthAngle);
                arcAngle = (int) (360 - 2 * mouthAngle);
                break;
        }

        g.fillArc(x, y, size, size, startAngle, arcAngle);

        // Eye
        g.setColor(Color.BLACK);
        int eyeSize = 3;
        int eyeX = x + size / 3;
        int eyeY = y + size / 4;
        g.fillOval(eyeX, eyeY, eyeSize, eyeSize);
    }

    /**
     * Draws a single ghost with colored body, semicircle head, wavy bottom, and
     * eyes.
     */
    private void drawGhost(Graphics2D g, Ghost ghost) {
        int cellSize = gameMap.getCellSize();
        int x = ghost.getCol() * cellSize + 2;
        int y = ghost.getRow() * cellSize + 2;
        int size = cellSize - 4;
        int bodyHeight = size / 2;

        java.awt.Color ghostBodyColor;
        switch (ghost.getGhostColor()) {
            case RED:
                ghostBodyColor = Color.RED;
                break;
            case PINK:
                ghostBodyColor = new Color(255, 184, 255);
                break;
            case CYAN:
                ghostBodyColor = Color.CYAN;
                break;
            case ORANGE:
                ghostBodyColor = new Color(255, 165, 0);
                break;
            default:
                ghostBodyColor = Color.WHITE;
        }

        g.setColor(ghostBodyColor);

        // Body
        g.fillRect(x, y + bodyHeight / 2, size, bodyHeight);

        // Head
        g.fillArc(x, y - bodyHeight / 2, size, bodyHeight, 0, 180);

        // Wavy bottom
        int waveCount = 3;
        int waveWidth = size / waveCount;
        for (int i = 0; i < waveCount; i++) {
            int waveX = x + (i * waveWidth);
            int waveY = y + bodyHeight / 2;
            g.fillArc(waveX, waveY, waveWidth, waveWidth / 2, 0, 180);
        }

        // Eyes
        g.setColor(Color.WHITE);
        int eyeRadius = 3;
        int eyeSpacing = size / 3;

        int leftEyeX = x + eyeSpacing - eyeRadius;
        int leftEyeY = y + bodyHeight / 4 - eyeRadius;
        g.fillOval(leftEyeX, leftEyeY, eyeRadius * 2, eyeRadius * 2);

        int rightEyeX = x + size - eyeSpacing - eyeRadius;
        int rightEyeY = y + bodyHeight / 4 - eyeRadius;
        g.fillOval(rightEyeX, rightEyeY, eyeRadius * 2, eyeRadius * 2);

        // Pupils
        g.setColor(Color.BLACK);
        int pupilRadius = 1;
        g.fillOval(leftEyeX + eyeRadius - pupilRadius, leftEyeY + eyeRadius - pupilRadius,
                pupilRadius * 2, pupilRadius * 2);
        g.fillOval(rightEyeX + eyeRadius - pupilRadius, rightEyeY + eyeRadius - pupilRadius,
                pupilRadius * 2, pupilRadius * 2);

        // Outline
        g.setColor(ghostBodyColor.darker());
        g.setStroke(new BasicStroke(1));
        g.drawRect(x, y + bodyHeight / 2, size, bodyHeight);
        g.drawArc(x, y - bodyHeight / 2, size, bodyHeight, 0, 180);
    }

    /**
     * Draws the level-up card overlay.
     */
    private void drawLevelCard(Graphics2D g) {
        int w = getWidth();
        int h = getHeight();

        // Dark overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, w, h);

        // Card
        int cardW = 300;
        int cardH = 160;
        int cardX = (w - cardW) / 2;
        int cardY = (h - cardH) / 2;

        // Card background with gradient
        GradientPaint cardGrad = new GradientPaint(cardX, cardY, new Color(15, 15, 50), cardX, cardY + cardH,
                new Color(25, 25, 80));
        g.setPaint(cardGrad);
        g.fillRoundRect(cardX, cardY, cardW, cardH, 24, 24);

        // Card border
        g.setColor(new Color(80, 180, 255));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 24, 24);

        // "LEVEL X"
        g.setFont(new Font("Monospaced", Font.BOLD, 42));
        String levelText = "LEVEL " + level;
        FontMetrics fm = g.getFontMetrics();
        int textX = cardX + (cardW - fm.stringWidth(levelText)) / 2;
        g.setColor(Color.YELLOW);
        g.drawString(levelText, textX, cardY + 70);

        // "GET READY!"
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        fm = g.getFontMetrics();
        String subtitle = "GET READY!";
        int subX = cardX + (cardW - fm.stringWidth(subtitle)) / 2;
        g.setColor(Color.WHITE);
        g.drawString(subtitle, subX, cardY + 110);

        // Speed indicator
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fm = g.getFontMetrics();
        String speedText = "Speed: " + (100 - ((gameSpeed - MIN_SPEED) * 100 / (150 - MIN_SPEED))) + "%";
        int speedX = cardX + (cardW - fm.stringWidth(speedText)) / 2;
        g.setColor(new Color(150, 200, 255));
        g.drawString(speedText, speedX, cardY + 140);
    }

    /**
     * Draws the game over overlay with restart prompt.
     */
    private void drawGameOver(Graphics2D g) {
        int w = getWidth();
        int h = getHeight();

        // Dark red overlay
        g.setColor(new Color(80, 0, 0, 170));
        g.fillRect(0, 0, w, h);

        // Card
        int cardW = 320;
        int cardH = 200;
        int cardX = (w - cardW) / 2;
        int cardY = (h - cardH) / 2;

        g.setColor(new Color(20, 5, 5, 230));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 24, 24);
        g.setColor(new Color(255, 60, 60));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 24, 24);

        // "GAME OVER"
        g.setFont(new Font("Monospaced", Font.BOLD, 38));
        FontMetrics fm = g.getFontMetrics();
        String text = "GAME OVER";
        int textX = cardX + (cardW - fm.stringWidth(text)) / 2;
        g.setColor(new Color(255, 60, 60));
        g.drawString(text, textX, cardY + 55);

        // Score
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        fm = g.getFontMetrics();
        String scoreText = "SCORE: " + score;
        int scoreX = cardX + (cardW - fm.stringWidth(scoreText)) / 2;
        g.setColor(Color.YELLOW);
        g.drawString(scoreText, scoreX, cardY + 95);

        // Level
        String lvlText = "LEVEL: " + level;
        int lvlX = cardX + (cardW - fm.stringWidth(lvlText)) / 2;
        g.setColor(Color.CYAN);
        g.drawString(lvlText, lvlX, cardY + 125);

        // Restart prompt
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        fm = g.getFontMetrics();
        String restart = "PRESS [R] TO RESTART";
        int restartX = cardX + (cardW - fm.stringWidth(restart)) / 2;
        // Blinking effect
        if ((System.currentTimeMillis() / 500) % 2 == 0) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(new Color(150, 150, 150));
        }
        g.drawString(restart, restartX, cardY + 170);
    }

    /**
     * Checks for collisions between Pac-Man and ghosts.
     */
    private void checkCollisions() {
        for (Ghost ghost : ghosts) {
            // Only collide with active ghosts
            if (ghost.getState() != Ghost.State.CHASING && ghost.getState() != Ghost.State.SCATTER) {
                continue;
            }
            if (ghost.collidesWith(pacMan.getRow(), pacMan.getCol())) {
                lives--;
                System.out.println("COLLISION! Lives remaining: " + lives);

                if (lives <= 0) {
                    gameOver = true;
                    gameTimer.stop();
                    repaint(); // Force repaint to show game over
                    return;
                }

                // Reset positions — spawn far from ghosts
                pacMan.resetPosition(17, 9);
                for (Ghost g : ghosts) {
                    g.resetPosition();
                }
                if (!ghosts.isEmpty()) {
                    ghosts.get(0).setState(Ghost.State.CHASING);
                }
                frameCount = 0;
                return;
            }
        }
    }

    // ==================== KEY INPUT ====================

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // Restart on R when game over
        if (gameOver && keyCode == KeyEvent.VK_R) {
            restartGame();
            return;
        }

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
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Returns the scoreboard height for window sizing.
     */
    public static int getScoreboardHeight() {
        return SCOREBOARD_HEIGHT;
    }
}
