package test;

import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;

import java.io.*;
import java.util.Arrays;

public class SpecificCompressorTest {

    public static void main(String[] args) {
        try {
            // Create a test array with 300 zeros followed by 5 ones
            byte[] testData = new byte[305];
            for (int i = 0; i < 300; i++) {
                testData[i] = 0;
            }
            for (int i = 300; i < 305; i++) {
                testData[i] = 1;
            }

            // Compress the data
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MyCompressorOutputStream compressor = new MyCompressorOutputStream(baos);

            for (byte b : testData) {
                compressor.write(b);
            }
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
            for (int i = 0; i < decompressedData.length; i++) {
                int value = decompressor.read();
                if (value == -1) {
                    System.out.println("Unexpected end of stream at index " + i);
                    break;
                }
                decompressedData[i] = (byte) value;
            }
            decompressor.close();

            // Verify the decompressed data matches the original
            boolean areEqual = Arrays.equals(testData, decompressedData);
            System.out.println("Decompressed data matches original data: " + areEqual);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
