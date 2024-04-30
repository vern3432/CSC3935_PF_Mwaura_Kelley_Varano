package rtspmedia.MediaClient.libraryview;

import rtspmedia.MediaClient.libraryview.libraryviewhelpers.LibraryView;
import rtspmedia.Server.LibraryMangement.*;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.SwingUtilities;

import merrimackutil.json.JsonIO;
import java.io.InvalidObjectException;
import java.io.FileReader;
import rtspmedia.rtp.*;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    /**
     * @param args
     *             /home/linxuser3/Documents/CSC3935_PF_Mwaura_Kelley_Varano/SampleAudio/Behind
     *             Enemy Lines.mp3
     * @throws LineUnavailableException
     */
    public static void main(String[] args) throws LineUnavailableException {
        try (Socket socket = new Socket(HOST, PORT);
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            // Example data to send
            output.writeObject("Hello, Server!");
            output.flush();

            // Example of receiving data from server
            String response = (String) input.readObject();
            socket.getLocalPort();
            // Receive RTP port from server and connect

            // Initialize LibraryView if connection is successful
            Library dummyLibrary = new Library(); // Assume library is received or created here
            dummyLibrary.deserialize(JsonIO.readObject(response));
            SwingUtilities.invokeLater(() -> {
                new LibraryView(dummyLibrary);
            });

            Object rtpPort = input.readObject();
            if (rtpPort instanceof Integer) {
                int portNumber = (Integer) rtpPort;
                System.out.println("RceivedPort:" + portNumber);
                // Start RTPClient with received port
                RTPClient rtpClient = new RTPClient("localhost", 500);
                rtpClient.startReceiving();
                System.out.println("The integer value is: " + portNumber);
            } else {
                System.out.println("Port:"+rtpPort.toString());
                System.out.println("The object is not an instance of Integer:n"+rtpPort.toString());
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
