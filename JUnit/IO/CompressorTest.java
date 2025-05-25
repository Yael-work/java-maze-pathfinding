package IO;

import algorithms.mazeGenerators.AMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;

import java.io.*;
import java.util.Arrays;

public class CompressorTest {

    public static void main(String[] args) {
        try {
            // Generate a test maze
            AMazeGenerator mazeGenerator = new MyMazeGenerator();
            Maze maze = mazeGenerator.generate(10, 10); // Smaller maze for testing

            // Convert maze to byte array
            byte[] mazeBytes = convertMazeToByteArray(maze);

            // Create a ByteArrayOutputStream to hold the compressed data
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MyCompressorOutputStream compressor = new MyCompressorOutputStream(baos);

            // Write the maze bytes to the compressor
            for (byte b : mazeBytes) {
                compressor.write(b);
            }
            compressor.close();

            // Get the compressed data
            byte[] compressedData = baos.toByteArray();
            System.out.println("Original size: " + mazeBytes.length + ", Compressed size: " + compressedData.length);

            // Create a ByteArrayInputStream from the compressed data
            ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
            MyDecompressorInputStream decompressor = new MyDecompressorInputStream(bais);

            // Read the decompressed data
            byte[] decompressedData = new byte[mazeBytes.length];
            for (int i = 0; i < decompressedData.length; i++) {
                int value = decompressor.read();
                if (value == -1) {
                    System.out.println("Unexpected end of stream at index " + i);
                    break;
                }
                decompressedData[i] = (byte) value;
            }
            decompressor.close();

            // Compare the original and decompressed data
            boolean areEqual = Arrays.equals(mazeBytes, decompressedData);
            System.out.println("Decompressed data matches original data: " + areEqual);

            if (!areEqual) {
                // Print the first few differences
                int diffCount = 0;
                for (int i = 0; i < mazeBytes.length && diffCount < 10; i++) {
                    if (mazeBytes[i] != decompressedData[i]) {
                        System.out.println("Difference at index " + i + ": original=" + mazeBytes[i] + ", decompressed=" + decompressedData[i]);
                        diffCount++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] convertMazeToByteArray(Maze maze) {
        // Format: [rows(2 bytes), columns(2 bytes), startRow(2 bytes), startCol(2 bytes), goalRow(2 bytes), goalCol(2 bytes), maze data...]
        int rows = maze.getRows();
        int cols = maze.getColumns();
        byte[] mazeBytes = new byte[rows * cols + 12]; // 12 bytes for metadata

        // Store metadata
        mazeBytes[0] = (byte)(rows >> 8);
        mazeBytes[1] = (byte)(rows);
        mazeBytes[2] = (byte)(cols >> 8);
        mazeBytes[3] = (byte)(cols);

        mazeBytes[4] = (byte)(maze.getStartPosition().getRowIndex() >> 8);
        mazeBytes[5] = (byte)(maze.getStartPosition().getRowIndex());
        mazeBytes[6] = (byte)(maze.getStartPosition().getColumnIndex() >> 8);
        mazeBytes[7] = (byte)(maze.getStartPosition().getColumnIndex());

        mazeBytes[8] = (byte)(maze.getGoalPosition().getRowIndex() >> 8);
        mazeBytes[9] = (byte)(maze.getGoalPosition().getRowIndex());
        mazeBytes[10] = (byte)(maze.getGoalPosition().getColumnIndex() >> 8);
        mazeBytes[11] = (byte)(maze.getGoalPosition().getColumnIndex());

        // Store maze data
        int index = 12;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mazeBytes[index++] = (byte)maze.getCell(i, j);
            }
        }

        return mazeBytes;
    }
}
