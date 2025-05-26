package test;

import IO.SimpleCompressorOutputStream;
import IO.SimpleDecompressorInputStream;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class RLECompressionTest {

    public static void main(String[] args) throws IOException {
        // Maze-based tests
        testMazeCompress(generateMazeWithValue(10, 10, 0), "Maze: All Zeros");
        testMazeCompress(generateMazeWithValue(10, 10, 1), "Maze: All Ones");
        testMazeCompress(generateAlternatingMaze(1, 100), "Maze: Alternating 0,1");
        testMazeCompress(generateMaxRunLengthMaze(), "Maze: Max 260 Zeros and 260 Ones");
        testMazeCompress(generateRandomMaze(10, 10), "Maze: Random 0/1 Pattern");

        // Byte-level structured tests
        testStructuredBytePatterns();
        testMalformedCompressedStream();
    }

    // ========== MAZE-BASED TESTS ==========

    private static Maze generateMazeWithValue(int rows, int cols, int value) {
        Maze maze = new Maze(rows, cols);
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                maze.setCell(r, c, value);
        maze.setStartPosition(new Position(0, 0));
        maze.setGoalPosition(new Position(rows - 1, cols - 1));
        return maze;
    }

    private static Maze generateAlternatingMaze(int rows, int cols) {
        Maze maze = new Maze(rows, cols);
        for (int i = 0; i < rows * cols; i++) {
            maze.setCell(i / cols, i % cols, i % 2);
        }
        maze.setStartPosition(new Position(0, 0));
        maze.setGoalPosition(new Position(rows - 1, cols - 1));
        return maze;
    }

    private static Maze generateMaxRunLengthMaze() {
        Maze maze = new Maze(1, 520);
        for (int i = 0; i < 260; i++) {
            maze.setCell(0, i, 0);
            maze.setCell(0, 260 + i, 1);
        }
        maze.setStartPosition(new Position(0, 0));
        maze.setGoalPosition(new Position(0, 519));
        return maze;
    }

    private static Maze generateRandomMaze(int rows, int cols) {
        Maze maze = new Maze(rows, cols);
        Random rand = new Random();
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                maze.setCell(r, c, rand.nextBoolean() ? 1 : 0);
        maze.setStartPosition(new Position(0, 0));
        maze.setGoalPosition(new Position(rows - 1, cols - 1));
        return maze;
    }

    private static void testMazeCompress(Maze maze, String description) {
        try {
            byte[] original = maze.toByteArray();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new SimpleCompressorOutputStream(baos).write(original);
            byte[] compressed = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
            byte[] decompressed = new byte[original.length];
            new SimpleDecompressorInputStream(bais).read(decompressed);

            boolean equal = Arrays.equals(original, decompressed);
            System.out.printf("[%s] Passed: %s | Compression: %.2f%%%n", description, equal ? "✅" : "❌",
                    100.0 * compressed.length / original.length);

            if (!equal) debugMismatch(original, decompressed);

        } catch (IOException e) {
            System.out.printf("[%s] ❌ Exception: %s%n", description, e.getMessage());
        }
    }

    // ========== BYTE-LEVEL TESTS ==========

    private static void testStructuredBytePatterns() throws IOException {
        // 12-byte header
        byte[] header = new byte[12];
        header[1] = 10; header[3] = 10; header[9] = 9; header[11] = 9;

        byte[][] patterns = new byte[7][];
        String[] labels = {
                "Structured 0/1/0/1 + random",
                "All Zeros",
                "All Ones",
                "Alternating 0,1",
                "Long runs (300 0s then 300 1s)",
                "Edge runs (255,10,255,10)",
                "Max Alternating (255 pairs)"
        };

        // 1. Structured
        byte[] p0 = new byte[512];
        System.arraycopy(header, 0, p0, 0, 12);
        for (int i = 12; i < 112; i++) p0[i] = (byte)(i % 2);
        Arrays.fill(p0, 112, 212, (byte)0);
        Arrays.fill(p0, 212, 312, (byte)1);
        for (int i = 312; i < 412; i++) p0[i] = (byte)(i % 2);
        for (int i = 412; i < 512; i++) p0[i] = (byte)(Math.random() > 0.5 ? 1 : 0);
        patterns[0] = p0;

        // 2. All 0s
        byte[] p1 = new byte[512]; System.arraycopy(header, 0, p1, 0, 12); patterns[1] = p1;

        // 3. All 1s
        byte[] p2 = new byte[512]; System.arraycopy(header, 0, p2, 0, 12);
        Arrays.fill(p2, 12, 512, (byte)1); patterns[2] = p2;

        // 4. Alternating
        byte[] p3 = new byte[512]; System.arraycopy(header, 0, p3, 0, 12);
        for (int i = 12; i < 512; i++) p3[i] = (byte)((i % 2)); patterns[3] = p3;

        // 5. Long 300/300
        byte[] p4 = new byte[612]; System.arraycopy(header, 0, p4, 0, 12);
        Arrays.fill(p4, 12, 312, (byte)0); Arrays.fill(p4, 312, 612, (byte)1); patterns[4] = p4;

        // 6. Edge runs
        byte[] p5 = new byte[542]; System.arraycopy(header, 0, p5, 0, 12);
        Arrays.fill(p5, 12, 267, (byte)0);
        Arrays.fill(p5, 267, 277, (byte)1);
        Arrays.fill(p5, 277, 532, (byte)0);
        Arrays.fill(p5, 532, 542, (byte)1); patterns[5] = p5;

        // 7. Max alternating
        byte[] p6 = new byte[522]; System.arraycopy(header, 0, p6, 0, 12);
        for (int i = 12; i < p6.length; i++) p6[i] = (byte)((i - 12) % 2); patterns[6] = p6;

        for (int i = 0; i < patterns.length; i++) {
            testBytePattern(patterns[i], labels[i]);
        }
    }

    private static void testBytePattern(byte[] original, String description) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new SimpleCompressorOutputStream(baos).write(original);
            byte[] compressed = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
            byte[] decompressed = new byte[original.length];
            new SimpleDecompressorInputStream(bais).read(decompressed);

            boolean equal = Arrays.equals(original, decompressed);
            System.out.printf("[%s] Passed: %s | Compression: %.2f%%%n", description, equal ? "✅" : "❌",
                    100.0 * compressed.length / original.length);

            if (!equal) debugMismatch(original, decompressed);

        } catch (IOException e) {
            System.out.printf("[%s] ❌ Exception: %s%n", description, e.getMessage());
        }
    }

    private static void testMalformedCompressedStream() {
        System.out.print("[Malformed] ");
        try {
            byte[] corrupted = {0, 10, 0, 10, 0, 0, 0, 0, 0, 9, 0, 9, 0}; // header + single bit, no count
            ByteArrayInputStream bais = new ByteArrayInputStream(corrupted);
            byte[] buffer = new byte[100];
            new SimpleDecompressorInputStream(bais).read(buffer);
            System.out.println("❌ Did not throw exception");
        } catch (IOException e) {
            System.out.println("✅ Threw expected IOException: " + e.getMessage());
        }
    }

    private static void debugMismatch(byte[] original, byte[] decompressed) {
        for (int i = 0; i < original.length; i++) {
            if (original[i] != decompressed[i]) {
                System.out.printf("Mismatch at index %d: original=%d, decompressed=%d%n",
                        i, original[i], decompressed[i]);
                for (int j = Math.max(0, i - 5); j <= Math.min(original.length - 1, i + 5); j++) {
                    System.out.printf("Index %d: original=%d, decompressed=%d%n",
                            j, original[j], decompressed[j]);
                }
                break;
            }
        }
    }
}
