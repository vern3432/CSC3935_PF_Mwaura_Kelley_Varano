package rtp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioFileLoader {
    private String filePath;

    public AudioFileLoader(String filePath) {
        this.filePath = filePath;
    }

    public byte[] loadAudioFile() throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return data;
    }
}
