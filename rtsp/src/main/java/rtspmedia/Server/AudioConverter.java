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

        AudioFormat outputFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);

        AudioInputStream convertedInputStream = AudioSystem.getAudioInputStream(outputFormat, audioInputStream);

        File outputFile = new File(outputFilePath);

        AudioSystem.write(convertedInputStream, AudioFileFormat.Type.WAVE, outputFile);

        audioInputStream.close();
        convertedInputStream.close();
    }
}
