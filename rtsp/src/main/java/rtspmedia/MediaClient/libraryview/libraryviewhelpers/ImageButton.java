package rtspmedia.MediaClient.libraryview.libraryviewhelpers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImageButton extends JButton implements ActionListener {
    private String imagePath;
    private String buttonText;
    private String fileLocation;

    public ImageButton(String imagePath, String buttonText) {
        this.imagePath = imagePath;
        this.buttonText = buttonText;
        this.fileLocation=fileLocation;
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
    public ImageButton(ImageIcon image, String buttonText,String fileLocation) {
        this.buttonText = buttonText;
        this.fileLocation=fileLocation;
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
        // Define the function to be triggered on click
        System.out.println(buttonText + " clicked!");
    }
}