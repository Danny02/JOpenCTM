/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.io;

import java.io.*;
import lzma.sdk.lzma.Encoder;
import lzma.streams.LzmaEncoderWrapper;
import lzma.streams.LzmaOutputStream;

/**
 *
 * @author daniel
 */
public class CtmOutputStream extends DataOutputStream
{

    private final int compressionLevel;

    public CtmOutputStream(OutputStream out)
    {
        this(5, out);
    }

    public CtmOutputStream(int compressionLevel, OutputStream out)
    {
        super(out);
        this.compressionLevel = compressionLevel;
    }

    public void writeString(String text) throws IOException
    {
        int len;

        // Get string length
        if (text != null) {
            len = text.length();
        } else {
            len = 0;
        }

        // Write string length
        writeLittleInt(len);
        write(text.getBytes());
    }

    public void writeLittleInt(int v) throws IOException
    {
        out.write(v & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 24) & 0xFF);
    }

    public void writeLittleFloat(float v) throws IOException
    {
        writeLittleInt(Float.floatToIntBits(v));
    }

    public void writePackedInts(int[] data, int count, int size, boolean signed) throws IOException
    {
        assert data.length >= count * size: "The data to be written is smaller"
                + " as stated by other parameters. Needed: "+(count*size)+" Provided: "+data.length;
        // Allocate memory for interleaved array
        byte[] tmp = new byte[count * size * 4];

        // Convert integers to an interleaved array
        for (int i = 0; i < count; ++i) {
            for (int k = 0; k < size; ++k) {
                int value = data[i * size + k];
                // Convert two's complement to signed magnitude?
                if (signed) {
                    value = value < 0 ? -1 - (value << 1) : value << 1;
                }
                interleavedInsert(value, tmp, i, k, count, size);
            }
        }

        writeCompressed(tmp);
    }

    public void writePackedFloats(float[] data, int count, int size) throws IOException
    {
        assert data.length >= count * size: "The data to be written is smaller"
                + " as stated by other parameters. Needed: "+(count*size)+" Provided: "+data.length;
        // Allocate memory for interleaved array
        byte[] tmp = new byte[count * size * 4];

        // Convert floats to an interleaved array
        for (int i = 0; i < count; ++i) {
            for (int k = 0; k < size; ++k) {
                int value = Float.floatToIntBits(data[i * size + k]);
                interleavedInsert(value, tmp, i, k, count, size);
            }
        }

        writeCompressed(tmp);
    }

    private void interleavedInsert(int value, byte[] data, int x, int y, int width, int height)
    {
        data[x + y * width + 3 * width * height] = (byte) (value & 0xff);
        data[x + y * width + 2 * width * height] = (byte) ((value >> 8) & 0xff);
        data[x + y * width + width * height] = (byte) ((value >> 16) & 0xff);
        data[x + y * width] = (byte) ((value >> 24) & 0xff);
    }

    private void writeCompressed(byte[] data) throws IOException
    {
        //some magic size as in the OpenCTM reference implementation
        ByteArrayOutputStream bout = new ByteArrayOutputStream(1000 + data.length);

        Encoder enc = new Encoder();
        enc.setEndMarkerMode(true);
        if (compressionLevel <= 5) {
            enc.setDictionarySize(1 << (compressionLevel * 2 + 14));
        } else if (compressionLevel == 6) {
            enc.setDictionarySize(1 << 25);
        } else {
            enc.setDictionarySize(1 << 26);
        }
        enc.setNumFastBytes(compressionLevel < 7 ? 32:64);

        try (LzmaOutputStream lzout = new LzmaOutputStream(bout, new CustomWrapper(enc))) {
            lzout.write(data);
            lzout.flush();
        }

        //This is the custom way of OpenCTM to write the LZMA properties
        this.writeLittleInt(bout.size());
        enc.writeCoderProperties(this);
        bout.writeTo(this);
    }

    private static class CustomWrapper extends LzmaEncoderWrapper
    {

        private final Encoder e;

        public CustomWrapper(Encoder encoder)
        {
            super(encoder);
            e = encoder;
        }

        @Override
        public void code(InputStream in, OutputStream out) throws IOException
        {
            //both integer attributs aren't used inside the method
            e.code(in, out, -1, -1, null);
        }
    }
}
