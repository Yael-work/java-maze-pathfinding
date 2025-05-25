package IO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

/**
 * An enhanced compressor that uses DEFLATE algorithm for better compression.
 * This provides much better compression ratios than simple RLE for most data.
 */

public class MyCompressorOutputStream extends OutputStream {
    private final OutputStream out;
    private final Deflater deflater;
    private final byte[] buffer = new byte[1024]; // buffer for compressed data

    public MyCompressorOutputStream(OutputStream out) {
        this.out = out;
        this.deflater = new Deflater(Deflater.BEST_COMPRESSION); //use best compression level
    }

    @Override
    public void write(int b) throws IOException {
        byte[] data = {(byte) b}; // single byte array for compression
        deflater.setInput(data);
        while (!deflater.needsInput()) {
            deflate(); // compress  and write to output
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (b.length < 12) throw new IOException("Byte array too small");

        out.write(b, 0, 12); //write header uncompressed

        deflater.setInput(b, 12, b.length - 12); //compress the data after the header
        deflater.finish();

        while (!deflater.finished()) {
            deflate(); // write compressed chunks
        }
    }

    private void deflate() throws IOException {
        int count = deflater.deflate(buffer); //compress data into buffer
        if (count > 0) {
            out.write(buffer, 0, count); // write compressed bytes to output
        }
    }

    @Override
    public void close() throws IOException {
        deflater.end(); // release compression resources
        out.close();
        super.close();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }
}
