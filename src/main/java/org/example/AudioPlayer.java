package org.example;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioPlayer {
    private Clip clip;
    private boolean isPaused = false;
    private long pausePosition = 0;
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private File nextFile;

    public void setNextFile(File nextFile) {
        this.nextFile = nextFile;
    }

    public void playFile(File musicFile) {
        try {
            resetClip();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            clip.start();
            isPaused = false;
            isPlaying.set(true);
            System.out.println("Playing audio: " + musicFile.getName());

            new Thread(() -> printProgress(musicFile)).start();

            while (clip.isRunning() || isPaused) {
                Thread.sleep(1000);
            }

            isPlaying.set(false);
            clip.close();
            audioInputStream.close();

            System.out.println("Playback completed.");

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | InterruptedException e) {
            System.err.println("Error playing file: " + musicFile.getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printProgress(File musicFile) {
        try {
            while (isPlaying.get()) {
                long currentPosition = clip.getMicrosecondPosition();
                long totalLength = clip.getMicrosecondLength();
                int progress = (int) ((currentPosition * 100) / totalLength);
                System.out.println("Current song progress: " + progress + "%");

                if (nextFile != null) {
                    System.out.println("Next song: " + nextFile.getName());
                }

                Thread.sleep(1000); // Sleep for 1 second
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void pauseMusic() {
        if (clip != null && clip.isRunning()) {
            pausePosition = clip.getMicrosecondPosition();
            clip.stop();
            isPaused = true;
            isPlaying.set(false);
            System.out.println("\nMusic paused.");
        }
    }

    public void resumeMusic() {
        if (clip != null && isPaused) {
            clip.setMicrosecondPosition(pausePosition);
            clip.start();
            isPaused = false;
            isPlaying.set(true);
            new Thread(() -> printProgress(null)).start();
            System.out.println("Music resumed.");
        }
    }

    public void forwardMusic(int seconds) {
        if (clip != null) {
            long newPosition = clip.getMicrosecondPosition() + (seconds * 1_000_000);
            clip.setMicrosecondPosition(newPosition);
            System.out.println("Forwarded " + seconds + " seconds.");
        }
    }

    public void rewindMusic(int seconds) {
        if (clip != null) {
            long newPosition = clip.getMicrosecondPosition() - (seconds * 1_000_000);
            clip.setMicrosecondPosition(Math.max(newPosition, 0));
            System.out.println("Rewinded " + seconds + " seconds.");
        }
    }

    public void nextMusic() {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
            isPlaying.set(true);
        }
    }

    public void stopMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
            isPlaying.set(false);
            isPaused = false;
            pausePosition = 0;
            System.out.println("Music stopped.");
        }
    }

    public boolean isPlaying() {
        return isPlaying.get();
    }

    public int getProgress() {
        if (clip != null) {
            long currentPosition = clip.getMicrosecondPosition();
            long totalLength = clip.getMicrosecondLength();
            return (int) ((currentPosition * 100) / totalLength);
        }
        return 0;
    }

    private void resetClip() {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            clip.close();
        }
    }
}
