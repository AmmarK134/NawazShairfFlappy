package com.example.flappy;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application window frame.
 * Sets up the fixed-size, non-resizable window and manages the game panel.
 */
public class GameFrame extends JFrame {
    private GamePanel gamePanel;

    public GameFrame() {
        setTitle("Flappy Bird (Pure Java)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Get screen size and set window to fit screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Update Constants with actual screen dimensions
        Constants.WINDOW_WIDTH = screenSize.width;
        Constants.WINDOW_HEIGHT = screenSize.height;
        
        setSize(screenSize.width, screenSize.height);
        
        // Position at top-left corner (0, 0)
        setLocation(0, 0);

        // Initialize game panel (after Constants are set)
        gamePanel = new GamePanel();
        add(gamePanel);

        // Clean up timer on window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gamePanel.cleanup();
            }
        });

        setVisible(true);
        gamePanel.requestFocusInWindow();
    }
}

