package org.example;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicLibrary {
    private final List<File> musicFiles = new ArrayList<>();

    public void loadMusicFiles(String folderPath) {
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));
            if (files != null) {
                Collections.addAll(musicFiles, files);
                System.out.println("Loaded " + files.length + " music files.");
                for (File file : files) {
                    System.out.println("Loaded file: " + file.getName());
                }
            } else {
                System.out.println("No files found in the directory.");
            }
        } else {
            System.out.println("Invalid directory: " + folderPath);
        }
    }

    public List<File> getMusicFiles() {
        return new ArrayList<>(musicFiles);
    }

    public List<File> getPlaylist(boolean shuffle) {
        List<File> playlist = new ArrayList<>(musicFiles);
        if (shuffle) {
            Collections.shuffle(playlist);
        }
        return playlist;
    }

    public void shuffleMusicFiles() {
        Collections.shuffle(musicFiles);
    }
}
