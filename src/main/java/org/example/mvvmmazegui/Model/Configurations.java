package org.example.mvvmmazegui.Model;

// this singleton class handles loading and saving application configuration properties
// it supports fallback loading from multiple sources and defaults if the config is missing

import java.io.*;
import java.net.URL;
import java.util.Properties;

public class Configurations {
    private static Configurations instance;
    private final Properties properties = new Properties();
    private final String configFilePath = "src/main/resources/config.properties";

    // constructor is private to enforce singleton pattern and loads properties on creation
    private Configurations() {
        loadProperties();
    }

    // returns the singleton instance of the Configurations class
    public static Configurations getInstance() {
        if (instance == null) {
            instance = new Configurations();
        }
        return instance;
    }

    // attempts to load config properties from multiple sources; sets defaults if all fail
    private void loadProperties() {
        // Try multiple approaches to load the properties file
        boolean loaded = false;

        // Approach 1: Try with class resource
        try {
            InputStream input = getClass().getResourceAsStream("/config.properties");
            if (input != null) {
                properties.load(input);
                input.close();
                loaded = true;
            }
        } catch (IOException ignored) {}

        // Approach 2: Try with class loader
        if (!loaded) {
            try {
                InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
                if (input != null) {
                    properties.load(input);
                    input.close();
                    loaded = true;
                }
            } catch (IOException ignored) {}
        }

        // Approach 3: Try with file path
        if (!loaded) {
            try (InputStream input = new FileInputStream(configFilePath)) {
                properties.load(input);
                loaded = true;
            } catch (IOException ignored) {}
        }

        // If all approaches failed, set default properties
        if (!loaded) {
            setDefaultProperties();
            saveProperties();
        }
    }

    // saves the current properties to the file system
    private void saveProperties() {
        try {
            // Try to save to the resource path first
            URL resourceUrl = getClass().getResource("/config.properties");
            if (resourceUrl != null) {
                try (OutputStream output = new FileOutputStream(resourceUrl.getPath())) {
                    properties.store(output, null);
                    return;
                } catch (IOException ignored) {}
            }

            // Fall back to the file path
            try (OutputStream output = new FileOutputStream(configFilePath)) {
                properties.store(output, null);
            }
        } catch (IOException e) {
            System.err.println("Warning: Failed to save config.properties: " + e.getMessage());
        }
    }

    // sets default values for maze generation, solving algorithms, and thread pool size
    private void setDefaultProperties() {
        properties.setProperty("mazeGeneratingAlgorithm", "MyMazeGenerator");
        properties.setProperty("mazeSearchingAlgorithm", "BreadthFirstSearch");
        properties.setProperty("threadPoolSize", "5");
    }

    // returns the configured maze generator class name
    public String getMazeGeneratingAlgorithm() {
        return properties.getProperty("mazeGeneratingAlgorithm");
    }

    // sets and persists the maze generator class name
    public void setMazeGeneratingAlgorithm(String algorithm) {
        properties.setProperty("mazeGeneratingAlgorithm", algorithm);
        saveProperties();
    }

    // returns the configured maze search algorithm name
    public String getMazeSearchingAlgorithm() {
        return properties.getProperty("mazeSearchingAlgorithm");
    }

    // sets and persists the maze search algorithm name
    public void setMazeSearchingAlgorithm(String algorithm) {
        properties.setProperty("mazeSearchingAlgorithm", algorithm);
        saveProperties();
    }

    // reloads the configuration from file, clearing existing values
    public void reloadProperties() {
        properties.clear();
        loadProperties();
    }

    // retrieves the configured thread pool size; defaults to 5 if not present
    public int getThreadPoolSize() {
        return Integer.parseInt(properties.getProperty("threadPoolSize", "5"));
    }

    // sets and persists the thread pool size
    public void setThreadPoolSize(int size) {
        properties.setProperty("threadPoolSize", String.valueOf(size));
        saveProperties();
    }
    // resets the singleton instance (used for reloading or testing)
    public static void reset() {
        instance = null;
    }
}
