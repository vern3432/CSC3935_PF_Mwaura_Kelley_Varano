package rtspmedia.Server;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import merrimackutil.json.JsonIO;
import rtspmedia.MediaClient.libraryview.libraryviewhelpers.LibraryView;
import rtspmedia.Server.LibraryMangement.Album;
import rtspmedia.Server.LibraryMangement.Library;
import rtspmedia.Server.LibraryMangement.Song;

public class MusicLibraryCLI {
    private static String libraryFilePath = "library.json"; // Default library file path
    private static Library library; // Library object

    
    /** 
     * @param args
     * @throws IOException
     * @throws UnsupportedAudioFileException 
     * @throws LineUnavailableException 
     */
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        if (args.length > 0) {
            libraryFilePath = args[0]; // Allow user to specify a different library file
        }
        ensureLibraryFileExists();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nAvailable Commands:");
            System.out.println("1. AddSong <song_name>");
            System.out.println("2. AddAlbum <album_name>");
            System.out.println("3. PrintLibrary");
            System.out.println("4. StartServer");
            System.out.println("5. ViewLibrary");

            System.out.println("6. Exit");
            System.out.print("Enter command: ");
            String command = scanner.nextLine();
            String[] tokens = command.split(" ", 2);

            switch (tokens[0]) {
                case "AddSong":
                    if (tokens.length > 1) {
                        String songName = tokens[1].trim(); // Correctly capture the full song name
                        addSong(songName);
                        saveLibraryToFile(library, libraryFilePath);
                    } else {
                        System.out.println("Error: Missing song name.");
                    }
                    break;
                case "AddAlbum":
                    if (tokens.length > 1) {
                        addAlbum();
                        saveLibraryToFile(library, libraryFilePath);

                    } else {
                        System.out.println("Error: Missing album name.");
                    }
                    break;
                case "StartServer":
                    ensureLibraryFileExists();

                    StartServer();
                    break;
                case "ViewLibrary":
                ensureLibraryFileExists();

                    ViewLibrary();
                    break;
                case "PrintLibrary":
                    printLibrary();
                    break;
                case "Exit":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }
        scanner.close();
        System.out.println("Exiting Music Library CLI.");
    }
    public static void addSong(String name) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        JFrame frame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter songFilter = new FileNameExtensionFilter("Audio Files", "mp3", "wav");
        fileChooser.setFileFilter(songFilter);
        fileChooser.setDialogTitle("Select an Audio file");
        

        int songResult = fileChooser.showOpenDialog(frame);
        if (songResult == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
            String wavFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".wav";

            if (extension.equals("mp3")) {
                // Convert MP3 to WAV
                AudioConverter.convertToWav(filePath, wavFilePath);
                filePath = wavFilePath; // Update file path to the converted WAV file
            }

            // Calculate the length of the song in milliseconds
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            long frames = audioStream.getFrameLength();
            double durationInSeconds = (frames+0.0) / format.getFrameRate();  
            int durationInMilliSeconds = (int)(durationInSeconds * 1000);
            fileChooser.setDialogTitle("Select an Image for the Album");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png"));

            int imageResult = fileChooser.showOpenDialog(frame);
            if (imageResult == JFileChooser.APPROVE_OPTION) {
                String imagePath = fileChooser.getSelectedFile().getAbsolutePath();
                // Add song to library and save to file
                // Resize the image to compress it
                BufferedImage originalImage = ImageIO.read(new File(imagePath));
                int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
                BufferedImage resizedImage = resizeImage(originalImage, type, 100, 100); // Resize to 100x100 pixels
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", baos);
                byte[] imageBytes = baos.toByteArray();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                // File imgFile = new File(imagePath);
                library.addSong(new Song(name, base64Image, filePath, Integer.toString(durationInMilliSeconds)));
                saveLibraryToFile(library, libraryFilePath);
            } else {
                System.out.println("No image selected or operation cancelled.");
            }
        } else {
            System.out.println("No song file selected or operation cancelled.");
        }
        frame.dispose();
    }
    private static BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        return resizedImage;
    }

    private static void addAlbum() {
        JFrame frame = new JFrame();
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setDialogTitle("Select an Album Directory");
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Set to choose only directories

        int result = directoryChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedDirectoryPath = directoryChooser.getSelectedFile().getAbsolutePath();
            System.out.println("Selected album directory: " + selectedDirectoryPath);
            // Implement further actions with the selected directory path here if needed.
        } else {
            System.out.println("No directory selected or operation cancelled.");
        }
        frame.dispose(); // Clean up the frame after use.
    }

    public static void ViewLibrary() {
        SwingUtilities.invokeLater(() -> {
            new LibraryView(library,null,null,null);
        });
    }

    public static void StartServer() {
        Server server=new Server(MusicLibraryCLI.libraryFilePath);

        
    }

    private static void printLibrary() {
        System.out.println("Library Contents:");
        System.out.println("Songs:");
        for (Song song : library.getSongs()) {
            System.out.println("Name: " + song.getName() + ", Album Image: " + song.getAlbumImage() + ", Path: " + song.getPath() + ", Length: " + "song.ser() "+ " ms");
        }
        System.out.println("Albums:");
        for (Album album : library.getAlbums()) {
            System.out.println("Album:");
            for (Song song : album.getSongs()) {
                System.out.println("  Song: " + song.getName() + ", Album Image: " + song.getAlbumImage() + ", Path: " + song.getPath() + ", Length: " + "getInt" + " ms");
            }
        }
    }

    private static Library loadLibraryFromFile(String filePath) {
        return null;
        // Implement loading logic
    }

    private static void saveLibraryToFile(Library library, String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath, false); // false to overwrite the file
            String libraryJson = library.serialize(); // Serialize the library to JSON
            writer.write(libraryJson);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving the library file: " + e.getMessage());
        }
    }

    private static void ensureLibraryFileExists() {
        File libraryFile = new File(libraryFilePath);
        if (!libraryFile.exists()) {
            try {
                libraryFile.createNewFile();
                FileWriter writer = new FileWriter(libraryFile);
                writer.write("{\"songs\":[],\"albums\":[]}");
                writer.close();
                library = new Library(); // Initialize a new empty Library
            } catch (IOException e) {
                System.out.println("Error creating or initializing the library file: " + e.getMessage());
            }
        } else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(libraryFile));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                reader.close();
                String jsonString = jsonBuilder.toString();
                library = new Library();
                library.deserialize(JsonIO.readObject(jsonString));
            } catch (Exception e) {
                System.out.println("Error loading the library file: " + e.getMessage());
                library = new Library(); // Initialize a new empty Library if there's an error loading
            }
        }
    }
}