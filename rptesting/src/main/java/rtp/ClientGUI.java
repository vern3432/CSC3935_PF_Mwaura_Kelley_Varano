package rtp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;

public class ClientGUI extends JFrame implements ActionListener {
    private JButton playButton;
    private JButton pauseButton;
    private RTPClient client;
    private AudioPlayer player;
    private boolean isPlaying = false;

    public ClientGUI() throws SocketException {
        super("RTP Audio Player");
        this.client = new RTPClient(5004);
        this.player = new AudioPlayer();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 100);
        setLayout(new FlowLayout());

        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        playButton.addActionListener(this);
        pauseButton.addActionListener(this);

        add(playButton);
        add(pauseButton);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == playButton) {
            playAudio();
        } else if (e.getSource() == pauseButton) {
            pauseAudio();
        }
    }

    private void playAudio() {
        if (!isPlaying) {
            isPlaying = true;
            new Thread(() -> {
                try {
                    player.start();
                    while (isPlaying) {
                        byte[] packet = client.receivePacket();
                        player.playAudio(packet);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void pauseAudio() {
        isPlaying = false;
        player.stop();
    }

    public static void main(String[] args) throws SocketException {
        new ClientGUI();
    }
}
