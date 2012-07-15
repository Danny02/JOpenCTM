package darwin.jlzmaio;

// LzmaInputStream.java -- transparently decompress LZMA while reading
// Copyright (c)2007 Christopher League <league@contrapunctus.net>
// modifyed by Daniel Heinrich <DannyNullZwo@gmail.com>
// This is free software, but it comes with ABSOLUTELY NO WARRANTY.
// GNU Lesser General Public License 2.1 or Common Public License 1.0
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LzmaInputStream extends InputStream
{

    private static final int QUEUESIZE = 4096;
    private final DecoderThread dth;
    private final ConcurrentBufferInputStream input;

    public LzmaInputStream(InputStream in)
    {
        BlockingQueue<byte[]> q = new ArrayBlockingQueue<byte[]>(QUEUESIZE);
        dth = new DecoderThread(in, q);
        input = new ConcurrentBufferInputStream(q);
        dth.start();
    }

    @Override
    public int available() throws IOException
    {
        input.makeReady();
        return input.available();
    }

    @Override
    public int read() throws IOException
    {
        int k = input.read();
        dth.maybeThrow();
        return k;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        int k = input.read(b, off, len);
        dth.maybeThrow();
        return k;
    }

    @Override
    public String toString()
    {
        return String.format("lzmaIn@%x", hashCode());
    }
}
