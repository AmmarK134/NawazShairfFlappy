package com.example.flappy;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

/**
 * Main game panel handling game loop, rendering, input, and game logic.
 */
public class GamePanel extends JPanel {
    private GameState state;
    private Bird bird;
    private PipeManager pipeManager;
    private BufferedImage background;
    private BufferedImage ground;
    private int score;
    private int highScore;
    private Timer gameTimer;
    private SelectionPanel selectionPanel;
    private boolean playAgainHover = false;
    private boolean quitHover = false;

    public GamePanel() {
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
        setFocusable(true);
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setLayout(null); // Use null layout for absolute positioning
        
        state = GameState.SELECT;
        score = 0;
        highScore = HighScoreStore.loadHighScore();
        
        loadAssets();
        setupSelectionPanel();
        setupInputHandlers();
    }

    private void loadAssets() {
        background = AssetLoader.loadImage("/images/background.png");
        ground = AssetLoader.loadImage("/images/ground.png");
        
        BufferedImage pipeTop = AssetLoader.loadImage("/images/pipe_top.png");
        BufferedImage pipeBottom = AssetLoader.loadImage("/images/pipe_bottom.png");
        pipeManager = new PipeManager(pipeTop, pipeBottom);

        // Pre-load sounds
        SoundPlayer.loadSound("flap", "/sounds/flap.wav");
        SoundPlayer.loadSound("point", "/sounds/point.wav");
        SoundPlayer.loadSound("hit", "/sounds/hit.wav");
        SoundPlayer.loadSound("die", "/sounds/die.wav");
        SoundPlayer.loadSound("hitaudio", "/sounds/hitaudio.wav"); // Pipe collision sound
        SoundPlayer.loadSound("audiobackground", "/sounds/audiobackground.wav"); // Background music
    }

