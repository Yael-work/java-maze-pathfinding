package IO;

import java.io.IOException;
import java.io.OutputStream;

public class SimpleCompressorOutputStream extends OutputStream {
    private final OutputStream out;

    public SimpleCompressorOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (b.length < 12) throw new IOException("byte array too small");

        // Wwrite header (first 12 bytes) uncompressed
        out.write(b, 0, 12);

        // start compressing from index 12
        int current = b[12] & 0xFF;
        int count = 1;

        for (int i = 13; i < b.length; i++) {
            int value = b[i] & 0xFF;

            if (value == current && count < 255) {
                count++;
            } else {
                // write current run: bit value followed by count
                out.write(current);
                out.write(count);

                if (value == current) {
                    // still same value, just reached 255 cap → start a new run
                    count = 1;
                } else {
                    // toggle to new bit
                    current = value;
                    count = 1;
                }
            }
        }

        // write final run: bit value followed by count
        out.write(current);
        out.write(count);
    }
}