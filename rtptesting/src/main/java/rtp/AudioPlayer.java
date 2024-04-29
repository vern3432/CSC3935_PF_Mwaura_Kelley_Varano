package rtp;


import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;

public class AudioPlayer {
    private SourceDataLine line;

    public void start() throws LineUnavailableException {
        // Ensure this matches your actual audio file's format.
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, 2, 4, 44100.0f, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();
    }
    

    public void playAudio(byte[] audioBytes) {
        if (line != null) {
            line.write(audioBytes, 0, audioBytes.length);
        }
    }

    public void stop() {
        if (line != null) {
            line.drain();
            line.close();
        }
    }
}
