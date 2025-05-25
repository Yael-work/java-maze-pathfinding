package IO;

import java.io.IOException;
import java.io.OutputStream;

public class MyCompressorOutputStream extends OutputStream {
    private final OutputStream out;

    public MyCompressorOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (b.length < 12) throw new IOException("Byte array too small");

        // 1. Write header uncompressed (first 12 bytes)
        out.write(b, 0, 12);

        // 2. Compress the maze body
        int current = b[12] & 0xFF;  // fix: unsigned and int
        int count = 1;
        out.write(current); // write first bit (0 or 1)

        for (int i = 13; i < b.length; i++) {
            int value = b[i] & 0xFF;
            if (value == current && count < 255) {
                count++;
            } else {
                out.write(count);
                current = value;
                count = 1;
            }
        }

        out.write(count); // final count
    }
}
