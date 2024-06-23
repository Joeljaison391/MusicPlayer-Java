package org.example;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MP3Player {

    public static void main(String[] args) {
        String filePath = "D:\\music\\One-Kiss(PaglaSongs).wav"; // Replace with your MP3 file path

        try {
            File mp3File = new File(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(mp3File);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            System.out.println("Playing audio...");
            clip.start();

             // Uncomment the following line if you want the program to pause while playing.
             Thread.sleep(clip.getMicrosecondLength() / 1000);

            while (clip.isRunning()) {
                Thread.sleep(1000); // Optional delay to keep the program running while clip is playing
            }

            clip.close();
            audioInputStream.close();

            System.out.println("Playback completed.");

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
