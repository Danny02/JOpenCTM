/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.io;

import java.io.*;
import lzma.sdk.lzma.Decoder;
import org.cservenak.streams.Coder;
import org.cservenak.streams.CoderThread;

/**
 *
 * @author daniel
 */
public class PackedInputStream extends InputStream
{

    private final CoderThread ct;
    private volatile InputStream in;

    protected PackedInputStream(byte[] packedData) throws IOException
    {
        InputStream i = new ByteArrayInputStream(packedData);
        Coder c = new CustomWrapper(packedData.length);
        ct = new CoderThread(c, i);

        in = ct.getInputStreamSink();

        ct.start();
    }

    @Override
    public int read()
            throws IOException
    {
        return in.read();
    }

    @Override
    public int read(byte b[], int off, int len)
            throws IOException
    {
        return in.read(b, off, len);
    }

    @Override
    public long skip(long n)
            throws IOException
    {
        return in.skip(n);
    }

    @Override
    public int available()
            throws IOException
    {
        return in.available();
    }

    @Override
    public void close()
            throws IOException
    {
        in.close();

        try {
            ct.join();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        ct.checkForException();
    }

    @Override
    public synchronized void mark(int readlimit)
    {
        in.mark(readlimit);
    }

    @Override
    public synchronized void reset()
            throws IOException
    {
        in.reset();
    }

    @Override
    public boolean markSupported()
    {
        return in.markSupported();
    }

    private static class CustomWrapper implements Coder
    {

        private final Decoder d;
        private final int len;

        public CustomWrapper(int packedLength)
        {
            d = new Decoder();
            len = packedLength;
        }

        @Override
        public void code(final InputStream in, final OutputStream out)
                throws IOException
        {

            byte[] properties = new byte[5];
            if (in.read(properties) != 5) {
                throw new IOException("LZMA file has no header!");
            }

            if (!d.setDecoderProperties(properties)) {
                throw new IOException("Decoder properties cannot be set!");
            }

            if (!d.code(in, out, len)) {
                throw new IOException("Decoding unsuccessful!");
            }
        }
    }
}