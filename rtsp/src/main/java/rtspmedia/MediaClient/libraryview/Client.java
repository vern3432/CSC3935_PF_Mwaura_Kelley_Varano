package rtspmedia.MediaClient.libraryview;
import rtspmedia.Server.LibraryMangement.*;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.SwingUtilities;

import merrimackutil.json.JsonIO;
import java.io.InvalidObjectException;
import java.io.FileReader;


public class Client  {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;


    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
             
            String library1 = (String) input.readObject();
            Library dummyLibrary= new Library();
            dummyLibrary.deserialize(JsonIO.readObject(library1));
            SwingUtilities.invokeLater(() -> {
                new LibraryView(dummyLibrary);
            });

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

