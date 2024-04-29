package rtp;


import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioFileProperties {
    private AudioFormat format;
    private int frameSize;
    private float frameRate;

    public AudioFileProperties(String filePath) throws UnsupportedAudioFileException, IOException {
        File file = new File(filePath);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        this.format = audioInputStream.getFormat();
        this.frameSize = format.getFrameSize();
        this.frameRate = format.getFrameRate();
        audioInputStream.close();
    }

    public AudioFormat getFormat() {
        return format;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public float getFrameRate() {
        return frameRate;
    }
}
