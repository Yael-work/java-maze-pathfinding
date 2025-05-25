package Server;

import algorithms.mazeGenerators.Maze;
import algorithms.search.*;
import IO.MyCompressorOutputStream;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class ServerStrategySolveSearchProblem implements IServerStrategy {
    private final HashMap<Integer, Solution> solutionCache = new HashMap<>(); // store solutions by maze hash

    @Override
    public void applyStrategy(InputStream inFromClient, OutputStream outToClient) {
        try {
            ObjectInputStream fromClient = new ObjectInputStream(inFromClient);
            ObjectOutputStream toClient = new ObjectOutputStream(outToClient);
            toClient.flush();

            Maze maze = (Maze) fromClient.readObject(); //receive maze
            int hash = Arrays.hashCode(maze.toByteArray()); //unique id for maze

            Solution solution;
            File solFile = new File(System.getProperty("java.io.tmpdir") + "/maze_" + hash + ".sol"); // temp file for solution

            if (solFile.exists()) {
                ObjectInputStream fromDisk = new ObjectInputStream(new FileInputStream(solFile));
                solution = (Solution) fromDisk.readObject(); //load cached solution
                fromDisk.close();
            } else {
                ISearchingAlgorithm searcher = (ISearchingAlgorithm) Class
                        .forName("algorithms.search." +
                                Configurations.getInstance().getMazeSearchingAlgorithm())
                        .getDeclaredConstructor().newInstance(); //dynamic search algorithm

                SearchableMaze searchableMaze = new SearchableMaze(maze);
                solution = searcher.solve(searchableMaze); //solve the maze

                ObjectOutputStream toDisk = new ObjectOutputStream(new FileOutputStream(solFile));
                toDisk.writeObject(solution); //save solution to disk
                toDisk.flush();
                toDisk.close();
            }

            toClient.writeObject(solution); //send solution back to client
            toClient.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
