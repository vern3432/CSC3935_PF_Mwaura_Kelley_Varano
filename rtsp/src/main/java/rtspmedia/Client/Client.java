package rtspmedia.Client;

import rtspmedia.util.Library;
import rtspmedia.util.LibraryView;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.IOException;
import javax.swing.SwingUtilities;

import merrimackutil.json.JsonIO;
import java.io.ObjectOutputStream;

/**
 * The Client class is responsible for establishing a connection to the server and initializing the library view.
 */
public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    /**
     * The main method that starts the client.
     * It establishes a socket connection to the server, initializes streams, and sets up the library view.
     * @param args Command line arguments, not used.
     */
    public static void main(String[] args) {
        try {
            Socket socket = new Socket(HOST, PORT);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.flush(); // Flush to ensure the header is sent
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            String library1 = (String) input.readObject();
            Library dummyLibrary = new Library();
            dummyLibrary.deserialize(JsonIO.readObject(library1));
            SwingUtilities.invokeLater(() -> {
                new LibraryView(dummyLibrary, socket, output, input);
            });
            // Do not close the socket here
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
