package org.example.mvvmmazegui.Model;

import org.example.Server.*;

public class ServerManager {
    private static Server generateServer;
    private static Server solveServer;
    private static boolean serversRunning = false;

    public static void startServers() {
        stopServers();
        try {
            Thread.sleep(1000); // to allow servers to close
        }
        catch (InterruptedException ignore){}
        Configurations.reset();  // reloading the config file or updates
        if (!serversRunning) {
            generateServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
            solveServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());

            generateServer.start();
            solveServer.start();
            serversRunning = true;

            System.out.println("Maze servers started on ports 5400 and 5401");
        }
    }

    public static void stopServers() {
        if (serversRunning) {
            if (generateServer != null) generateServer.stop();
            if (solveServer != null) solveServer.stop();
            generateServer = null;
            solveServer = null;
            serversRunning = false;
            System.out.println("Maze servers stopped.");
        }
    }
}