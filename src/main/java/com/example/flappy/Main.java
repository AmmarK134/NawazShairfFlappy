package com.example.flappy;

import javax.swing.SwingUtilities;

/**
 * Entry point for the Flappy Bird application.
 * Launches the game frame on the Event Dispatch Thread.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GameFrame();
            } catch (Exception e) {
                System.err.println("Failed to start game: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}

