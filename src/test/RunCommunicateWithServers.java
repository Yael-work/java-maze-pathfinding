package test;

import Client.Client;
import Client.IClientStrategy;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class RunCommunicateWithServers {

    public static void main(String[] args) {
        // Start servers
        Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        Server solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();

        // Generate maze using server
        Maze maze = CommunicateWithServer_MazeGenerating();
        if (maze != null) {
            // Solve maze using server
            CommunicateWithServer_SolveSearchProblem(maze);
        }

        // Stop servers
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }

    private static Maze CommunicateWithServer_MazeGenerating() {
        try {
            final Maze[] generatedMaze = new Maze[1]; // use array to modify from inside lambda

            Client client = new Client(InetAddress.getLocalHost(), 5400, (inFromServer, outToServer) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    toServer.flush();

                    // Send maze dimensions
                    int[] mazeDimensions = new int[]{50, 50};
                    toServer.writeObject(mazeDimensions);
                    toServer.flush();

                    // Receive compressed maze
                    byte[] compressedMaze = (byte[]) fromServer.readObject();
                    InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                    byte[] decompressedMaze = new byte[50 * 50 + 12];
                    is.read(decompressedMaze);

                    // Construct and print maze
                    Maze maze = new Maze(decompressedMaze);
                    generatedMaze[0] = maze;
                    maze.print();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            client.communicateWithServer();
            return generatedMaze[0];

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void CommunicateWithServer_SolveSearchProblem(Maze maze) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, (inFromServer, outToServer) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    toServer.flush();

                    // Send maze to server
                    toServer.writeObject(maze);
                    toServer.flush();

                    // Receive solution
                    Solution solution = (Solution) fromServer.readObject();
                    System.out.println("Solution steps:");
                    ArrayList<AState> steps = solution.getSolutionPath();
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
