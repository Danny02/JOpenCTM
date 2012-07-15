package darwin.jlzmaio;

// ConcurrentBufferInputStream.java -- read bytes from blocking queue
// Copyright (c)2007 Christopher League <league@contrapunctus.net>
// modifyed by Daniel Heinrich <DannyNullZwo@gmail.com>
// This is free software, but it comes with ABSOLUTELY NO WARRANTY.
// GNU Lesser General Public License 2.1 or Common Public License 1.0
import java.io.*;
import java.util.concurrent.BlockingQueue;

class ConcurrentBufferInputStream extends InputStream
{

    private BlockingQueue<byte[]> q;
    private byte[] buf = null;
    private int next = 0;
    private boolean eof = false;

    ConcurrentBufferInputStream(BlockingQueue<byte[]> q)
    {
        this.q = q;
        this.eof = false;
    }

    public void makeReady() throws IOException
    {
        if (buf == null) {
            buf = guarded_take();
        }
    }

    private byte[] guarded_take() throws IOException
    {
        try {
            return q.take();
        } catch (InterruptedException exn) {
            throw new InterruptedIOException(exn.getMessage());
        }
    }

    private boolean prepareAndCheckEOF() throws IOException
    {
        if (eof) {
            return true;
        }
        if (buf == null || next >= buf.length) {
            buf = guarded_take();
            next = 0;
            if (buf.length == 0) {
                eof = true;
                return true;
            }
        }
        return false;
    }

    public int read() throws IOException
    {
        if (prepareAndCheckEOF()) {
            return -1;
        }
        int x = buf[next];
        next++;
        return x & 0xff;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        if (prepareAndCheckEOF()) {
            return -1;
        }
        int k = buf.length - next;
        if (len < k) {
            k = len;
        }
        System.arraycopy(buf, next, b, off, k);
        next += k;
        return k;
    }

    @Override
    public int available() throws IOException
    {
        return buf.length;
    }

    @Override
    public String toString()
    {
        return String.format("cbIn@%x", hashCode());
    }
}
