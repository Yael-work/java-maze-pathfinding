package Server;

import IO.MyCompressorOutputStream;
import algorithms.mazeGenerators.AMazeGenerator;
import algorithms.mazeGenerators.Maze;

import java.io.*;

public class ServerStrategyGenerateMaze implements IServerStrategy {
    @Override
    public void applyStrategy(InputStream inFromClient, OutputStream outToClient) {
        try {
            ObjectInputStream fromClient = new ObjectInputStream(inFromClient);
            ObjectOutputStream toClient = new ObjectOutputStream(outToClient);
            toClient.flush();

            int[] dimensions = (int[]) fromClient.readObject(); // read maze size
            int rows = dimensions[0];
            int cols = dimensions[1];

            AMazeGenerator generator = (AMazeGenerator) Class
                    .forName("algorithms.mazeGenerators." +
                            Configurations.getInstance().getMazeGeneratingAlgorithm())
                    .getDeclaredConstructor().newInstance(); // dynamic generator class

            Maze maze = generator.generate(rows, cols); // generate maze
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            MyCompressorOutputStream compressor = new MyCompressorOutputStream(byteOut);
            compressor.write(maze.toByteArray()); // compress maze
            compressor.flush();
            compressor.close();

            toClient.writeObject(byteOut.toByteArray()); // send compressed maze
            toClient.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}