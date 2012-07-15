package darwin.jlzmaio;

// DecoderThread.java -- run LZMA decoder in a separate thread
// Copyright (c)2007 Christopher League <league@contrapunctus.net>
// modifyed by Daniel Heinrich <DannyNullZwo@gmail.com>
// This is free software, but it comes with ABSOLUTELY NO WARRANTY.
// GNU Lesser General Public License 2.1 or Common Public License 1.0
import lzma.Compression.LZMA.Decoder;
import java.io.*;
import java.util.concurrent.BlockingQueue;

class DecoderThread extends Thread
{

    private static final byte[] props = new byte[]{0x5d, 0x00, 0x00, 0x10, 0x00};
    private InputStream in;
    private OutputStream out;
    private Decoder dec;
    private IOException exn;
    private long outSize;

    public DecoderThread(InputStream in, BlockingQueue<byte[]> q)
    {
        this.in = in;
        out = ConcurrentBufferOutputStream.create(q);
        dec = new Decoder();
        exn = null;
    }

    @Override
    public void run()
    {
        try {
            if (LzmaOutputStream.LZMA_HEADER) {
                int n = in.read(props, 0, props.length);
                if (n != props.length) {
                    throw new IOException("input .lzma file is too short");
                }
                dec.SetDecoderProperties(props);
                for (int i = 0; i < 8; i++) {
                    int v = in.read();
                    if (v < 0) {
                        throw new IOException("Can't read stream size");
                    }
                    outSize |= ((long) v) << (8 * i);
                }
            } else {
                outSize = -1;
                dec.SetDecoderProperties(props);
            }
            dec.Code(in, out, outSize);
            in.close(); //?
        } catch (IOException ex) {
            exn = ex;
        }
        // close either way, so listener can unblock
        try {
            out.close();
        } catch (IOException ex) {
        }
    }

    public void maybeThrow() throws IOException
    {
        if (exn != null) {
            throw exn;
        }
    }

    public long getSize()
    {
        return outSize;
    }

    @Override
    public String toString()
    {
        return String.format("Dec@%x", hashCode());
    }
}
