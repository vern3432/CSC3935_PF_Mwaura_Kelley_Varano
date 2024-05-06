package rtspmedia.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageConverter {

    public static String encodeImageToBase64(Image image) throws IOException {
        BufferedImage bufferedImage = toBufferedImage(image);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream); // You can change "png" to another format if needed
        byte[] imageBytes = outputStream.toByteArray();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        outputStream.close();
        return base64Image;
    }

    private static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static void main(String[] args) {
        // Example usage
        Image image = Toolkit.getDefaultToolkit().getImage("path/to/your/image.jpg"); // Load an image file
        try {
            String base64Image = encodeImageToBase64(image);
            System.out.println("Encoded Base64 Image:");
            System.out.println(base64Image);
        } catch (IOException e) {
            System.err.println("Error encoding image to Base64.");
            e.printStackTrace();
        }
    }
}
