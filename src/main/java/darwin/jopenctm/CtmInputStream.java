/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm;

import java.io.*;
import org.jlzmaio.LzmaInputStream;

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
        int len = readLittleInt();
        if (len > 0) {
            byte[] values = new byte[len];
            read(values);
            return new String(values);
        } else {
            return "";
        }
    }

    /**
    * reads a single Integer value, in little edian order
    * @return
    * @throws IOException
    */
    public int readLittleInt() throws IOException
    {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return (ch1 + (ch2 << 8) + (ch3 << 16) + (ch4 << 24));
    }

    /**
     * Reads floating point type stored in little endian (see readFloat() for big endian)
     * @return float value translated from little endian
     * @throws IOException if an IO error occurs
     */
    public final float readLittleFloat() throws IOException {
        return Float.intBitsToFloat(readLittleInt());
    }

    public int[] readPackedInts(int[] data, int count, int size, boolean signed) throws IOException
    {
        assert data.length >= size * count;
        // Read packed data size from the stream
        readInt();

        byte[] tmp = new byte[count * size * 4];

//        LzmaInputStream lzin = new LzmaInputStream(this, new Decoder());
        LzmaInputStream lzin = new LzmaInputStream(in);
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

//        LzmaInputStream lzin = new LzmaInputStream(this, new Decoder());
        LzmaInputStream lzin = new LzmaInputStream(in);
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
