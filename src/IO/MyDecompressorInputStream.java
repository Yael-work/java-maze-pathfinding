package IO;

import java.io.IOException;
import java.io.InputStream;

public class MyDecompressorInputStream extends InputStream {
    private final InputStream in;

    public MyDecompressorInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (b.length < 12) throw new IOException("Byte array too small");

        // 1. Read header first (12 bytes)
        int bytesRead = in.read(b, 0, 12);
        if (bytesRead != 12) throw new IOException("Failed to read header");

        int index = 12;

        // 2. Read initial bit value (0 or 1), cast to unsigned
        int current = in.read() & 0xFF;

        // 3. Read and apply RLE counts
        while (index < b.length) {
            int count = in.read() & 0xFF; // read count as unsigned byte

            for (int i = 0; i < count && index < b.length; i++) {
                b[index++] = (byte) current;
            }

            current = 1 - current; // toggle between 0 and 1
        }

        return index;
    }
}
