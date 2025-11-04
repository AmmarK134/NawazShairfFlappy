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
        setResizable(true); // Allow resizing for maximize
        
        // Maximize window (shows taskbar)
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Get initial size estimate
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Constants.WINDOW_WIDTH = screenSize.width;
        Constants.WINDOW_HEIGHT = screenSize.height;

        // Initialize game panel
        gamePanel = new GamePanel();
        add(gamePanel);

        // Window listener for both opening and closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Update Constants with actual window dimensions (accounts for taskbar)
                Dimension windowSize = getContentPane().getSize();
                Constants.WINDOW_WIDTH = windowSize.width;
                Constants.WINDOW_HEIGHT = windowSize.height;
                // Force repaint with new dimensions
                if (gamePanel != null) {
                    gamePanel.revalidate();
                    gamePanel.repaint();
                }
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                gamePanel.cleanup();
            }
        });

        setVisible(true);
        gamePanel.requestFocusInWindow();
    }
}

