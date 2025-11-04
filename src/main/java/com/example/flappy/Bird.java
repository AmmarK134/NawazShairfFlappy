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

    public Bird(BufferedImage sprite) {
        this.sprite = sprite;
        reset();
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
        int centerX = (int) x + Constants.BIRD_WIDTH / 2;
        int centerY = (int) y + Constants.BIRD_HEIGHT / 2;
        
        g.translate(centerX, centerY);
        g.rotate(Math.toRadians(rotation));
        g.translate(-centerX, -centerY);
        
        g.drawImage(sprite, (int) x, (int) y, Constants.BIRD_WIDTH, Constants.BIRD_HEIGHT, null);
        
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
        return Constants.BIRD_WIDTH;
    }

    public int getHeight() {
        return Constants.BIRD_HEIGHT;
    }

    public float getVelY() {
        return velY;
    }
}

