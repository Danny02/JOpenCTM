/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm;

import java.io.*;
import lzma.streams.LzmaOutputStream;
import lzma.streams.LzmaOutputStream.Builder;

/**
 *
 * @author daniel
 */
public class CtmOutputStream extends DataOutputStream
{

    public CtmOutputStream(OutputStream out)
    {
        super(out);
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
        writeInt(len);
        write(text.getBytes());
    }

    void writePackedInts(int[] data, int count, int size, boolean signed) throws IOException
    {
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

    void writePackedFloats(float[] data, int count, int size) throws IOException
    {
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
        data[x + y * width + 3 * width * height] = (byte) (value & 0x000000ff);
        data[x + y * width + 2 * width * height] = (byte) ((value >> 8) & 0x000000ff);
        data[x + y * width + width * height] = (byte) ((value >> 16) & 0x000000ff);
        data[x + y * width] = (byte) ((value >> 24) & 0x000000ff);
    }

    private void writeCompressed(byte[] data) throws IOException
    {
        Builder b = new Builder(this).useBT4MatchFinder().
                useEndMarkerMode(false).
                useMaximalFastBytes().
                useMaximalDictionarySize();

        LzmaOutputStream lzout = b.build();
        lzout.write(data);
    }
}
