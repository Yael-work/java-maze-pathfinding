package Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configurations {
    private static Configurations instance = null;
    private final Properties properties;

    private Configurations() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("resources/config.properties")); //load config file
        } catch (IOException e) {
            e.printStackTrace();
        }
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