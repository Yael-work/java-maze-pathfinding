package IO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

/**
 * An enhanced compressor that uses DEFLATE algorithm for better compression.
 * This provides much better compression ratios than simple RLE for most data.
 */
public class EnhancedCompressorOutputStream extends OutputStream {
    private final OutputStream out;
    private final Deflater deflater;
    private final byte[] buffer = new byte[1024];

    public EnhancedCompressorOutputStream(OutputStream out) {
        this.out = out;
        this.deflater = new Deflater(Deflater.BEST_COMPRESSION);
    }

    @Override
    public void write(int b) throws IOException {
        byte[] data = {(byte) b};
        deflater.setInput(data);
        while (!deflater.needsInput()) {
            deflate();
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (b.length < 12) throw new IOException("Byte array too small");

        // 1. Write header uncompressed (first 12 bytes)
        out.write(b, 0, 12);

        // 2. Compress the maze body using DEFLATE
        deflater.setInput(b, 12, b.length - 12);
        deflater.finish();
        
        while (!deflater.finished()) {
            deflate();
        }
    }

    private void deflate() throws IOException {
        int count = deflater.deflate(buffer);
        if (count > 0) {
            out.write(buffer, 0, count);
        }
    }

    @Override
    public void close() throws IOException {
        deflater.end();
        out.close();
        super.close();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }
}