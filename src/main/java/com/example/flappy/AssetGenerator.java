package com.example.flappy;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility to generate placeholder assets if they don't exist.
 * Run this once to create the required images and sounds directories.
 * 
 * Note: This class is for development setup. Actual game assets should be
 * placed in src/main/resources/images and src/main/resources/sounds.
 */
public class AssetGenerator {
    public static void main(String[] args) {
        String baseDir = "src/main/resources";
        generateImages(baseDir);
        System.out.println("Placeholder assets generated in " + baseDir);
        System.out.println("Note: Sound files (.wav) need to be created manually or obtained separately.");
    }

    private static void generateImages(String baseDir) {
        try {
            // Create directories
            new File(baseDir + "/images").mkdirs();
            new File(baseDir + "/sounds").mkdirs();

            // Background (sky gradient)
            BufferedImage bg = new BufferedImage(432, 768, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bg.createGraphics();
            g.setColor(new Color(135, 206, 235)); // Sky blue
            g.fillRect(0, 0, 432, 768);
            // Add some clouds
            g.setColor(new Color(255, 255, 255, 150));
            g.fillOval(50, 50, 100, 60);
            g.fillOval(150, 40, 120, 70);
            g.fillOval(300, 80, 90, 50);
            ImageIO.write(bg, "PNG", new File(baseDir + "/images/background.png"));
            g.dispose();

            // Bird sprites (3 different colored birds)
            Color[] birdColors = {new Color(255, 165, 0), new Color(255, 0, 0), new Color(0, 255, 0)};
            for (int i = 0; i < 3; i++) {
                BufferedImage bird = new BufferedImage(34, 24, BufferedImage.TYPE_INT_ARGB);
                g = bird.createGraphics();
                g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                                   java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                // Body (ellipse)
                g.setColor(birdColors[i]);
                g.fillOval(5, 5, 20, 15);
                // Wing
                g.setColor(new Color(birdColors[i].getRed(), birdColors[i].getGreen(), 
                                    birdColors[i].getBlue(), 200));
                g.fillOval(10, 10, 12, 8);
                // Eye
                g.setColor(Color.WHITE);
                g.fillOval(18, 8, 6, 6);
                g.setColor(Color.BLACK);
                g.fillOval(20, 9, 3, 3);
                // Beak
                g.setColor(new Color(255, 200, 0));
                g.fillPolygon(new int[]{25, 32, 25}, new int[]{12, 12, 15}, 3);
                ImageIO.write(bird, "PNG", new File(baseDir + "/images/bird" + (i + 1) + ".png"));
                g.dispose();
            }

            // Pipe top
            BufferedImage pipeTop = new BufferedImage(52, 320, BufferedImage.TYPE_INT_ARGB);
            g = pipeTop.createGraphics();
            g.setColor(new Color(0, 150, 0)); // Dark green
            g.fillRect(0, 0, 52, 320);
            g.setColor(new Color(0, 200, 0)); // Lighter green
            g.fillRect(2, 0, 48, 10);
            g.fillRect(2, 0, 48, 320);
            g.setColor(new Color(0, 100, 0)); // Darker green border
            g.drawRect(0, 0, 51, 319);
            ImageIO.write(pipeTop, "PNG", new File(baseDir + "/images/pipe_top.png"));
            g.dispose();

            // Pipe bottom
            BufferedImage pipeBottom = new BufferedImage(52, 320, BufferedImage.TYPE_INT_ARGB);
            g = pipeBottom.createGraphics();
            g.setColor(new Color(0, 150, 0));
            g.fillRect(0, 0, 52, 320);
            g.setColor(new Color(0, 200, 0));
            g.fillRect(2, 310, 48, 10);
            g.fillRect(2, 0, 48, 320);
            g.setColor(new Color(0, 100, 0));
            g.drawRect(0, 0, 51, 319);
            ImageIO.write(pipeBottom, "PNG", new File(baseDir + "/images/pipe_bottom.png"));
            g.dispose();

            // Ground (repeating pattern)
            BufferedImage ground = new BufferedImage(336, 112, BufferedImage.TYPE_INT_ARGB);
            g = ground.createGraphics();
            g.setColor(new Color(139, 90, 43)); // Brown
            g.fillRect(0, 0, 336, 112);
            // Add some grass texture
            g.setColor(new Color(0, 150, 0));
            for (int i = 0; i < 336; i += 10) {
                g.drawLine(i, 0, i + 5, 10);
            }
            ImageIO.write(ground, "PNG", new File(baseDir + "/images/ground.png"));
            g.dispose();

        } catch (IOException e) {
            System.err.println("Failed to generate assets: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

