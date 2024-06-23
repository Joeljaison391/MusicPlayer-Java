package org.example.config;


import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private Properties properties;

    public ConfigManager() {
        properties = new Properties();
        loadConfig();
    }

    private void loadConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            // File not found, will create a new one upon saving
        }
    }

    public void saveConfig() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public String getLastSelectedFolder() {
        return properties.getProperty("lastSelectedFolder", "");
    }

    public void setLastSelectedFolder(String path) {
        properties.setProperty("lastSelectedFolder", path);
    }
}
