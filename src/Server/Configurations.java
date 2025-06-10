package Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configurations {
    private static Configurations instance = null;
    private final Properties properties;

    private Configurations() {
            properties = new Properties();

            // 1. try relative file path
            try (InputStream fallbackInput = new FileInputStream("resources/config.properties")) {
                properties.load(fallbackInput);
                return;
            } catch (IOException ignored) {}


            // 2. fallback:Try classpath
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                if (input != null) {
                    properties.load(input);
                    return;
                }
            } catch (IOException ignored) {}


            // 3. fallback: Absolute path from system property or environment variable
            String externalPath = System.getProperty("config.path");
            if (externalPath == null || externalPath.isEmpty()) {
                externalPath = System.getenv("CONFIG_PATH");
            }

            if (externalPath != null && !externalPath.isEmpty()) {
                try (InputStream externalInput = new FileInputStream(externalPath)) {
                    properties.load(externalInput);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.err.println("ERROR: config.properties not found in classpath, 'resources/', or external path.");
        }

    public static Configurations getInstance() {
        if (instance == null) {
            instance = new Configurations(); // singleton instance
        }
        return instance;
    }

    public int getThreadPoolSize() {
        return Integer.parseInt(properties.getProperty("threadPoolSize", "4")); // default pool size is 4
    }

    public String getMazeGeneratingAlgorithm() {
        return properties.getProperty("mazeGeneratingAlgorithm", "MyMazeGenerator");
    }

    public String getMazeSearchingAlgorithm() {
        return properties.getProperty("mazeSearchingAlgorithm", "BestFirstSearch");
    }
}