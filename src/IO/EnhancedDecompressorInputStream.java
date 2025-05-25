package IO;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;

/**
 * An enhanced decompressor that uses DEFLATE algorithm for decompression.
 * This corresponds to the EnhancedCompressorOutputStream.
 */
public class EnhancedDecompressorInputStream extends InputStream {
    private final InputStream in;
    private final Inflater inflater;
    private final byte[] buffer = new byte[1024];
    private int bufferPos = 0;
    private int bufferLen = 0;

    public EnhancedDecompressorInputStream(InputStream in) {
        this.in = in;
        this.inflater = new Inflater();
    }

    @Override
    public int read() throws IOException {
        if (bufferPos >= bufferLen) {
            bufferLen = in.read(buffer);
            bufferPos = 0;
            if (bufferLen == -1) {
                return -1;
            }
        }
        return buffer[bufferPos++] & 0xFF;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (b.length < 12) throw new IOException("Byte array too small");

        // 1. Read header first (12 bytes)
        int bytesRead = in.read(b, 0, 12);
        if (bytesRead != 12) throw new IOException("Failed to read header");

        // 2. Read compressed data into a temporary buffer
        byte[] compressedData = new byte[b.length * 2]; // Allocate enough space
        int compressedSize = 0;
        int read;
        
        while ((read = in.read(compressedData, compressedSize, compressedData.length - compressedSize)) != -1) {
            compressedSize += read;
        }

        // 3. Decompress the data
        inflater.setInput(compressedData, 0, compressedSize);
        
        try {
            int decompressedSize = inflater.inflate(b, 12, b.length - 12);
            return 12 + decompressedSize;
        } catch (Exception e) {
            throw new IOException("Decompression failed", e);
        } finally {
            inflater.end();
        }
    }

    @Override
    public void close() throws IOException {
        inflater.end();
        in.close();
        super.close();
    }
}