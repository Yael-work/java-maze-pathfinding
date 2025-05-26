package IO;

import java.io.IOException;
import java.io.InputStream;

public class SimpleDecompressorInputStream extends InputStream {
    private final InputStream in;

    public SimpleDecompressorInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }


    @Override
    public int read(byte[] b) throws IOException {
        if (b.length < 12)
            throw new IOException("array too small for maze header");

        int bytesRead = in.read(b, 0, 12);
        if (bytesRead != 12)
            throw new IOException("could not read full maze header");

        int index = 12;

        while (index < b.length) {
            // Read bit value
            int currentBit = in.read();
            if (currentBit == -1)
                throw new IOException("Unexpected end of stream");
            if (currentBit != 0 && currentBit != 1)
                throw new IOException(" bit must be 0 or 1");

            // Read count
            int count = in.read();
            if (count == -1)
                throw new IOException("Unexpected end of stream");

            count = count & 0xFF;

            // Write the same bit 'count' times
            for (int i = 0; i < count && index < b.length; i++) {
                b[index++] = (byte) currentBit;
            }
        }

        return index;
    }
}