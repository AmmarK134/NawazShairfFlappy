package com.example.flappy;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages pipe spawning, movement, and rendering.
 */
public class PipeManager {
    private List<Pipe> pipes;
    private BufferedImage pipeTop;
    private BufferedImage pipeBottom;
    private int spawnTimer;
    private Random random;

    public PipeManager(BufferedImage pipeTop, BufferedImage pipeBottom) {
        this.pipes = new ArrayList<>();
        this.pipeTop = pipeTop;
        this.pipeBottom = pipeBottom;
        this.spawnTimer = 0;
        this.random = new Random();
    }

    /**
     * Update all pipes: move them left, remove off-screen ones, spawn new ones.
     */
    public void update() {
        // Move all pipes
        for (Pipe pipe : pipes) {
            pipe.update(Constants.PIPE_SPEED);
        }

        // Remove off-screen pipes
        pipes.removeIf(Pipe::isOffScreen);

        // Spawn new pipe
        spawnTimer++;
        if (spawnTimer >= Constants.PIPE_SPAWN_INTERVAL) {
            spawnPipe();
            spawnTimer = 0;
        }
    }

    /**
     * Spawn a new pipe at the right edge with random gap position.
     * Creates varied vertical positions for more interesting gameplay.
     */
    private void spawnPipe() {
        // Create more varied gap positions - divide screen into zones for better distribution
        int range = Constants.PIPE_MAX_GAP_Y - Constants.PIPE_MIN_GAP_Y;
        int zone = random.nextInt(5); // 5 different zones (high, mid-high, middle, mid-low, low)
        int gapY;
        
        switch (zone) {
            case 0: // High position
                gapY = Constants.PIPE_MIN_GAP_Y + random.nextInt(range / 5);
                break;
            case 1: // Mid-high position
                gapY = Constants.PIPE_MIN_GAP_Y + (range / 5) + random.nextInt(range / 5);
                break;
            case 2: // Middle position
                gapY = Constants.PIPE_MIN_GAP_Y + (range * 2 / 5) + random.nextInt(range / 5);
                break;
            case 3: // Mid-low position
                gapY = Constants.PIPE_MIN_GAP_Y + (range * 3 / 5) + random.nextInt(range / 5);
                break;
            default: // Low position
                gapY = Constants.PIPE_MIN_GAP_Y + (range * 4 / 5) + random.nextInt(range / 5);
                break;
        }
        
        pipes.add(new Pipe(Constants.WINDOW_WIDTH, gapY, Constants.PIPE_WIDTH, Constants.PIPE_GAP_HEIGHT));
    }

    /**
     * Render all pipes.
     */
    public void render(Graphics2D g) {
        for (Pipe pipe : pipes) {
            int x = pipe.getX();
            int gapY = pipe.getGapY();
            int gapHeight = Constants.PIPE_GAP_HEIGHT;

            // Top pipe (flipped)
            int topPipeHeight = gapY - gapHeight / 2;
            if (topPipeHeight > 0 && pipeTop != null) {
                g.drawImage(pipeTop, x, 0, Constants.PIPE_WIDTH, topPipeHeight, null);
            }

            // Bottom pipe
            int bottomPipeTop = gapY + gapHeight / 2;
            int groundY = Constants.WINDOW_HEIGHT - Constants.GROUND_HEIGHT;
            int bottomPipeHeight = groundY - bottomPipeTop;
            if (bottomPipeHeight > 0 && pipeBottom != null) {
                g.drawImage(pipeBottom, x, bottomPipeTop, Constants.PIPE_WIDTH, bottomPipeHeight, null);
            }
        }
    }

    /**
     * Check collision between bird and any pipe.
     * More lenient collision - uses smaller collision box with padding.
     */
    public boolean checkCollision(Bird bird) {
        int birdX = bird.getX();
        int birdY = bird.getY();
        int birdWidth = bird.getWidth();
        int birdHeight = bird.getHeight();
        
        // More lenient padding - bird collision box is smaller than visual (more forgiving)
        int padding = 20; // Large padding for more forgiving collisions
        int birdLeft = birdX + padding;
        int birdRight = birdX + birdWidth - padding;
        int birdTop = birdY + padding;
        int birdBottom = birdY + birdHeight - padding;

        for (Pipe pipe : pipes) {
            int pipeX = pipe.getX();
            int pipeWidth = pipe.getWidth();
            int topPipeBottom = pipe.getTopPipeBottom();
            int bottomPipeTop = pipe.getBottomPipeTop();
            int groundY = Constants.WINDOW_HEIGHT - Constants.GROUND_HEIGHT;

            // Check if bird is horizontally overlapping with pipe
            if (birdRight > pipeX && birdLeft < pipeX + pipeWidth) {
                // Check collision with top pipe - bird must be inside the top pipe solid area
                if (birdTop < topPipeBottom && birdBottom > 0) {
                    return true;
                }
                
                // Check collision with bottom pipe - bird must be inside the bottom pipe solid area
                if (birdBottom > bottomPipeTop && birdTop < groundY) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if bird passed a pipe and update score.
     */
    public int checkScoring(Bird bird) {
        int score = 0;
        for (Pipe pipe : pipes) {
            if (pipe.hasPassed(bird.getX())) {
                pipe.setScored(true);
                score++;
            }
        }
        return score;
    }

    /**
     * Reset all pipes and spawn timer.
     */
    public void reset() {
        pipes.clear();
        spawnTimer = 0;
    }

    public List<Pipe> getPipes() {
        return pipes;
    }
}

