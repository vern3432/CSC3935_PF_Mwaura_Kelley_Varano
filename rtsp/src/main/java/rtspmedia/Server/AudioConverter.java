package rtspmedia.Server;

import javax.sound.sampled.*;

import java.io.File;
import java.io.IOException;

public class AudioConverter {

    public static void convertToWav(String inputFilePath, String outputFilePath)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File inputFile = new File(inputFilePath);

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile);

        AudioFormat inputFormat = audioInputStream.getFormat();

        AudioFormat outputFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            48000.0f,       // sample rate
            16,             // bit depth
            2,              // channels
            4,              // frame size
            48000.0f,       // frame rate
            false           // little-endian (false for big-endian)
        );

        AudioInputStream convertedInputStream = AudioSystem.getAudioInputStream(outputFormat, audioInputStream);

        File outputFile = new File(outputFilePath);

        AudioSystem.write(convertedInputStream, AudioFileFormat.Type.WAVE, outputFile);

        audioInputStream.close();
        convertedInputStream.close();
    }

    public static void main(String[] args) throws LineUnavailableException {
        String inputFilePath = "/home/linxuser3/Documents/CSC3935_PF_Mwaura_Kelley_Varano-1/SampleAudio/Behind Enemy Lines.mp3";  // Example input file path
        String outputFilePath = "/home/linxuser3/Documents/CSC3935_PF_Mwaura_Kelley_Varano-1/SampleAudio/Behind Enemy Lines.wav"; // Example output file path

        try {
            convertToWav(inputFilePath, outputFilePath);
            System.out.println("Conversion successful.");
        } catch (UnsupportedAudioFileException | IOException e) {
            System.out.println("Conversion failed: " + e.getMessage());
        }
    }
}
