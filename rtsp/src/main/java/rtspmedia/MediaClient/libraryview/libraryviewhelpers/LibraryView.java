package rtspmedia.MediaClient.libraryview.libraryviewhelpers;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Base64;
import java.io.ObjectInputStream;

import rtspmedia.Server.LibraryMangement.Library;
import rtspmedia.Server.LibraryMangement.Song;
import rtspmedia.rtp.RTPClient;
import rtspmedia.rtp.RTPServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Base64;

public class LibraryView extends JFrame {
    Socket socket;
    JFrame frame;
    JButton setupButton, playButton, pauseButton, tearButton, descButton;
    JPanel mainPanel, buttonPanel, imagePanel;
    JLabel iconLabel;
    ObjectOutputStream output;
    ObjectInputStream input;
    public LibraryView() {
        // Initialize frame
        frame = new JFrame("Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Set the size of the frame

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

    public LibraryView(Library library,Socket socket,ObjectOutputStream output,ObjectInputStream input ) {
        this.output=output;
        this.input=input;
        // Initialize frame
        this.socket=socket;
        frame = new JFrame("Client Library");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setSize(800, 600); // Set the size of the frame

        // Initialize panels
        mainPanel = new JPanel(new BorderLayout());
        buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10)); // A little space between buttons
        imagePanel = new JPanel();
        mainPanel.setBackground(Color.BLACK);
        buttonPanel.setBackground(Color.BLACK);
        imagePanel.setBackground(Color.BLACK);
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem toggleTheme = new JMenuItem("Toggle Theme");
        menu.add(toggleTheme);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
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

        // Load and display songs from the library
        library.getSongs().forEach(song -> {
            try {
                byte[] imageData = Base64.getDecoder().decode(song.getAlbumImage());
                ImageIcon icon = new ImageIcon(imageData);
                Image img = icon.getImage().getScaledInstance(2000, 2000, Image.SCALE_SMOOTH);
                ImageButton songButton = new ImageButton(new ImageIcon(img), song.getName(), song.getPath());
                songButton.setHorizontalTextPosition(JButton.CENTER);
                songButton.setVerticalTextPosition(JButton.BOTTOM);
                songButton.setSocket(this.socket);
                songButton.setOutput(this.output);
                songButton.setInput(this.input);
                imagePanel.add(songButton);


            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        JScrollPane scrollPane = new JScrollPane(imagePanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void createAndShowLibraryViewWithDummyData() {
        Library dummyLibrary = new Library();
        try {
            // Accessing the placeholder image via ClassLoader
            InputStream is = LibraryView.class.getResourceAsStream("/images/placeholder.jpg");
            if (is == null) {
                throw new RuntimeException("Cannot find resource file");
            }
            byte[] imageBytes = is.readAllBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Create dummy songs
            Song song1 = new Song("Song 1", base64Image, "path/to/song1.mp3");
            Song song2 = new Song("Song 2", base64Image, "path/to/song2.mp3");
            Song song3 = new Song("Song 3", base64Image, "path/to/song3.mp3");

            // Add songs to the library
            dummyLibrary.addSong(song1);
            dummyLibrary.addSong(song2);
            dummyLibrary.addSong(song3);

            SwingUtilities.invokeLater(() -> {
                new LibraryView(dummyLibrary,null,null,null);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        createAndShowLibraryViewWithDummyData();
    }
}