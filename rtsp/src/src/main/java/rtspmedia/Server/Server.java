package rtspmedia.Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.IOException;
import rtspmedia.Server.LibraryMangement.*;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.IOException;
import javax.swing.SwingUtilities;
import rtspmedia.MediaClient.libraryview.*;
import rtspmedia.Server.LibraryMangement.*;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.SwingUtilities;
import rtspmedia.MediaClient.libraryview.*;
import merrimackutil.json.JsonIO;
import java.io.InvalidObjectException;
import java.io.FileReader;


public class Server {
    private static final int PORT = 12345;
    private static final int MAX_CONNECTIONS = 20; // Maximum number of concurrent connections
    
    private static String libraryFilePath = "library.json"; // Default library file path
    private static  Library library;
    Server(String libraryFilePath){
        this.libraryFilePath=libraryFilePath;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            int connectionCount = 0;

            while (connectionCount < MAX_CONNECTIONS) { // Limit the number of concurrent connections
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
                connectionCount++;
            }
        } catch (IOException e) {                       
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /** 
     * @param args
     */
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            int connectionCount = 0;

            while (connectionCount < MAX_CONNECTIONS) { // Limit the number of concurrent connections
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
                connectionCount++;
            }
        } catch (IOException e) {                       
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
                ensureLibraryFileExists();
                output.writeObject(Server.library.serialize());
                output.flush();
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            }
        }
    }

    private static void ensureLibraryFileExists() {
        File libraryFile = new File(libraryFilePath);
        if (!libraryFile.exists()) {
            try {
                libraryFile.createNewFile();
                FileWriter writer = new FileWriter(libraryFile);
                writer.write("{\"songs\":[libraryFile],\"albums\":[]}");
                writer.close();
                Server.library = new Library(); // Initialize a new empty Library
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
                Server.library = new Library();
                System.out.println(jsonString);
                Server.library.deserialize(JsonIO.readObject(jsonString));
            } catch (Exception e) {
                System.out.println("Error loading the library file: " + e.getMessage());
                Server.library = new Library(); // Initialize a new empty Library if there's an error loading
            }
        }
    }
}
