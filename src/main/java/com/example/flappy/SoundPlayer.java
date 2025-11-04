package com.example.flappy;

import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SoundPlayer {
    private static final Map<String, byte[]> soundBytes = new HashMap<>(); // cache raw bytes
    private static Clip bgClip = null;
    private static boolean soundsEnabled = true;

    // ---------- Public API ----------

    public static void setSoundsEnabled(boolean enabled) {
        soundsEnabled = enabled;
        if (!enabled) stopBackgroundMusic();
    }

    /** Preload a sound into memory (recommended at startup). */
    public static void loadSound(String name, String resourcePath) {
        if (soundBytes.containsKey(name)) return;
        byte[] data = readAll(resourcePath);
        if (data != null) soundBytes.put(name, data);
    }

    /** Fire-and-forget SFX (creates a short-lived Clip each time so sounds can overlap). */
    public static void play(String name) {
        if (!soundsEnabled) return;
        
        byte[] data = soundBytes.get(name);
        if (data == null) {
            // try on-demand load: /sounds/<name>.wav
            data = readAll("/sounds/" + name + ".wav");
            if (data == null) {
                System.err.println("Sound not found: " + name);
                return;
            }
            soundBytes.put(name, data);
        }
        
        playFromBytesAsync(data);
    }

    /** Loop background music by name (expects /sounds/<name>.wav). */
    public static void playBackgroundMusic(String name) {
        if (!soundsEnabled) return;
        
        stopBackgroundMusic(); // stop old
        
        byte[] data = soundBytes.get(name);
        if (data == null) {
            data = readAll("/sounds/" + name + ".wav");
            if (data == null) {
                System.err.println("Background music not found: " + name);
                return;
            }
            soundBytes.put(name, data);
        }
        
        try {
            bgClip = createClipFromBytes(data);
            if (bgClip == null) {
                System.err.println("Failed to create bg clip: " + name);
                return;
            }
            
            bgClip.setFramePosition(0);
            bgClip.loop(Clip.LOOP_CONTINUOUSLY);
            // Some mixers require an explicit start after loop:
            if (!bgClip.isRunning()) bgClip.start();
            
            System.out.println("Background music started: " + name);
        } catch (Exception e) {
            System.err.println("Failed to start background music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void stopBackgroundMusic() {
        if (bgClip != null) {
            try {
                if (bgClip.isRunning()) bgClip.stop();
            } catch (Exception ignored) {}
            try {
                if (bgClip.isOpen()) bgClip.close();
            } catch (Exception ignored) {}
            bgClip = null;
        }
    }

    /** Close everything (e.g., on app exit). */
    public static void cleanup() {
        stopBackgroundMusic();
        soundBytes.clear();
    }

    // ---------- Internals ----------

    private static byte[] readAll(String resourcePath) {
        if (resourcePath == null || !resourcePath.startsWith("/")) {
            System.err.println("Resource path must start with '/': " + resourcePath);
            return null;
        }
        
        try (InputStream raw = SoundPlayer.class.getResourceAsStream(resourcePath)) {
            if (raw == null) {
                System.err.println("Resource not found: " + resourcePath);
                System.err.println("Debug: Check if file exists at: src/main/resources" + resourcePath);
                // Debug: try to see what resources are available
                System.err.println("Debug: Resource URL = " + SoundPlayer.class.getResource(resourcePath));
                return null;
            }
            
            try (BufferedInputStream bis = new BufferedInputStream(raw);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream(8192)) {
                
                byte[] buf = new byte[8192];
                int r;
                while ((r = bis.read(buf)) != -1) bos.write(buf, 0, r);
                
                byte[] data = bos.toByteArray();
                
                // Quick format sanity check up front (will throw if not WAV/PCM)
                try (AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data))) {
                    // ok if this succeeds; no need to read fully here
                }
                
                System.out.println("Loaded sound: " + resourcePath + " (" + data.length + " bytes)");
                return data;
            }
        } catch (UnsupportedAudioFileException uafe) {
            System.err.println("Unsupported audio (must be PCM WAV): " + resourcePath);
            System.err.println("Convert your file to PCM WAV format (16-bit, 44.1kHz recommended)");
            uafe.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("I/O reading sound: " + resourcePath);
            ioe.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected while reading: " + resourcePath);
            e.printStackTrace();
        }
        return null;
    }

    private static void playFromBytesAsync(byte[] data) {
        // Use a short worker thread so UI never stalls
        new Thread(() -> {
            Clip clip = null;
            try {
                clip = createClipFromBytes(data);
                if (clip == null) return;
                
                final Clip finalClip = clip; // Make final for lambda
                clip.setFramePosition(0);
                clip.start();
                
                // Let the thread live until playback ends, then close clip
                clip.addLineListener(ev -> {
                    if (ev.getType() == LineEvent.Type.STOP) {
                        try { finalClip.close(); } catch (Exception ignored) {}
                    }
                });
            } catch (Exception e) {
                System.err.println("SFX play failed: " + e.getMessage());
                if (clip != null) try { clip.close(); } catch (Exception ignored) {}
            }
        }, "SFX-Play").start();
    }

    private static Clip createClipFromBytes(byte[] data) throws Exception {
        // Wrap with BufferedInputStream to ensure mark/reset support
        try (BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data))) {
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
            AudioFormat baseFormat = ais.getFormat();
            
            // Ensure PCM signed; if not, convert
            AudioFormat decodedFormat = toPcmIfNeeded(baseFormat);
            AudioInputStream dais = (decodedFormat == baseFormat)
                    ? ais
                    : AudioSystem.getAudioInputStream(decodedFormat, ais);
            
            DataLine.Info info = new DataLine.Info(Clip.class, decodedFormat);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(dais); // clip buffers the audio; OK to let streams close after open
            return clip;
        }
    }

    private static AudioFormat toPcmIfNeeded(AudioFormat f) {
        if (f.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) return f;
        
        // Convert to 16-bit PCM signed, little endian
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                f.getSampleRate(),
                16,
                f.getChannels(),
                f.getChannels() * 2,
                f.getSampleRate(),
                false
        );
    }
}
