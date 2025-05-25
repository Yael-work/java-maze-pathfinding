package test;

import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;

import java.io.*;
import java.util.Arrays;

public class SpecificCompressorTest {

    public static void main(String[] args) {
        try {
            // Create a test array with a proper header and a pattern that might trigger the issue at index 267
            byte[] testData = new byte[512]; // 12 bytes header + 500 bytes data

            // First 12 bytes: header (simulating a 10x10 maze with start at (0,0) and goal at (9,9))
            testData[0] = 0; // rows high byte
            testData[1] = 10; // rows low byte
            testData[2] = 0; // columns high byte
            testData[3] = 10; // columns low byte
            testData[4] = 0; // start row high byte
            testData[5] = 0; // start row low byte
            testData[6] = 0; // start column high byte
            testData[7] = 0; // start column low byte
            testData[8] = 0; // goal row high byte
            testData[9] = 9; // goal row low byte
            testData[10] = 0; // goal column high byte
            testData[11] = 9; // goal column low byte

            // First 100 bytes after header: alternating 0s and 1s
            for (int i = 12; i < 112; i++) {
                testData[i] = (byte)(i % 2);
            }

            // Next 100 bytes: all 0s
            for (int i = 112; i < 212; i++) {
                testData[i] = 0;
            }

            // Next 100 bytes: all 1s
            for (int i = 212; i < 312; i++) {
                testData[i] = 1;
            }

            // Next 100 bytes: alternating 0s and 1s again
            for (int i = 312; i < 412; i++) {
                testData[i] = (byte)(i % 2);
            }

            // Last 100 bytes: random pattern of 0s and 1s
            for (int i = 412; i < 512; i++) {
                testData[i] = (byte)(Math.random() > 0.5 ? 1 : 0);
            }

            // Ensure index 267 is 1 (to match the error description)
            testData[267] = 1;

            // Compress the data
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MyCompressorOutputStream compressor = new MyCompressorOutputStream(baos);

            // Use write(byte[] b) method instead of write(int b)
            compressor.write(testData);
            compressor.close();

            // Get the compressed data
            byte[] compressedData = baos.toByteArray();
            System.out.println("Original size: " + testData.length);
            System.out.println("Compressed size: " + compressedData.length);

            // Print the compressed data
            System.out.print("Compressed data: ");
            for (byte b : compressedData) {
                System.out.print((b & 0xFF) + " ");
            }
            System.out.println();

            // Verify that the compressed data is (255,0,45,0,5,1)
            boolean isFormatCorrect = compressedData.length == 6 && 
                                     (compressedData[0] & 0xFF) == 255 && 
                                     (compressedData[1] & 0xFF) == 0 && 
                                     (compressedData[2] & 0xFF) == 45 && 
                                     (compressedData[3] & 0xFF) == 0 && 
                                     (compressedData[4] & 0xFF) == 5 && 
                                     (compressedData[5] & 0xFF) == 1;

            System.out.println("Compressed format is correct: " + isFormatCorrect);

            // Decompress the data
            ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
            MyDecompressorInputStream decompressor = new MyDecompressorInputStream(bais);

            byte[] decompressedData = new byte[testData.length];
            // Use read(byte[] b) method instead of read()
            decompressor.read(decompressedData);
            decompressor.close();

            // Verify the decompressed data matches the original
            boolean areEqual = Arrays.equals(testData, decompressedData);
            System.out.println("Decompressed data matches original data: " + areEqual);

            // Check specifically for a mismatch at index 267
            if (!areEqual) {
                for (int i = 0; i < testData.length; i++) {
                    if (testData[i] != decompressedData[i]) {
                        System.out.printf("Mismatch at index %d: original=%d, decompressed=%d%n",
                                i, testData[i], decompressedData[i]);

                        // Check a few bytes before and after the mismatch
                        int start = Math.max(0, i - 5);
                        int end = Math.min(testData.length - 1, i + 5);
                        System.out.println("Values around the mismatch:");
                        for (int j = start; j <= end; j++) {
                            System.out.printf("Index %d: original=%d, decompressed=%d%n",
                                    j, testData[j], decompressedData[j]);
                        }

                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
