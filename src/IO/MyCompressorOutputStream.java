package IO;

import java.io.OutputStream;

public class MyCompressorOutputStream extends OutputStream {
    private OutputStream out;

    public MyCompressorOutputStream(OutputStream out) {
        this.out = out;
    }

    private int lastByte = -1;
    private int count = 0;

    @Override
    public void write(int b) {
        try {
            if (b == lastByte) {
                if (count == 255) {
                    out.write(count);
                    out.write(0); // Write 0 for the other bit
                    count = 0;
                }
                count++;
            } else {
                if (lastByte != -1) {
                    out.write(count);
                }
                lastByte = b;
                count = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (lastByte != -1) {
                out.write(lastByte);
                out.write(count);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
