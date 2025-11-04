package com.example.flappy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages high score persistence to a file in the user's home directory.
 */
public class HighScoreStore {
    private static final String HOME_DIR = System.getProperty("user.home");
    private static final Path HIGH_SCORE_DIR = Paths.get(HOME_DIR, Constants.HIGH_SCORE_DIR);
    private static final Path HIGH_SCORE_FILE = HIGH_SCORE_DIR.resolve(Constants.HIGH_SCORE_FILE);

    /**
     * Read the high score from file, or return 0 if file doesn't exist.
     */
    public static int loadHighScore() {
        if (!Files.exists(HIGH_SCORE_FILE)) {
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE.toFile()))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Failed to read high score: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Save the high score to file. Creates directory if it doesn't exist.
     */
    public static void saveHighScore(int score) {
        try {
            // Create directory if it doesn't exist
            if (!Files.exists(HIGH_SCORE_DIR)) {
                Files.createDirectories(HIGH_SCORE_DIR);
            }

            // Write score
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE.toFile()))) {
                writer.write(String.valueOf(score));
            }
        } catch (IOException e) {
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }
}

