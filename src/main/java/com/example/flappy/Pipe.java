package com.example.flappy;

/**
 * Represents a single pipe pair (top and bottom).
 */
public class Pipe {
    private int x;
    private int gapY; // Y position of the gap center
    private int width;
    private int gapHeight;
    private boolean scored; // Whether the bird has passed this pipe

    public Pipe(int x, int gapY, int width, int gapHeight) {
        this.x = x;
        this.gapY = gapY;
        this.width = width;
        this.gapHeight = gapHeight;
        this.scored = false;
    }

    /**
     * Move pipe left by specified speed.
     */
    public void update(int speed) {
        x -= speed;
    }

    /**
     * Check if pipe is off-screen.
     */
    public boolean isOffScreen() {
        return x + width < 0;
    }

    /**
     * Get bounding rectangle for top pipe.
     */
    public int getTopPipeBottom() {
        return gapY - gapHeight / 2;
    }

    /**
     * Get bounding rectangle for bottom pipe.
     */
    public int getBottomPipeTop() {
        return gapY + gapHeight / 2;
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public int getGapY() {
        return gapY;
    }

    public boolean isScored() {
        return scored;
    }

    public void setScored(boolean scored) {
        this.scored = scored;
    }

    /**
     * Check if bird has passed the pipe center (for scoring).
     */
    public boolean hasPassed(int birdX) {
        return birdX > x + width && !scored;
    }
}

