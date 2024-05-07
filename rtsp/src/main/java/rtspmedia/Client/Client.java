package rtspmedia.Client;

import rtspmedia.util.Library;
import rtspmedia.util.LibraryView;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;

import javax.swing.SwingUtilities;

import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONObject;

import java.io.ObjectOutputStream;

public class Client {
    private static String HOST = "localhost";
    private static int PORT = 12345;
    private static JSONObject configobj;
    private static ClientConfiguration config;
    private final static String configFile = "data/client-config/config.json";

    private static void initialize(){

        try {
            configobj = JsonIO.readObject(new File(configFile));
        } catch (FileNotFoundException x) {
            System.out.println("Couldn't find config file!");
        }
        try {
            config = new ClientConfiguration(configobj);
        } catch (InvalidObjectException x) {
            System.out.println("Invalid JSON Object");
        }

        HOST = config.getServerAddress();
        PORT = config.getServerPort();

    }

    /**
     * @param args
     *             /home/linxuser3/DocumSocket"Directory:"ents/CSC3935_PF_Mwaura_Kelley_Varano/SampleAudio/Behind
     *             Enemy Lines.mp3
     */
    public static void main(String[] args) {
        try {

            initialize();

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
            System.out.println("Client excenvalid type code: ACption: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
