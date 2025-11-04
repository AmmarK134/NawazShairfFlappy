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
     */
    private void spawnPipe() {
        int gapY = Constants.PIPE_MIN_GAP_Y + 
                   random.nextInt(Constants.PIPE_MAX_GAP_Y - Constants.PIPE_MIN_GAP_Y);
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
     * Uses proper AABB (Axis-Aligned Bounding Box) collision with small tolerance.
     */
    public boolean checkCollision(Bird bird) {
        int birdX = bird.getX();
        int birdY = bird.getY();
        int birdWidth = bird.getWidth();
        int birdHeight = bird.getHeight();
        
        // Add small padding to collision box for more forgiving collisions
        int padding = 5;
        int birdLeft = birdX + padding;
        int birdRight = birdX + birdWidth - padding;
        int birdTop = birdY + padding;
        int birdBottom = birdY + birdHeight - padding;

        for (Pipe pipe : pipes) {
            int pipeX = pipe.getX();
            int pipeWidth = pipe.getWidth();

            // Check if bird is horizontally aligned with pipe (with padding)
            if (birdRight > pipeX && birdLeft < pipeX + pipeWidth) {
                int topPipeBottom = pipe.getTopPipeBottom();
                int bottomPipeTop = pipe.getBottomPipeTop();

                // Check collision with top pipe (bird overlaps with pipe)
                if (birdBottom > 0 && birdTop < topPipeBottom) {
                    return true;
                }
                
                // Check collision with bottom pipe (bird overlaps with pipe)
                int groundY = Constants.WINDOW_HEIGHT - Constants.GROUND_HEIGHT;
                if (birdTop < groundY && birdBottom > bottomPipeTop) {
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

