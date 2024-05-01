package rtspmedia.MediaClient.libraryview.libraryviewhelpers;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

import rtspmedia.rtp.RTPClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ImageButton extends JButton implements ActionListener {
    private String imagePath;
    private String buttonText;
    private String fileLocation;
    public ObjectInputStream input;
    public ObjectOutputStream output;
    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ImageButton(String imagePath, String buttonText) {
        this.imagePath = imagePath;
        this.buttonText = buttonText;
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Resize image to 100x100 pixels
        this.setIcon(new ImageIcon(resizedImg));

        this.setText(buttonText);
        this.setHorizontalTextPosition(JButton.CENTER);
        this.setVerticalTextPosition(JButton.BOTTOM);
        this.addActionListener(this);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setFocusPainted(false);
    }

    public ImageButton(ImageIcon image, String buttonText, String fileLocation) {
        this.buttonText = buttonText;
        this.fileLocation = fileLocation;
        System.out.println(fileLocation);
        ImageIcon icon = image;
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Resize image to 100x100 pixels
        this.setIcon(new ImageIcon(resizedImg));

        this.setText(buttonText);
        this.setHorizontalTextPosition(JButton.CENTER);
        this.setVerticalTextPosition(JButton.BOTTOM);
        this.addActionListener(this);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setFocusPainted(false);
    }

    /**
     * @return String
     */
    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        GradientPaint gp = new GradientPaint(0, 0, Color.BLUE, getWidth(), getHeight(), Color.CYAN);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (socket != null && !socket.isClosed()) {
                output.writeObject("Directory:"+this.fileLocation);
                output.flush();
                output.reset(); // Reset the stream to avoid caching objects
    
                // Now, receive a response from the server
                String message = (String) input.readObject();
                System.out.println("Received from server: " + message);
                int convertedValue = Integer.parseInt(message);
                try {
                    RTPClient client = new RTPClient(convertedValue);
                    client.startReceiving();
                } catch (SocketException | UnknownHostException | LineUnavailableException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    RTPClient client = new RTPClient(convertedValue);
                    client.startReceiving();
                } catch (SocketException | UnknownHostException | LineUnavailableException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
        
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("Error communicating with the server: " + ex.getMessage());
        }
    }
    
    
}