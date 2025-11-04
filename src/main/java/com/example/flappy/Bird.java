package com.example.flappy;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

/**
 * Bird entity with physics (gravity, velocity, flap mechanics).
 */
public class Bird {
    private float x;
    private float y;
    private float velY;
    private BufferedImage sprite;
    private float rotation; // degrees
    private int displayWidth;
    private int displayHeight;

    public Bird(BufferedImage sprite) {
        this.sprite = sprite;
        calculateDisplaySize();
        reset();
    }

    /**
     * Calculate display size maintaining aspect ratio from sprite.
     */
    private void calculateDisplaySize() {
        if (sprite != null) {
            int spriteWidth = sprite.getWidth();
            int spriteHeight = sprite.getHeight();
            if (spriteWidth > 0 && spriteHeight > 0) {
                // Calculate height to maintain aspect ratio
                displayWidth = Constants.BIRD_DISPLAY_WIDTH;
                displayHeight = (int) ((float) spriteHeight / spriteWidth * Constants.BIRD_DISPLAY_WIDTH);
            } else {
                displayWidth = Constants.BIRD_DISPLAY_WIDTH;
                displayHeight = Constants.BIRD_DISPLAY_WIDTH;
            }
        } else {
            displayWidth = Constants.BIRD_DISPLAY_WIDTH;
            displayHeight = Constants.BIRD_DISPLAY_WIDTH;
        }
    }

    /**
     * Reset bird to starting position and velocity.
     */
    public void reset() {
        this.x = Constants.BIRD_START_X;
        this.y = Constants.BIRD_START_Y;
        this.velY = 0;
        this.rotation = 0;
    }

    /**
     * Apply flap impulse (jump).
     */
    public void flap() {
        velY = Constants.FLAP_IMPULSE;
    }

    /**
     * Update physics: apply gravity, clamp velocity, update position.
     */
    public void update() {
        // Apply gravity
        velY += Constants.GRAVITY;

        // Clamp to terminal velocity
        if (velY > Constants.TERMINAL_VELOCITY) {
            velY = Constants.TERMINAL_VELOCITY;
        }

        // Update position
        y += velY;

        // Update rotation based on velocity (visual feedback)
        if (velY < 0) {
            rotation = Math.max(rotation - Constants.ROTATION_SPEED, -Constants.MAX_ROTATION_DEGREES);
        } else {
            rotation = Math.min(rotation + Constants.ROTATION_SPEED, Constants.MAX_ROTATION_DEGREES);
        }
    }

    /**
     * Render the bird with rotation.
     */
    public void render(Graphics2D g) {
        if (sprite == null) return;

        AffineTransform oldTransform = g.getTransform();
        
        // Translate to bird center, rotate, then translate back
        int centerX = (int) x + displayWidth / 2;
        int centerY = (int) y + displayHeight / 2;
        
        g.translate(centerX, centerY);
        g.rotate(Math.toRadians(rotation));
        g.translate(-centerX, -centerY);
        
        // Draw maintaining aspect ratio
        g.drawImage(sprite, (int) x, (int) y, displayWidth, displayHeight, null);
        
        g.setTransform(oldTransform);
    }

    /**
     * Get bounding rectangle for collision detection.
     */
    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public int getWidth() {
        return displayWidth;
    }

    public int getHeight() {
        return displayHeight;
    }

    public float getVelY() {
        return velY;
    }
}

