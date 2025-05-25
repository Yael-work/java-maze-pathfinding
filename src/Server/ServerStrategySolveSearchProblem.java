package Server;

import algorithms.mazeGenerators.Maze;
import algorithms.search.*;
import IO.MyCompressorOutputStream;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class ServerStrategySolveSearchProblem implements IServerStrategy {
    private final HashMap<Integer, Solution> solutionCache = new HashMap<>();

    @Override
    public void applyStrategy(InputStream inFromClient, OutputStream outToClient) {
        try {
            ObjectInputStream fromClient = new ObjectInputStream(inFromClient);
            ObjectOutputStream toClient = new ObjectOutputStream(outToClient);
            toClient.flush();

            Maze maze = (Maze) fromClient.readObject();
            int hash = Arrays.hashCode(maze.toByteArray());

            Solution solution;
            File solFile = new File(System.getProperty("java.io.tmpdir") + "/maze_" + hash + ".sol");

            if (solFile.exists()) {
                ObjectInputStream fromDisk = new ObjectInputStream(new FileInputStream(solFile));
                solution = (Solution) fromDisk.readObject();
                fromDisk.close();
            } else {
                ISearchingAlgorithm searcher = (ISearchingAlgorithm) Class
                        .forName("algorithms.search." +
                                Configurations.getInstance().getMazeSearchingAlgorithm())
                        .getDeclaredConstructor().newInstance();

                SearchableMaze searchableMaze = new SearchableMaze(maze);
                solution = searcher.solve(searchableMaze);

                ObjectOutputStream toDisk = new ObjectOutputStream(new FileOutputStream(solFile));
                toDisk.writeObject(solution);
                toDisk.flush();
                toDisk.close();
            }

            toClient.writeObject(solution);
            toClient.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}