package darwin.jlzmaio;

// EncoderThread.java -- run LZMA encoder in a separate thread
// Copyright (c)2007 Christopher League <league@contrapunctus.net>
// modifyed by Daniel Heinrich <DannyNullZwo@gmail.com>
// This is free software, but it comes with ABSOLUTELY NO WARRANTY.
// GNU Lesser General Public License 2.1 or Common Public License 1.0
import java.io.*;
import java.util.concurrent.BlockingQueue;
import lzma.Compression.LZMA.Encoder;

class EncoderThread extends Thread
{

    public static final Integer DEFAULT_DICT_SZ_POW2 = new Integer(20);
    private BlockingQueue<byte[]> q;
    private InputStream in;
    private OutputStream out;
    private Encoder enc = new Encoder();
    private IOException exn;

    /**
     * @param dictSzPow2 If non-null, equivalent to the N in the -dN arg to
     *                   LzmaAlone
     * @param fastBytes  If non-null, equivalent to the N in the -fbN arg to
     *                   LzmaAlone
     */
    public EncoderThread(OutputStream out, BlockingQueue<byte[]> q,
            Integer dictSzPow2, Integer fastBytes, int lp)
    {
        in = new ConcurrentBufferInputStream(q);
        this.out = out;
        enc.SetDictionarySize(1 << (dictSzPow2 == null ? DEFAULT_DICT_SZ_POW2 : dictSzPow2).intValue());
        if (fastBytes != null) {
            enc.SetNumFastBytes(fastBytes.intValue());
        }

        enc.SetAlgorithm(2);
        enc.SetLcLpPb(3, lp, 2);
    }

    @Override
    public void run()
    {
        try {
            enc.SetEndMarkerMode(true);
            if (LzmaOutputStream.LZMA_HEADER) {
                enc.WriteCoderProperties(out);
                // 5d 00 00 10 00
                long fileSize = -1;
                for (int i = 0; i < 8; i++) {
                    out.write((int) (fileSize >>> (8 * i)) & 0xFF);
                }
            }
            enc.Code(in, out, -1, -1, null);
        } catch (IOException ex) {
            exn = ex;
        }
    }

    public IOException getException()
    {
        return exn;
    }

    @Override
    public String toString()
    {
        return String.format("Enc@%x", hashCode());
    }
}
