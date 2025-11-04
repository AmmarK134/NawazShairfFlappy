package com.example.flappy;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

/**
 * Panel for selecting bird skin before starting the game.
 */
public class SelectionPanel extends JPanel {
    private static final int BIRD_COUNT = 3;
    private BufferedImage[] birdSprites;
    private BufferedImage background;
    private int selectedIndex = -1;
    private Runnable onStartCallback;
    private boolean startButtonHover = false;

    public SelectionPanel() {
        setDoubleBuffered(true);
        setOpaque(true);
        setBackground(Color.CYAN);
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        loadAssets();
        setupMouseListener();
    }

    private void loadAssets() {
        background = AssetLoader.loadImage("/images/background.png");
        birdSprites = new BufferedImage[BIRD_COUNT];
        for (int i = 0; i < BIRD_COUNT; i++) {
            birdSprites[i] = AssetLoader.loadImage("/images/bird" + (i + 1) + ".png");
        }
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // Check bird selection
                int startX = Constants.WINDOW_WIDTH / 2 - (BIRD_COUNT * 80) / 2;
                for (int i = 0; i < BIRD_COUNT; i++) {
                    int birdX = startX + i * 80;
                    int birdY = Constants.WINDOW_HEIGHT / 2 - 40;
                    if (x >= birdX && x < birdX + 60 && y >= birdY && y < birdY + 60) {
                        selectedIndex = i;
                        repaint();
                        return;
                    }
                }

                // Check start button
                if (selectedIndex >= 0 && isStartButtonHover(e.getX(), e.getY())) {
                    if (onStartCallback != null) {
                        onStartCallback.run();
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean wasHover = startButtonHover;
                startButtonHover = selectedIndex >= 0 && isStartButtonHover(e.getX(), e.getY());
                if (wasHover != startButtonHover) {
                    repaint();
                }
            }
        });
    }

    private boolean isStartButtonHover(int x, int y) {
        int btnX = Constants.WINDOW_WIDTH / 2 - 80;
        int btnY = Constants.WINDOW_HEIGHT - 150;
        return x >= btnX && x < btnX + 160 && y >= btnY && y < btnY + 40;
    }

    public void setOnStartCallback(Runnable callback) {
        this.onStartCallback = callback;
    }

    public BufferedImage getSelectedBirdSprite() {
        if (selectedIndex >= 0 && selectedIndex < birdSprites.length && birdSprites[selectedIndex] != null) {
            return birdSprites[selectedIndex];
        }
        // Default to first bird if available, otherwise return null (game will handle it)
        return birdSprites.length > 0 && birdSprites[0] != null ? birdSprites[0] : null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        if (background != null) {
            g2d.drawImage(background, 0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, null);
        } else {
            g2d.setColor(new Color(135, 206, 235)); // Sky blue
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Title
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        String title = "Select Bird";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (Constants.WINDOW_WIDTH - titleWidth) / 2, 100);

        // Draw bird options
        int startX = Constants.WINDOW_WIDTH / 2 - (BIRD_COUNT * 80) / 2;
        for (int i = 0; i < BIRD_COUNT; i++) {
            int birdX = startX + i * 80;
            int birdY = Constants.WINDOW_HEIGHT / 2 - 40;

            // Highlight selected
            if (i == selectedIndex) {
                g2d.setColor(new Color(255, 255, 0, 100));
                g2d.fillOval(birdX - 5, birdY - 5, 70, 70);
            }

            // Draw bird thumbnail
            if (birdSprites[i] != null) {
                g2d.drawImage(birdSprites[i], birdX, birdY, 60, 60, null);
            } else {
                // Fallback colored rectangle
                g2d.setColor(new Color(255, 165, 0));
                g2d.fillRect(birdX, birdY, 60, 60);
            }
        }

        // Start button
        if (selectedIndex >= 0) {
            int btnX = Constants.WINDOW_WIDTH / 2 - 80;
            int btnY = Constants.WINDOW_HEIGHT - 150;
            
            g2d.setColor(startButtonHover ? new Color(0, 200, 0) : new Color(0, 150, 0));
            g2d.fillRoundRect(btnX, btnY, 160, 40, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String btnText = "Start Game";
            int btnTextWidth = g2d.getFontMetrics().stringWidth(btnText);
            g2d.drawString(btnText, btnX + (160 - btnTextWidth) / 2, btnY + 28);
        } else {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            String hint = "Click a bird to select";
            int hintWidth = g2d.getFontMetrics().stringWidth(hint);
            g2d.drawString(hint, (Constants.WINDOW_WIDTH - hintWidth) / 2, Constants.WINDOW_HEIGHT - 120);
        }
    }
}

