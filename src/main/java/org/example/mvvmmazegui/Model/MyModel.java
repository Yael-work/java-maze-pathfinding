package org.example.mvvmmazegui.Model;

// this class is responsible for the core maze logic including generating, solving, and controlling player movement
// it supports server-based and local algorithms and notifies the view model on key events

import org.example.Client.Client;
import org.example.IO.MyDecompressorInputStream;
import org.example.algorithms.mazeGenerators.*;
import org.example.algorithms.search.*;

import java.io.*;
import java.net.InetAddress;
import java.util.Stack;
import java.util.function.Consumer;

public class MyModel implements IModel {
    private Maze maze;
    private Solution solution;
    private int playerRow, playerCol;
    private Consumer<String> changeListener; // used for communication with viewModel (notification )

    private int stepCount = 0;
    private long startTime = 0;

    //used for redo and undo
    private final Stack<int[]> undoStack = new Stack<>();
    private final Stack<int[]> redoStack = new Stack<>();

    // generates a new maze using either a remote server or a local generator
    // resets player position and clears history stacks
    @Override
    public void generateMaze(int rows, int cols) {
        String genAlgo = Configurations.getInstance().getMazeGeneratingAlgorithm();
        IMazeGenerator generator;
        switch (genAlgo) {
            case "SimpleMazeGenerator" -> generator = new SimpleMazeGenerator();
            case "EmptyMazeGenerator" -> generator = new EmptyMazeGenerator();
            default -> generator = new MyMazeGenerator();
        }

        // Try client-server approach first
        try {
            Client client = new Client(getServerAddress(), 5400, (in, out) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(out);
                    ObjectInputStream fromServer = new ObjectInputStream(in);

                    toServer.flush();
                    toServer.writeObject(new int[]{rows, cols});
                    toServer.flush();

                    byte[] compressedMaze = (byte[]) fromServer.readObject();
                    InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                    byte[] decompressed = new byte[rows * cols + 100];
                    is.read(decompressed);

                    maze = new Maze(decompressed);
                    playerRow = maze.getStartPosition().getRowIndex();
                    playerCol = maze.getStartPosition().getColumnIndex();
                    stepCount = 0;
                    startTime = System.currentTimeMillis();
                    undoStack.clear();
                    redoStack.clear();
                    notifyChange("mazeGenerated");
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error communicating with server: " + e.getMessage());
                    // Will fall back to local generation
                    maze = null;
                }
            });
            client.communicateWithServer();
        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            // Fall back to local generation
            maze = null;
        }

        // If client-server approach failed, use local generation
        if (maze == null) {
            try {
                // Use the selected generator
                maze = generator.generate(rows, cols);
                playerRow = maze.getStartPosition().getRowIndex();
                playerCol = maze.getStartPosition().getColumnIndex();
                stepCount = 0;
                startTime = System.currentTimeMillis();
                undoStack.clear();
                redoStack.clear();
                notifyChange("mazeGenerated");
            } catch (Exception e) {
                System.err.println("Error generating maze locally: " + e.getMessage());
                throw new RuntimeException("Failed to generate maze", e);
            }
        }
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void setMaze(Maze maze) {
        this.maze = maze;
        this.playerRow = maze.getStartPosition().getRowIndex();
        this.playerCol = maze.getStartPosition().getColumnIndex();
        notifyChange("mazeLoaded");
    }

    // solves the current maze using either a remote server or a local search algorithm
    @Override
    public void solveMaze() {
        if (maze == null) return;

        try {
            // Try client-server approach first
            Client client = new Client(getServerAddress(), 5401, (in, out) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(out);
                    ObjectInputStream fromServer = new ObjectInputStream(in);

                    toServer.flush();
                    toServer.writeObject(maze);
                    toServer.flush();

                    solution = (Solution) fromServer.readObject();
                    notifyChange("mazeSolved");
                } catch (IOException | ClassNotFoundException e) {
                    // Log the error but don't throw exception
                    System.err.println("Error communicating with server: " + e.getMessage());
                    // Will fall back to local solution
                    solution = null;
                }
            });
            client.communicateWithServer();
        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            // Fall back to local solution
            solution = null;
        }

        // If client-server approach failed, use local search algorithm
        if (solution == null) {
            try {
                // Use BFS as a fallback
                SearchableMaze searchableMaze = new SearchableMaze(maze);
                BreadthFirstSearch bfs = new BreadthFirstSearch();
                solution = bfs.solve(searchableMaze);
                notifyChange("mazeSolved");
            } catch (Exception e) {
                System.err.println("Error solving maze locally: " + e.getMessage());
            }
        }
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    // moves the player in the specified direction (0-7) if the move is valid
    // also records move for undo/redo and triggers view update
    @Override
    public void movePlayer(int direction) {
        int[][] moves = {
            {-1, 0}, {0, 1}, {1, 0}, {0, -1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        int newRow = playerRow + moves[direction][0];
        int newCol = playerCol + moves[direction][1];
        if (maze != null &&
                newRow >= 0 && newRow < maze.getRows() &&
                newCol >= 0 && newCol < maze.getColumns() &&
                maze.getCell(newRow, newCol) == 0) {
            undoStack.push(new int[]{playerRow, playerCol});
            redoStack.clear();
            playerRow = newRow;
            playerCol = newCol;
            stepCount++;
            notifyChange("playerMoved");
        }
    }

    // undoes the last player move and updates state
    @Override
    public void undoMove() {
        if (!undoStack.isEmpty()) {
            redoStack.push(new int[]{playerRow, playerCol});
            int[] previousPosition = undoStack.pop();
            playerRow = previousPosition[0];
            playerCol = previousPosition[1];
            stepCount++;
            notifyChange("playerMoved");
        }
    }

    // redoes the previously undone move and updates state
    @Override
    public void redoMove() {
        if (!redoStack.isEmpty()) {
            undoStack.push(new int[]{playerRow, playerCol});
            int[] nextPosition = redoStack.pop();
            playerRow = nextPosition[0];
            playerCol = nextPosition[1];
            stepCount++;
            notifyChange("playerMoved");
        }
    }

    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    private InetAddress getServerAddress() throws IOException {
        return InetAddress.getLocalHost();
    }

    @Override
    public void setChangeListener(Consumer<String> callback) {
        this.changeListener = callback;
    }

    // invokes the change listener to notify observers (typically the view model)
    private void notifyChange(String event) {
        if (changeListener != null) {
            changeListener.accept(event);
        }
    }

    @Override
    public int getStepCount() {
        return stepCount;
    }

    @Override
    public long getElapsedTimeSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
    // resets player position and state to the start of the current maze
    @Override
    public void restartMaze() {
        if (maze == null) return;
        this.playerRow = maze.getStartPosition().getRowIndex();
        this.playerCol = maze.getStartPosition().getColumnIndex();
        this.stepCount = 0;
        this.startTime = System.currentTimeMillis();
        notifyChange("mazeRestarted");
    }
    @Override
    public void clearSolution() {
        this.solution = null;
    }
}