    private void setupSelectionPanel() {
        selectionPanel = new SelectionPanel();
        selectionPanel.setOnStartCallback(this::startGame);
        selectionPanel.setBounds(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        selectionPanel.setVisible(true);
        add(selectionPanel);
    }

    private void startGame() {
        remove(selectionPanel);
        BufferedImage birdSprite = selectionPanel.getSelectedBirdSprite();
        bird = new Bird(birdSprite);
        
        state = GameState.PLAYING;
        score = 0;
        pipeManager.reset();
        
        // Start background music
        SoundPlayer.playBackgroundMusic("audiobackground");
        
        // Start game loop
        gameTimer = new Timer(Constants.TIMER_DELAY_MS, e -> {
            if (state == GameState.PLAYING) {
                updateGame();
            }
            repaint();
        });
        gameTimer.start();
        
        revalidate();
        repaint();
        requestFocusInWindow();
    }

    private void setupInputHandlers() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                
                if (state == GameState.PLAYING) {
                    if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_UP) {
                        flapBird();
                    } else if (key == KeyEvent.VK_P) {
                        togglePause();
                    }
                } else if (state == GameState.PAUSED) {
                    if (key == KeyEvent.VK_P) {
                        togglePause();
                    } else if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_UP) {
                        togglePause();
                        flapBird();
                    }
                } else if (state == GameState.GAME_OVER) {
                    if (key == KeyEvent.VK_R) {
                        restartGame();
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Only handle clicks if not in SELECT state (SelectionPanel handles that)
                if (state == GameState.SELECT) {
                    return; // Let SelectionPanel handle it
                }
                if (state == GameState.PLAYING || state == GameState.PAUSED) {
                    flapBird();
                    if (state == GameState.PAUSED) {
                        togglePause();
                    }
                } else if (state == GameState.GAME_OVER) {
                    handleGameOverClick(e.getX(), e.getY());
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (state == GameState.GAME_OVER) {
                    boolean wasPlayAgainHover = playAgainHover;
                    boolean wasQuitHover = quitHover;
                    
                    playAgainHover = isPlayAgainButton(e.getX(), e.getY());
                    quitHover = isQuitButton(e.getX(), e.getY());
                    
                    if (wasPlayAgainHover != playAgainHover || wasQuitHover != quitHover) {
                        repaint();
                    }
                }
            }
        });
    }

    private void flapBird() {
        if (state == GameState.PLAYING && bird != null) {
            bird.flap();
            SoundPlayer.play("flap");
        }
    }

    private void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
        }
        repaint();
    }

    private void updateGame() {
        // Update bird
        bird.update();

        // Update pipes
        pipeManager.update();

        // Check scoring
        int newPoints = pipeManager.checkScoring(bird);
        if (newPoints > 0) {
            score += newPoints;
            SoundPlayer.play("point");
            if (score > highScore) {
                highScore = score;
                HighScoreStore.saveHighScore(highScore);
            }
        }

        // Check collisions
        if (pipeManager.checkCollision(bird)) {
            // Stop background music and play hit sound when hitting a pipe
            SoundPlayer.stopBackgroundMusic();
            SoundPlayer.play("hitaudio");
            gameOver();
            return;
        }

        // Check ground collision (at the very bottom)
        int groundY = Constants.WINDOW_HEIGHT - Constants.GROUND_HEIGHT;
        if (bird.getY() + bird.getHeight() >= groundY) {
            gameOver();
            return;
        }

        // Check ceiling collision (optional, but prevents going off-screen top)
        if (bird.getY() < 0) {
            gameOver();
        }
    }

    private void gameOver() {
        state = GameState.GAME_OVER;
        // Stop background music
        SoundPlayer.stopBackgroundMusic();
        SoundPlayer.play("hit");
        SoundPlayer.play("die");
        if (gameTimer != null) {
            gameTimer.stop();
        }
        repaint();
    }

    private void restartGame() {
        bird.reset();
        pipeManager.reset();
        score = 0;
        state = GameState.PLAYING;
        // Restart background music
        SoundPlayer.playBackgroundMusic("audiobackground");
        if (gameTimer != null) {
            gameTimer.start();
        }
        repaint();
    }

    private void handleGameOverClick(int x, int y) {
        if (isPlayAgainButton(x, y)) {
            restartGame();
        } else if (isQuitButton(x, y)) {
            System.exit(0);
        }
    }

    private boolean isPlayAgainButton(int x, int y) {
        int btnX = Constants.WINDOW_WIDTH / 2 - 80;
        int btnY = Constants.WINDOW_HEIGHT / 2 + 50;
        return x >= btnX && x < btnX + 160 && y >= btnY && y < btnY + 40;
    }

    private boolean isQuitButton(int x, int y) {
        int btnX = Constants.WINDOW_WIDTH / 2 - 80;
        int btnY = Constants.WINDOW_HEIGHT / 2 + 100;
        return x >= btnX && x < btnX + 160 && y >= btnY && y < btnY + 40;
    }

    public void cleanup() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        SoundPlayer.cleanup();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (state == GameState.SELECT) {
            // Selection panel handles its own rendering
            super.paintComponent(g);
            return;
        }
        
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        if (background != null) {
            g2d.drawImage(background, 0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, null);
        } else {
            g2d.setColor(new Color(135, 206, 235));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw pipes
        if (pipeManager != null) {
            pipeManager.render(g2d);
        }

        // Draw ground at the very bottom
        int groundY = Constants.WINDOW_HEIGHT - Constants.GROUND_HEIGHT;
        if (ground != null) {
            int groundWidth = ground.getWidth();
            int offset = (int) (System.currentTimeMillis() / 10) % groundWidth;
            for (int x = -offset; x < Constants.WINDOW_WIDTH; x += groundWidth) {
                g2d.drawImage(ground, x, groundY, null);
            }
        } else {
            g2d.setColor(new Color(139, 90, 43)); // Brown
            g2d.fillRect(0, groundY, Constants.WINDOW_WIDTH, Constants.GROUND_HEIGHT);
        }

        // Draw bird
        if (bird != null) {
            bird.render(g2d);
        }

        // Draw HUD
        drawHUD(g2d);

        // Draw overlays
        if (state == GameState.PAUSED) {
            drawPauseOverlay(g2d);
        } else if (state == GameState.GAME_OVER) {
            drawGameOverOverlay(g2d);
        }
    }

    private void drawHUD(Graphics2D g) {
        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String scoreText = String.valueOf(score);
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        
        // Shadow
        g.setColor(Color.BLACK);
        g.drawString(scoreText, (Constants.WINDOW_WIDTH - scoreWidth) / 2 + 2, 62);
        g.setColor(Color.WHITE);
        g.drawString(scoreText, (Constants.WINDOW_WIDTH - scoreWidth) / 2, 60);
    }

    private void drawPauseOverlay(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Pause text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String text = "PAUSED";
        int width = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (Constants.WINDOW_WIDTH - width) / 2, Constants.WINDOW_HEIGHT / 2);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String hint = "Press P to resume";
        int hintWidth = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, (Constants.WINDOW_WIDTH - hintWidth) / 2, Constants.WINDOW_HEIGHT / 2 + 40);
    }

    private void drawGameOverOverlay(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Game Over text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String gameOverText = "GAME OVER";
        int gameOverWidth = g.getFontMetrics().stringWidth(gameOverText);
        g.drawString(gameOverText, (Constants.WINDOW_WIDTH - gameOverWidth) / 2, Constants.WINDOW_HEIGHT / 2 - 60);

        // Score
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String scoreText = "Score: " + score;
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        g.drawString(scoreText, (Constants.WINDOW_WIDTH - scoreWidth) / 2, Constants.WINDOW_HEIGHT / 2 - 10);

        // High Score
        String highScoreText = "Best: " + highScore;
        int highScoreWidth = g.getFontMetrics().stringWidth(highScoreText);
        g.drawString(highScoreText, (Constants.WINDOW_WIDTH - highScoreWidth) / 2, Constants.WINDOW_HEIGHT / 2 + 20);

        // Play Again button
        int btnX = Constants.WINDOW_WIDTH / 2 - 80;
        int btnY = Constants.WINDOW_HEIGHT / 2 + 50;
        g.setColor(playAgainHover ? new Color(0, 200, 0) : new Color(0, 150, 0));
        g.fillRoundRect(btnX, btnY, 160, 40, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String btnText = "Play Again";
        int btnTextWidth = g.getFontMetrics().stringWidth(btnText);
        g.drawString(btnText, btnX + (160 - btnTextWidth) / 2, btnY + 28);

        // Quit button
        btnY = Constants.WINDOW_HEIGHT / 2 + 100;
        g.setColor(quitHover ? new Color(200, 0, 0) : new Color(150, 0, 0));
        g.fillRoundRect(btnX, btnY, 160, 40, 10, 10);
        g.setColor(Color.WHITE);
        String quitText = "Quit";
        int quitTextWidth = g.getFontMetrics().stringWidth(quitText);
        g.drawString(quitText, btnX + (160 - quitTextWidth) / 2, btnY + 28);

        // Hint
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        String hint = "Or press R to restart";
        int hintWidth = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, (Constants.WINDOW_WIDTH - hintWidth) / 2, Constants.WINDOW_HEIGHT / 2 + 160);
    }
}

