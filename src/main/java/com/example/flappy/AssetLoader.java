package com.example.flappy;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for loading image and sound assets from classpath resources.
 */
public class AssetLoader {
    /**
     * Load an image from the resources directory.
     * 
     * @param path Relative path from resources (e.g., "/images/bird1.png")
     * @return BufferedImage or null if not found
     */
    public static BufferedImage loadImage(String path) {
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("Resource not found: " + path);
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Show error dialog and exit if a critical asset is missing.
     */
    public static void checkCriticalAsset(String path, String assetName) {
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                JOptionPane.showMessageDialog(
                    null,
                    "Critical asset missing: " + assetName + "\nPath: " + path + "\n\nPlease ensure all assets are in src/main/resources",
                    "Asset Loading Error",
                    JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                "Failed to load asset: " + assetName + "\nError: " + e.getMessage(),
                "Asset Loading Error",
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
    }
}

