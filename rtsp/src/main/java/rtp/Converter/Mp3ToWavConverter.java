package rtp.Converter;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Mp3ToWavConverter {

    public static void convertMp3ToWav(String mp3Filename, String wavFilename) throws UnsupportedAudioFileException, IOException {
        // Load the MP3 audio file
        File mp3File = new File(mp3Filename);
        AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(mp3File);

        // Specify the output WAV file format
        AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                48000.0f,
                16,
                2,
                4,
                48000.0f,
                false
        );

        // Convert the MP3 stream to the specified WAV format
        AudioInputStream wavStream = AudioSystem.getAudioInputStream(targetFormat, mp3Stream);

        // Write the WAV file
        File wavFile = new File(wavFilename);
        AudioSystem.write(wavStream, AudioFileFormat.Type.WAVE, wavFile);

        // Close streams
        wavStream.close();
        mp3Stream.close();
        System.out.println("Conversion complete.");
    }

    public static void main(String[] args) {
        try {
            String mp3Filename = "/home/linxuser3/Documents/CSC3935_PF_Mwaura_Kelley_Varano/SampleAudio/Behind Enemy Lines.mp3";
            String wavFilename = "output.wav";
            convertMp3ToWav(mp3Filename, wavFilename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
