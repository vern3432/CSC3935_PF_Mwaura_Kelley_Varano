package rtspmedia.MediaClient.libraryview;
import javax.swing.*;
import java.awt.*;

public class Player {

    // GUI components
    JFrame frame;
    JButton setupButton;
    JButton playButton;
    JButton pauseButton;
    JButton tearButton;
    JButton descButton;

    JPanel mainPanel;
    JPanel buttonPanel;
    JLabel iconLabel;

    public Player() {
        // Initialize frame
        frame = new JFrame("Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize buttons
        setupButton = new JButton("Setup Connection");
        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        tearButton = new JButton("Close Connection");
        descButton = new JButton("Describe Stream");

        // Initialize panels
        mainPanel = new JPanel();
        buttonPanel = new JPanel();
        iconLabel = new JLabel();

        // Layout setup
        buttonPanel.setLayout(new GridLayout(1, 0));
        buttonPanel.add(setupButton);
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(tearButton);
        buttonPanel.add(descButton);

        mainPanel.setLayout(null);
        mainPanel.add(iconLabel);
        mainPanel.add(buttonPanel);
        iconLabel.setBounds(0, 0, 380, 280);
        buttonPanel.setBounds(5, 280, 380, 50);

        // Add panels to frame
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frame.setSize(new Dimension(390, 370));
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Create a new instance of Client to initialize the GUI
        new Player();
    }
}
