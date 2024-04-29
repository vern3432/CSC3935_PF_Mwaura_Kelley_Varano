package rtspmedia.Server;

import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFrame;

public class MusicLibraryCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nAvailable Commands:");
            System.out.println("1. AddSong <song_name>");
            System.out.println("2. AddAlbum <album_name>");
            System.out.println("3. PrintLibrary");
            System.out.println("4. StartServer");
            System.out.println("5. Exit");
            System.out.print("Enter command: ");
            String command = scanner.nextLine();
            String[] tokens = command.split(" ", 2);

            switch (tokens[0]) {
                case "AddSong":
                    if (tokens.length > 1) {
                        addSong();
                    } else {
                        System.out.println("Error: Missing song name.");
                    }
                    break;
                case "AddAlbum":
                    if (tokens.length > 1) {
                        addAlbum();
                    } else {
                        System.out.println("Error: Missing album name.");
                    }
                    break;
                case "StartServer":
                    StartServer();
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
    public static void addSong() {
        JFrame frame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 Files", "mp3");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Select an MP3 file");

        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            System.out.println("Selected song: " + selectedFilePath);
            // Implement further actions with the selected file path here if needed.
        } else {
            System.out.println("No file selected or operation cancelled.");
        }
        frame.dispose(); // Clean up the frame after use.
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

    public static void StartServer() {

    }

    private static void printLibrary() {
        System.out.println("Library Contents:");
        // Implement the logic to print the contents of the library.
    }

}
