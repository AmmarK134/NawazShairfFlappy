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
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Center window on screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - Constants.WINDOW_WIDTH) / 2;
        int y = (screenSize.height - Constants.WINDOW_HEIGHT) / 2;
        setLocation(x, y);

        // Initialize game panel
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

