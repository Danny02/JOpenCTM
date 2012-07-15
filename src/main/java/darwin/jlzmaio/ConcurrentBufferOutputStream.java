package darwin.jlzmaio;

// ConcurrentBufferOutputStream.java -- write bytes to blocking queue
// Copyright (c)2007 Christopher League <league@contrapunctus.net>
// modifyed by Daniel Heinrich <DannyNullZwo@gmail.com>

// This is free software, but it comes with ABSOLUTELY NO WARRANTY.
// GNU Lesser General Public License 2.1 or Common Public License 1.0
import java.io.*;
import java.util.concurrent.BlockingQueue;

class ConcurrentBufferOutputStream extends OutputStream
{
    private final BlockingQueue<byte[]> q;
    private static final int BUFSIZE = 16384;

    private ConcurrentBufferOutputStream(BlockingQueue<byte[]> q) {
        this.q = q;
    }

    public static OutputStream create(BlockingQueue<byte[]> q) {
        OutputStream out = new ConcurrentBufferOutputStream(q);
        out = new BufferedOutputStream(out, BUFSIZE);
        return out;
    }

    private void guarded_put(byte[] a) throws IOException {
        try {
            q.put(a);
        } catch (InterruptedException exn) {
            throw new InterruptedIOException(exn.getMessage());
        }
    }

    public void write(int i) throws IOException {
        byte b[] = new byte[1];
        b[0] = (byte) (i & 0xff);
        guarded_put(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] a = new byte[len];
        System.arraycopy(b, off, a, 0, len);
        guarded_put(a);
    }

    @Override
    public void close() throws IOException {
        byte b[] = new byte[0]; // sentinel
        guarded_put(b);
    }
}
