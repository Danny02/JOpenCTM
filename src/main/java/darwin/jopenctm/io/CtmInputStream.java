/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.io;

import java.io.*;

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
     * <p/>
     * @return < p/> @throws IOException
     */
    public int readLittleInt() throws IOException
    {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch1 + (ch2 << 8) + (ch3 << 16) + (ch4 << 24));
    }

    /**
     * Reads floating point type stored in little endian (see readFloat() for
     * big endian)
     * <p/>
     * @return float value translated from little endian
     * <p/>
     * @throws IOException if an IO error occurs
     */
    public final float readLittleFloat() throws IOException
    {
        return Float.intBitsToFloat(readLittleInt());
    }

    public int[] readPackedInts(int count, int size, boolean signed) throws IOException
    {
        int[] data = new int[count * size];
        byte[] tmp = readPackedData(count * size * 4);
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

    public float[] readPackedFloats(int count, int size) throws IOException
    {
        float[] data = new float[count * size];
        byte[] tmp = readPackedData(count * size * 4);
        // Convert interleaved array to floats
        for (int i = 0; i < count; ++i) {
            for (int k = 0; k < size; ++k) {
                int value = interleavedRetrive(tmp, i, k, count, size);
                data[i * size + k] = Float.intBitsToFloat(value);
            }
        }

        return data;
    }

    private byte[] readPackedData(int size) throws IOException
    {
        byte[] packed = new byte[readLittleInt() + 5];//lzma properties are 5 bytes big
        read(packed);

        byte[] tmp = new byte[size]; //a Float is 4 bytes
        new PackedInputStream(packed).read(tmp);
        return tmp;
    }

    private int interleavedRetrive(byte[] data, int x, int y, int width, int height)
    {
        return (int) data[x + y * width + 3 * width * height]
                | (((int) data[x + y * width + 2 * width * height]) << 8)
                | (((int) data[x + y * width + 1 * width * height]) << 16)
                | (((int) data[x + y * width]) << 24);
    }
}
