package darwin.jlzmaio;

// LzmaOutputStream.java -- transparently compress LZMA while writing
// Copyright (c)2007 Christopher League <league@contrapunctus.net>
// modifyed by Daniel Heinrich <DannyNullZwo@gmail.com>
// This is free software, but it comes with ABSOLUTELY NO WARRANTY.
// GNU Lesser General Public License 2.1 or Common Public License 1.0
import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LzmaOutputStream extends FilterOutputStream
{

    private EncoderThread eth;
    /**
     * true for compatibility with lzma(1) command-line tool, false for
     * compatibility with previous versions of LZMA streams.
     */
    public final static boolean LZMA_HEADER = true;
    private static final int QUEUESIZE = 4096;
    private final OutputStream ooo;

    public LzmaOutputStream(OutputStream out)
    {
        this(out, EncoderThread.DEFAULT_DICT_SZ_POW2, null, 0);
    }

    public LzmaOutputStream(OutputStream out, Integer dictSzPow2,
            Integer fastBytes, int lp)
    {
        super(null);
        ooo = out;
        BlockingQueue<byte[]> q = new ArrayBlockingQueue<byte[]>(QUEUESIZE);
        eth = new EncoderThread(out, q, dictSzPow2, fastBytes, lp);
        this.out = ConcurrentBufferOutputStream.create(q);
        eth.start();
    }

    @Override
    public void write(int i) throws IOException
    {
        IOException ex = eth.getException();
        if (ex != null) {
            throw ex;
        }

        out.write(i);
    }

    public void finish() throws IOException
    {
        out.close();
        try {
            eth.join();
        } catch (InterruptedException exn) {
            throw new InterruptedIOException(exn.getMessage());
        }

        IOException ex = eth.getException();
        if (ex != null) {
            throw ex;
        }
    }

    @Override
    public void close() throws IOException
    {
        ooo.close();
    }

    @Override
    public String toString()
    {
        return String.format("lzmaOut@%x", hashCode());
    }
}
