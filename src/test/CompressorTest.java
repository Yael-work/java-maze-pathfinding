package test;

import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.AMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;

import java.io.*;
import java.util.Arrays;

public class CompressorTest {
    public static void main(String[] args) {
        try {
            // 1. Generate maze and serialize
            AMazeGenerator generator = new MyMazeGenerator();
            Maze maze = generator.generate(1000, 1000);
            byte[] originalBytes = maze.toByteArray();

            // 2. Compress using in-memory stream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MyCompressorOutputStream compressor = new MyCompressorOutputStream(baos);
            compressor.write(originalBytes);
            compressor.flush();
            compressor.close();

            byte[] compressedBytes = baos.toByteArray();
            System.out.println("Original size: " + originalBytes.length);
            System.out.println("Compressed size: " + compressedBytes.length);

            // 3. Decompress
            ByteArrayInputStream bais = new ByteArrayInputStream(compressedBytes);
            MyDecompressorInputStream decompressor = new MyDecompressorInputStream(bais);
            byte[] decompressedBytes = new byte[originalBytes.length];
            decompressor.read(decompressedBytes);
            decompressor.close();

            // 4. Compare byte arrays
            boolean match = Arrays.equals(originalBytes, decompressedBytes);
            System.out.println("Decompressed matches original: " + match);

            if (!match) {
                for (int i = 0; i < originalBytes.length; i++) {
                    if (originalBytes[i] != decompressedBytes[i]) {
                        System.out.printf("Mismatch at index %d: original=%d, decompressed=%d%n",
                                i, originalBytes[i], decompressedBytes[i]);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
