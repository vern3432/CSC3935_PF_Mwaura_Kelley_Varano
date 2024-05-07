package rtspmedia.util;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

import rtspmedia.rtp.RTPClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Base64;

public class ImageButton extends JButton implements ActionListener {
    private String imagePath;
    private String buttonText;
    private String fileLocation;
    public ObjectInputStream input;
    public ObjectOutputStream output;
    public long length;
    private BufferedImage bufferedImage;
    private String songTitle;
    private Image unsized;
    Image resizedImg;

    
    /** 
     * @return BufferedImage
     */
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

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
        this.unsized = img;
        this.resizedImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Resize image to 100x100 pixels
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
        this.unsized = img;
        this.songTitle = buttonText;
        this.resizedImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Resize image to 100x100 pixels
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

    public static String encodeToString(Image image) {
        System.out.println(image.toString());
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            // Create a buffered image with transparency
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);

            // Draw the image on to the buffered image
            bufferedImage.getGraphics().drawImage(image, 0, 0, null);

            // Write the image to a byte array output stream
            ImageIO.write(bufferedImage, "jpg", bos); // Default type set to "jpg"
            byte[] imageBytes = bos.toByteArray();

            // Base64 encode the byte array
            imageString = Base64.getEncoder().encodeToString(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    public Image resizeImage(Image originalImage) {
        return originalImage.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (socket != null && !socket.isClosed()) {
                output.writeObject("Directory:" + this.fileLocation);
                output.flush();
                output.reset(); // Reset the stream to avoid caching objects

                // Now, receive a response from the server
                String message = (String) input.readObject();
                System.out.println("Received from server: " + message);
                int convertedValue = Integer.parseInt(message);
                try {
                    RTPClient client = new RTPClient(convertedValue, length,
                            new ImageConverter().encodeImageToBase64(resizedImg), this.songTitle);
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