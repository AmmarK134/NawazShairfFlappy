package com.example.flappy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility to generate simple placeholder WAV sound files.
 * Run this once to create minimal sound effects.
 */
public class SoundGenerator {
    public static void main(String[] args) {
        String soundsDir = "src/main/resources/sounds";
        new File(soundsDir).mkdirs();

        // Generate simple beep sounds
        generateBeep(soundsDir + "/flap.wav", 440, 50); // Short high beep
        generateBeep(soundsDir + "/point.wav", 880, 100); // Higher beep for scoring
        generateBeep(soundsDir + "/hit.wav", 220, 150); // Lower beep for collision
        generateBeep(soundsDir + "/die.wav", 110, 200); // Lowest beep for death

        System.out.println("Placeholder sound files generated in " + soundsDir);
    }

    /**
     * Generate a simple beep WAV file.
     * @param filename Output file path
     * @param frequency Frequency in Hz
     * @param duration Duration in milliseconds
     */
    private static void generateBeep(String filename, int frequency, int duration) {
        try (FileOutputStream out = new FileOutputStream(filename)) {
            int sampleRate = 44100;
            int numSamples = sampleRate * duration / 1000;
            byte[] data = new byte[44 + numSamples * 2]; // WAV header + 16-bit samples

            // WAV header
            writeString(out, "RIFF");
            writeInt(out, 36 + numSamples * 2);
            writeString(out, "WAVE");
            writeString(out, "fmt ");
            writeInt(out, 16); // fmt chunk size
            writeShort(out, (short) 1); // audio format (PCM)
            writeShort(out, (short) 1); // number of channels
            writeInt(out, sampleRate); // sample rate
            writeInt(out, sampleRate * 2); // byte rate
            writeShort(out, (short) 2); // block align
            writeShort(out, (short) 16); // bits per sample
            writeString(out, "data");
            writeInt(out, numSamples * 2); // data chunk size

            // Generate sine wave samples
            for (int i = 0; i < numSamples; i++) {
                double angle = 2.0 * Math.PI * i * frequency / sampleRate;
                short sample = (short) (Math.sin(angle) * 32767 * 0.5); // 50% volume
                out.write(sample & 0xFF);
                out.write((sample >> 8) & 0xFF);
            }
        } catch (IOException e) {
            System.err.println("Failed to generate sound: " + filename);
            e.printStackTrace();
        }
    }

    private static void writeString(FileOutputStream out, String s) throws IOException {
        out.write(s.getBytes("ASCII"));
    }

    private static void writeInt(FileOutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }

    private static void writeShort(FileOutputStream out, short value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
    }
}

