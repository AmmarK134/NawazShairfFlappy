package com.example.flappy;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages sound playback with pre-loaded clips for performance.
 */
public class SoundPlayer {
    private static final Map<String, Clip> soundCache = new HashMap<>();
    private static boolean soundsEnabled = true;

    /**
     * Pre-load a sound file into memory.
     */
    public static void loadSound(String name, String resourcePath) {
        if (soundCache.containsKey(name)) {
            return; // Already loaded
        }

        try (InputStream is = SoundPlayer.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Sound resource not found: " + resourcePath);
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            soundCache.put(name, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Failed to load sound: " + name + " (" + resourcePath + ")");
            e.printStackTrace();
        }
    }

    /**
     * Play a pre-loaded sound. If not loaded, attempts to load it first.
     */
    public static void play(String name) {
        if (!soundsEnabled) {
            return;
        }

        Clip clip = soundCache.get(name);
        if (clip == null) {
            // Try to load on-demand
            loadSound(name, "/sounds/" + name + ".wav");
            clip = soundCache.get(name);
            if (clip == null) {
                return;
            }
        }

        // Reset and play
        clip.setFramePosition(0);
        clip.start();
    }

    /**
     * Enable or disable sound playback.
     */
    public static void setSoundsEnabled(boolean enabled) {
        soundsEnabled = enabled;
    }

    /**
     * Clean up all loaded clips.
     */
    public static void cleanup() {
        for (Clip clip : soundCache.values()) {
            if (clip != null && clip.isOpen()) {
                clip.close();
            }
        }
        soundCache.clear();
    }
}

