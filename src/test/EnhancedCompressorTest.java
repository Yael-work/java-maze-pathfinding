package test;

import IO.EnhancedCompressorOutputStream;
import IO.EnhancedDecompressorInputStream;
import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.AMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;

import java.io.*;
import java.util.Arrays;

public class EnhancedCompressorTest {
    public static void main(String[] args) {
        final int n = 100; // Number of test iterations
        int rleFailures = 0;
        int deflateFailures = 0;

        for (int i = 0; i < n; i++) {
            try {
                // 1. Generate maze and serialize
                AMazeGenerator generator = new MyMazeGenerator();
                Maze maze = generator.generate(1000, 1000);
                byte[] originalBytes = maze.toByteArray();

                // 2. Compress using RLE
                ByteArrayOutputStream baosRLE = new ByteArrayOutputStream();
                MyCompressorOutputStream compressorRLE = new MyCompressorOutputStream(baosRLE);
                compressorRLE.write(originalBytes);
                compressorRLE.flush();
                compressorRLE.close();
                byte[] compressedBytesRLE = baosRLE.toByteArray();

                // 3. Compress using DEFLATE
                ByteArrayOutputStream baosDEFLATE = new ByteArrayOutputStream();
                EnhancedCompressorOutputStream compressorDEFLATE = new EnhancedCompressorOutputStream(baosDEFLATE);
                compressorDEFLATE.write(originalBytes);
                compressorDEFLATE.flush();
                compressorDEFLATE.close();
                byte[] compressedBytesDEFLATE = baosDEFLATE.toByteArray();

                // 4. Decompress RLE
                ByteArrayInputStream baisRLE = new ByteArrayInputStream(compressedBytesRLE);
                MyDecompressorInputStream decompressorRLE = new MyDecompressorInputStream(baisRLE);
                byte[] decompressedBytesRLE = new byte[originalBytes.length];
                decompressorRLE.read(decompressedBytesRLE);
                decompressorRLE.close();

                // 5. Decompress DEFLATE
                ByteArrayInputStream baisDEFLATE = new ByteArrayInputStream(compressedBytesDEFLATE);
                EnhancedDecompressorInputStream decompressorDEFLATE = new EnhancedDecompressorInputStream(baisDEFLATE);
                byte[] decompressedBytesDEFLATE = new byte[originalBytes.length];
                decompressorDEFLATE.read(decompressedBytesDEFLATE);
                decompressorDEFLATE.close();

                // 6. Compare results
                boolean rleMatch = Arrays.equals(originalBytes, decompressedBytesRLE);
                boolean deflateMatch = Arrays.equals(originalBytes, decompressedBytesDEFLATE);

                if (!rleMatch) {
                    System.out.println("❌ RLE test failed at iteration " + i);
                    rleFailures++;
                }
                if (!deflateMatch) {
                    System.out.println("❌ DEFLATE test failed at iteration " + i);
                    deflateFailures++;
                }

            } catch (IOException e) {
                System.out.println("❗ Exception occurred at iteration " + i);
                e.printStackTrace();
            }
        }

        // Summary
        System.out.println("\n=== Test Summary ===");
        System.out.println("Total iterations: " + n);
        System.out.println("RLE failures: " + rleFailures);
        System.out.println("DEFLATE failures: " + deflateFailures);
    }
}
