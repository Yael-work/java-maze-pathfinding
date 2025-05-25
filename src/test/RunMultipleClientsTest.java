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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * This class tests the client-server implementation with multiple clients
 * connecting to the servers concurrently.
 */
public class RunMultipleClientsTest {

    private static final int NUM_CLIENTS = 10; // Number of clients to test with

    @Test
    public void testMultipleClients() throws Exception {
        // Start servers
        Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        Server solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();

        try {
            // Use CountDownLatch to wait for all clients to finish
            CountDownLatch latch = new CountDownLatch(NUM_CLIENTS);

            // Track success of each client
            boolean[] clientSuccess = new boolean[NUM_CLIENTS];
            Maze[] generatedMazes = new Maze[NUM_CLIENTS];
            int[] solutionSteps = new int[NUM_CLIENTS];

            // Create and start multiple client threads
            Thread[] clientThreads = new Thread[NUM_CLIENTS];
            for (int i = 0; i < NUM_CLIENTS; i++) {
                final int clientId = i + 1;
                final int index = i;
                clientThreads[i] = new Thread(() -> {
                    try {
                        System.out.println("[DEBUG_LOG] Client " + clientId + " starting...");

                        // Generate maze using server
                        Maze maze = generateMaze(clientId);
                        if (maze != null) {
                            generatedMazes[index] = maze;

                            // Solve maze using server
                            solutionSteps[index] = solveMaze(clientId, maze);

                            // If we got here without exceptions, mark as success
                            clientSuccess[index] = (maze != null && solutionSteps[index] > 0);
                        }

                        System.out.println("[DEBUG_LOG] Client " + clientId + " finished. Success: " + clientSuccess[index]);
                        latch.countDown();
                    } catch (Exception e) {
                        System.err.println("[DEBUG_LOG] Error in client " + clientId + ": " + e.getMessage());
                        e.printStackTrace();
                        clientSuccess[index] = false;
                        latch.countDown();
                    }
                });
                clientThreads[i].start();
            }

            // Wait for all clients to finish
            latch.await();
            System.out.println("[DEBUG_LOG] All clients have finished.");

            // Assert that all clients were successful
            for (int i = 0; i < NUM_CLIENTS; i++) {
                assertTrue(clientSuccess[i], "Client " + (i+1) + " should succeed");
                assertNotNull(generatedMazes[i], "Client " + (i+1) + " should generate a maze");
                assertTrue(solutionSteps[i] > 0, "Client " + (i+1) + " should receive a valid solution");
            }

        } finally {
            // Stop servers
            mazeGeneratingServer.stop();
            solveSearchProblemServer.stop();
            System.out.println("[DEBUG_LOG] Servers stopped.");
        }
    }

    private static Maze generateMaze(int clientId) {
        try {
            final Maze[] generatedMaze = new Maze[1]; // use array to modify from inside lambda

            Client client = new Client(InetAddress.getLocalHost(), 5400, (inFromServer, outToServer) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    toServer.flush();

                    // Send maze dimensions - vary slightly by client ID to make them different
                    int[] mazeDimensions = new int[]{40 + clientId, 40 + clientId};
                    toServer.writeObject(mazeDimensions);
                    toServer.flush();

                    // Receive compressed maze
                    byte[] compressedMaze = (byte[]) fromServer.readObject();
                    InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                    byte[] decompressedMaze = new byte[(40 + clientId) * (40 + clientId) + 12];
                    is.read(decompressedMaze);

                    // Construct maze
                    Maze maze = new Maze(decompressedMaze);
                    generatedMaze[0] = maze;
                    System.out.println("[DEBUG_LOG] Client " + clientId + " generated a " + maze.getRows() + "x" + maze.getColumns() + " maze.");
                } catch (Exception e) {
                    System.err.println("[DEBUG_LOG] Client " + clientId + " error generating maze: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            client.communicateWithServer();
            return generatedMaze[0];

        } catch (UnknownHostException e) {
            System.err.println("[DEBUG_LOG] Client " + clientId + " host error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static int solveMaze(int clientId, Maze maze) {
        final int[] solutionSize = {0}; // Use array to store value from lambda

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
                    solutionSize[0] = solution.getSolutionPath().size();
                    System.out.println("[DEBUG_LOG] Client " + clientId + " received solution with " +
                            solutionSize[0] + " steps.");
                } catch (Exception e) {
                    System.err.println("[DEBUG_LOG] Client " + clientId + " error solving maze: " + e.getMessage());
                    e.printStackTrace();
                    solutionSize[0] = 0; // Mark as failed
                }
            });

            client.communicateWithServer();
            return solutionSize[0];
        } catch (UnknownHostException e) {
            System.err.println("[DEBUG_LOG] Client " + clientId + " host error: " + e.getMessage());
            e.printStackTrace();
            return 0; // Return 0 to indicate failure
        }
    }
}
