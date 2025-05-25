package test;

import Client.Client;
import Client.IClientStrategy;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.AState;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class RunCommunicateWithServers {
    public static void main(String[] args) {
        // Initialize servers
        Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        Server solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());

        // Start servers
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();

        // Communicate with servers
        CommunicateWithServer_MazeGenerating();
        CommunicateWithServer_SolveSearchProblem();

        // Stop servers
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }

    private static void CommunicateWithServer_MazeGenerating() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, (inFromServer, outToServer) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    toServer.flush();

                    int[] mazeDimensions = new int[]{50, 50};
                    toServer.writeObject(mazeDimensions);
                    toServer.flush();

                    byte[] compressedMaze = (byte[]) fromServer.readObject();

                    InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                    byte[] decompressedMaze = new byte[50 * 50 + 12];  // Maze + header
                    is.read(decompressedMaze);

                    Maze maze = new Maze(decompressedMaze);
                    maze.print();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, (inFromServer, outToServer) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    toServer.flush();

                    MyMazeGenerator generator = new MyMazeGenerator();
                    Maze maze = generator.generate(50, 50);
                    maze.print();

                    toServer.writeObject(maze); // Send maze to server
                    toServer.flush();

                    Solution mazeSolution = (Solution) fromServer.readObject();

                    System.out.println("Solution steps:");
                    ArrayList<AState> steps = mazeSolution.getSolutionPath();
                    for (int i = 0; i < steps.size(); i++) {
                        System.out.printf("%d. %s%n", i, steps.get(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
