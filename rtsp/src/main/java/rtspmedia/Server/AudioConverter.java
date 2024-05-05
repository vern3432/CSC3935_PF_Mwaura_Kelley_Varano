package rtspmedia.Server;

import javax.sound.sampled.*;

import java.io.File;
import java.io.IOException;

public class AudioConverter {

    public static void convertToWav(String inputFilePath, String outputFilePath)
            throws UnsupportedAudioFileException, IOException {
        File inputFile = new File(inputFilePath);
        try (AudioInputStream mp3AudioStream = AudioSystem.getAudioInputStream(inputFile)) {
            AudioFormat sourceFormat = mp3AudioStream.getFormat();
            AudioFormat pcmFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(),
                16,
                sourceFormat.getChannels(),
                sourceFormat.getChannels() * 2,
                sourceFormat.getSampleRate(),
                false
            );
            try (AudioInputStream pcmAudioStream = AudioSystem.getAudioInputStream(pcmFormat, mp3AudioStream)) {
                File outputFile = new File(outputFilePath);
                AudioSystem.write(pcmAudioStream, AudioFileFormat.Type.WAVE, outputFile);
            }
        }
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
