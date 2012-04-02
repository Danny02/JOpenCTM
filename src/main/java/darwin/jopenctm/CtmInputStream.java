/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm;

import java.io.*;
import lzma.sdk.lzma.Decoder;
import lzma.streams.LzmaInputStream;

/**
 *
 * @author daniel
 */
public class CtmInputStream extends DataInputStream
{

    public CtmInputStream(InputStream in)
    {
        super(in);
    }

    public String readString() throws IOException
    {
        int len = readInt();
        if (len > 0) {
            byte[] values = new byte[len];
            read(values);
            return new String(values);
        } else {
            return "";
        }
    }

    public int[] readPackedInts(int[] data, int count, int size, boolean signed) throws IOException
    {
        assert data.length >= size * count;
        // Read packed data size from the stream
        readInt();

        byte[] tmp = new byte[count * size * 4];

        LzmaInputStream lzin = new LzmaInputStream(this, new Decoder());
        lzin.read(tmp);
        // Convert interleaved array to integers
        for (int i = 0; i < count; ++i) {
            for (int k = 0; k < size; ++k) {
                int value = interleavedRetrive(tmp, i, k, count, size);
                if (signed) {
                    long x = ((long) value) & 0xFFFFFFFFL;//not sure if correct
                    value = (x & 1) != 0 ? -(int) ((x + 1) >> 1) : (int) (x >> 1);
                }
                data[i * size + k] = value;
            }
        }
        return data;
    }

    public float[] readPackedFloats(float[] data, int count, int size) throws IOException
    {
        assert data.length >= size * count;
        // Read packed data size from the stream
        readInt();

        // Allocate memory for interleaved array
        byte[] tmp = new byte[count * size * 4];

        LzmaInputStream lzin = new LzmaInputStream(this, new Decoder());
        lzin.read(tmp);

        // Convert interleaved array to floats
        for (int i = 0; i < count; ++i) {
            for (int k = 0; k < size; ++k) {
                int value = interleavedRetrive(tmp, i, k, count, size);
                data[i * size + k] = Float.intBitsToFloat(value);
            }
        }

        return data;
    }

    private int interleavedRetrive(byte[] data, int x, int y, int width, int height)
    {
        return (int) data[x + y * width + 3 * width * height]
                | (((int) data[x + y * width + 2 * width * height]) << 8)
                | (((int) data[x + y * width + 1 * width * height]) << 16)
                | (((int) data[x + y * width]) << 24);
    }
}
