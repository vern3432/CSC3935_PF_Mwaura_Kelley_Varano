package rtspmedia.MediaClient;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
public class Client {
    JFrame frame;
    JButton setupButton, playButton, pauseButton, tearButton, descButton;
    JPanel mainPanel, buttonPanel, imagePanel;
    JLabel iconLabel;

    public Client() {
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

        // Load images dynamically from "SampleImages/Jpegs"
        File dir = new File("SampleImages/Jpegs");
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpeg");
            }
        });

        if (files != null && files.length > 0) {
            int rows = (int) Math.ceil(files.length / 5.0); // Calculate the number of rows needed
            imagePanel = new JPanel(new GridLayout(rows, 5, 5, 5)); // Set GridLayout with rows and 5 columns
            for (File file : files) {
                String imagePath = file.getPath();
                String description = file.getName();
                ImageButton button = new ImageButton(imagePath, description);
                imagePanel.add(button);
            }
            JScrollPane scrollPane = new JScrollPane(imagePanel); // Wrap imagePanel in a JScrollPane
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            mainPanel.add(scrollPane);
        }

        // Set the entire GUI background to black
        frame.getContentPane().setBackground(Color.BLACK);
        mainPanel.setBackground(Color.BLACK);
        buttonPanel.setBackground(Color.BLACK);
        imagePanel.setBackground(Color.BLACK);

        // Create menu bar and add to frame
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem toggleTheme = new JMenuItem("Toggle Theme");
        menu.add(toggleTheme);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // Add action listener to toggle theme
        toggleTheme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainPanel.getBackground().equals(Color.BLACK)) {
                    mainPanel.setBackground(Color.WHITE);
                    buttonPanel.setBackground(Color.WHITE);
                    imagePanel.setBackground(Color.WHITE);
                    frame.getContentPane().setBackground(Color.WHITE);
                } else {
                    mainPanel.setBackground(Color.BLACK);
                    buttonPanel.setBackground(Color.BLACK);
                    imagePanel.setBackground(Color.BLACK);
                    frame.getContentPane().setBackground(Color.BLACK);
                }
            }
        });

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Client();
            }
        });
    }
}