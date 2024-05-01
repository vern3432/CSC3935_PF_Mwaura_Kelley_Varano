package rtspmedia.Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.IOException;
import rtspmedia.Server.LibraryMangement.*;
import rtspmedia.rtp.RTPServer;

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
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.flush(); // Flush to ensure the header is sent
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                new Thread(new ClientHandler(socket, input, output)).start();
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
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.flush(); // Flush to ensure the header is sent
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                new Thread(new ClientHandler(socket, input, output)).start();
                connectionCount++;
            }
        } catch (IOException e) {                       
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectInputStream input;
        private ObjectOutputStream output;

        public ClientHandler(Socket socket, ObjectInputStream input, ObjectOutputStream output) {
            this.socket = socket;
            this.input = input;
            this.output = output;
        }

        public void run() {
            try {
                ensureLibraryFileExists();
                output.writeObject(Server.library.serialize());
                output.flush();
                output.reset(); // Reset the stream after sending
        
                // Wait for a message from the client
                Object message = input.readObject();
                if (message instanceof String && ((String) message).contains("Directory:")) {
                    System.out.println("Client says: " + message);
                    String request = (String)message;
                     request = request.replace("Directory:", "");
                    RTPServer rtpserver = new RTPServer(request);
                    int port = rtpserver.getRTP_PORT();
                    // Send a response back to the client
                    output.writeObject(Integer.toString(port));
                    output.flush();
                    output.reset(); // Reset the stream after sending
                    rtpserver.start();
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    System.out.println("Error closing client socket: " + e.getMessage());
                }
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
                Server.library =  new Library(); // Initialize a new empty Library if there's an error loading
            }
        }
    }
}
