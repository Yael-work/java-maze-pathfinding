package IO;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;

/**
 * An enhanced decompressor that uses DEFLATE algorithm for decompression.
 * This corresponds to the EnhancedCompressorOutputStream.
 */

public class MyDecompressorInputStream extends InputStream {
    private final InputStream in;
    private final Inflater inflater;
    private final byte[] buffer = new byte[1024]; // read buffer for raw bytes
    private int bufferPos = 0;
    private int bufferLen = 0;

    public MyDecompressorInputStream(InputStream in) {
        this.in = in;
        this.inflater = new Inflater(); // setup inflater for decompression
    }

    @Override
    public int read() throws IOException {
        if (bufferPos >= bufferLen) {
            bufferLen = in.read(buffer); // refill buffer if empty
            bufferPos = 0;
            if (bufferLen == -1) {
                return -1;
            }
        }
        return buffer[bufferPos++] & 0xFF; // return unsigned byte
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (b.length < 12) throw new IOException("Byte array too small");

        int bytesRead = in.read(b, 0, 12); // read uncompressed header
        if (bytesRead != 12) throw new IOException("Failed to read header");

        byte[] compressedData = new byte[b.length * 2]; // allocate buffer for compressed data
        int compressedSize = 0;
        int read;

        while ((read = in.read(compressedData, compressedSize, compressedData.length - compressedSize)) != -1) {
            compressedSize += read; // accumulate compressed bytes
        }

        inflater.setInput(compressedData, 0, compressedSize); // set input for inflater

        try {
            int decompressedSize = inflater.inflate(b, 12, b.length - 12); // decompress into byte array
            return 12 + decompressedSize;
        } catch (Exception e) {
            throw new IOException("Decompression failed", e);
        } finally {
            inflater.end(); // release inflater resources
        }
    }

    @Override
    public void close() throws IOException {
        inflater.end(); // release inflater
        in.close();
        super.close();
    }
}
