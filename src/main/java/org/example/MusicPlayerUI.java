package org.example;

import org.example.config.ConfigManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MusicPlayerUI extends JFrame {
    private static final MusicLibrary musicLibrary = new MusicLibrary();
    private static final AudioPlayer audioPlayer = new AudioPlayer();
    private static final ConfigManager configManager = new ConfigManager();

    private JButton playButton, pauseButton, resumeButton, nextButton, forwardButton, rewindButton, shuffleButton, exitButton;
    private JProgressBar progressBar;
    private JLabel currentSongLabel, nextSongLabel;
    private JList<String> songList;
    private DefaultListModel<String> songListModel;
    private JFileChooser fileChooser;

    public MusicPlayerUI() {
        setTitle("Music Player");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Initialize UI components
        initUIComponents();

        // Check for last selected folder
        String lastFolder = configManager.getLastSelectedFolder();
        if (lastFolder.isEmpty() || !new File(lastFolder).exists()) {
            selectMusicFolder();
        } else {
            loadMusicFiles(lastFolder);
        }
    }

    private void initUIComponents() {
        // Button panel
        JPanel buttonPanel = new JPanel();
        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        resumeButton = new JButton("Resume");
        nextButton = new JButton("Next");
        forwardButton = new JButton("Forward");
        rewindButton = new JButton("Rewind");
        shuffleButton = new JButton("Shuffle");
        exitButton = new JButton("Exit");

        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(forwardButton);
        buttonPanel.add(rewindButton);
        buttonPanel.add(shuffleButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Info panel
        JPanel infoPanel = new JPanel();
        progressBar = new JProgressBar(0, 100);
        currentSongLabel = new JLabel("Current song: ");
        nextSongLabel = new JLabel("Next song: ");

        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(progressBar);
        infoPanel.add(currentSongLabel);
        infoPanel.add(nextSongLabel);

        add(infoPanel, BorderLayout.NORTH);

        // Song list panel
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());
        songListModel = new DefaultListModel<>();
        songList = new JList<>(songListModel);
        JScrollPane scrollPane = new JScrollPane(songList);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        add(listPanel, BorderLayout.CENTER);

        // Action listeners
        playButton.addActionListener(e -> playMusic());
        pauseButton.addActionListener(e -> audioPlayer.pauseMusic());
        resumeButton.addActionListener(e -> audioPlayer.resumeMusic());
        nextButton.addActionListener(e -> nextMusic());
        forwardButton.addActionListener(e -> audioPlayer.forwardMusic(15));
        rewindButton.addActionListener(e -> audioPlayer.rewindMusic(15));
        shuffleButton.addActionListener(e -> shuffleMusic());
        exitButton.addActionListener(e -> exitApplication());
    }

    private void selectMusicFolder() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File folder = fileChooser.getSelectedFile();
            configManager.setLastSelectedFolder(folder.getAbsolutePath());
            configManager.saveConfig();
            loadMusicFiles(folder.getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(this, "Please select a music folder to proceed.");
            System.exit(0);
        }
    }

    private void loadMusicFiles(String folderPath) {
        musicLibrary.loadMusicFiles(folderPath);
        List<File> files = musicLibrary.getMusicFiles();
        songListModel.clear();
        for (File file : files) {
            songListModel.addElement(file.getName());
        }
        if (files.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No music files found in the selected directory.");
            selectMusicFolder();
        }
    }

    private void playMusic() {
        List<File> playlist = musicLibrary.getPlaylist(false);
        if (playlist.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No music files loaded.");
            return;
        }

        new Thread(() -> {
            for (int i = 0; i < playlist.size(); i++) {
                File musicFile = playlist.get(i);
                File nextFile = (i + 1 < playlist.size()) ? playlist.get(i + 1) : (playlist.size() == 1 ? playlist.get(0) : null);
                audioPlayer.setNextFile(nextFile);
                audioPlayer.playFile(musicFile);
                updateUI(musicFile, nextFile);
            }
        }).start();
    }

    private void nextMusic() {
        if (audioPlayer.isPlaying()) {
            audioPlayer.nextMusic();
        } else {
            List<File> playlist = musicLibrary.getPlaylist(false);
            if (!playlist.isEmpty()) {
                File musicFile = playlist.get(0);
                File nextFile = (playlist.size() == 1) ? playlist.get(0) : playlist.get(1);
                audioPlayer.setNextFile(nextFile);
                audioPlayer.playFile(musicFile);
                updateUI(musicFile, nextFile);
            }
        }
    }

    private void shuffleMusic() {
        List<File> playlist = musicLibrary.getPlaylist(true);
        if (playlist.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No music files loaded.");
            return;
        }

        new Thread(() -> {
            for (int i = 0; i < playlist.size(); i++) {
                File musicFile = playlist.get(i);
                File nextFile = (i + 1 < playlist.size()) ? playlist.get(i + 1) : (playlist.size() == 1 ? playlist.get(0) : null);
                audioPlayer.setNextFile(nextFile);
                audioPlayer.playFile(musicFile);
                updateUI(musicFile, nextFile);
            }
        }).start();
    }

    private void updateUI(File currentFile, File nextFile) {
        SwingUtilities.invokeLater(() -> {
            currentSongLabel.setText("Current song: " + currentFile.getName());
            nextSongLabel.setText("Next song: " + (nextFile != null ? nextFile.getName() : "None"));
        });

        new Thread(() -> {
            while (audioPlayer.isPlaying()) {
                int progress = audioPlayer.getProgress();
                progressBar.setValue(progress);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void exitApplication() {
        audioPlayer.stopMusic();
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MusicPlayerUI musicPlayerUI = new MusicPlayerUI();
            musicPlayerUI.setVisible(true);
        });
    }
}
