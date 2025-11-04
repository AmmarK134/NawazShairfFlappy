package com.example.flappy;

/**
 * Centralized game constants for tuning gameplay and rendering.
 */
public final class Constants {
    private Constants() {} // Prevent instantiation

    // Window dimensions (portrait phone-style)
    public static final int WINDOW_WIDTH = 432;
    public static final int WINDOW_HEIGHT = 768;

    // Game loop
    public static final int TARGET_FPS = 60;
    public static final int TIMER_DELAY_MS = 1000 / TARGET_FPS; // ~16ms

    // Bird physics
    public static final float GRAVITY = 0.5f;
    public static final float FLAP_IMPULSE = -8.0f;
    public static final float TERMINAL_VELOCITY = 12.0f;
    public static final int BIRD_WIDTH = 34;
    public static final int BIRD_HEIGHT = 24;
    public static final int BIRD_START_X = WINDOW_WIDTH / 4;
    public static final int BIRD_START_Y = WINDOW_HEIGHT / 2;

    // Pipes
    public static final int PIPE_WIDTH = 52;
    public static final int PIPE_GAP_HEIGHT = 150;
    public static final int PIPE_SPEED = 3;
    public static final int PIPE_SPAWN_INTERVAL = 90; // frames between spawns
    public static final int PIPE_MIN_GAP_Y = 150;
    public static final int PIPE_MAX_GAP_Y = WINDOW_HEIGHT - 200;

    // Ground
    public static final int GROUND_HEIGHT = 112;
    public static final int GROUND_Y = WINDOW_HEIGHT - GROUND_HEIGHT;

    // Bird rotation (visual)
    public static final float MAX_ROTATION_DEGREES = 30.0f;
    public static final float ROTATION_SPEED = 3.0f;

    // High score file
    public static final String HIGH_SCORE_DIR = ".flappybird";
    public static final String HIGH_SCORE_FILE = "highscore.txt";
}

