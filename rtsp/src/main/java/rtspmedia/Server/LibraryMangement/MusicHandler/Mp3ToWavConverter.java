package rtspmedia.Server.LibraryMangement.MusicHandler ;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Mp3ToWavConverter {
    // Constructor
    public Mp3ToWavConverter() {
        // Constructor logic (can be expanded if needed)
    }
    
    // Method to convert MP3 to WAV
    public void convertMp3ToWav(String mp3FilePath, String wavFilePath) {
        // Set the PCM signed audio format
        AudioFormat format = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            48000.0f,       // sample rate
            16,             // bit depth
            2,              // channels
            4,              // frame size
            48000.0f,       // frame rate
            false           // little-endian (false for big-endian)
        );

        File mp3File = new File(mp3FilePath);
        File wavFile = new File(wavFilePath);

        try {
            // Get the audio input stream from the MP3 file
            AudioInputStream mp3AudioStream = AudioSystem.getAudioInputStream(mp3File);
            // Get the decoded audio input stream in the specified format
            AudioInputStream decodedStream = AudioSystem.getAudioInputStream(format, mp3AudioStream);

            // Write the decoded stream to a WAV file
            AudioSystem.write(decodedStream, AudioFileFormat.Type.WAVE, wavFile);

            // Close streams
            mp3AudioStream.close();
            decodedStream.close();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }
    
    // Main method (for testing)
    public static void main(String[] args) {
        Mp3ToWavConverter converter = new Mp3ToWavConverter();
        // Example file paths (adjust as necessary)
        String mp3FilePath = "/home/linxuser3/Documents/CSC3935_PF_Mwaura_Kelley_Varano/SampleAudio/You'll Find a Way.mp3";
        String wavFilePath = "/home/linxuser3/Documents/CSC3935_PF_Mwaura_Kelley_Varano/SampleAudio/conversion.wav";
        
        // Test the conversion method
        converter.convertMp3ToWav(mp3FilePath, wavFilePath);
        System.out.println("Conversion completed.");
    }
}
