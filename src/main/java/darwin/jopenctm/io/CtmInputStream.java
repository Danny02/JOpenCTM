/*
 * Copyright (C) 2012 Daniel Heinrich
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * (version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301  USA.
 */
package darwin.jopenctm.io;

import java.io.*;
import java.util.Arrays;
import lzma.sdk.lzma.Decoder;
import org.cservenak.streams.Coder;

/**
 *
 * @author daniel
 */
public class CtmInputStream extends DataInputStream {

    public CtmInputStream(InputStream in) {
        super(in);
    }

    public String readString() throws IOException {
        int len = readLittleInt();
        if (len > 0) {
            byte[] values = new byte[len];
            if (read(values) == -1) {
                throw new IOException("End of file reached while parsing the file!");
            }
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
    public int readLittleInt() throws IOException {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch1 + (ch2 << 8) + (ch3 << 16) + (ch4 << 24));
    }

    public int[] readLittleIntArray(int count) throws IOException {
        int[] array = new int[count];
        for (int i = 0; i < count; i++) {
            array[i] = readLittleInt();
        }
        return array;
    }

    /**
     * Reads floating point type stored in little endian (see readFloat() for
     * big endian)
     * <p/>
     * @return float value translated from little endian
     * <p/>
     * @throws IOException if an IO error occurs
     */
    public final float readLittleFloat() throws IOException {
        return Float.intBitsToFloat(readLittleInt());
    }

    public float[] readLittleFloatArray(int count) throws IOException {
        float[] array = new float[count];
        for (int i = 0; i < count; i++) {
            array[i] = readLittleFloat();
        }
        return array;
    }

    public int[] readPackedInts(int count, int size, boolean signed) throws IOException {
        int[] data = new int[count * size];
        byte[] tmp = readCompressedData(count * size * 4);//a Integer is 4 bytes
        // Convert interleaved array to integers
        for (int i = 0; i < count; ++i) {
            for (int k = 0; k < size; ++k) {
                int value = interleavedRetrive(tmp, i + k * count, count * size);
                if (signed) {
                    long x = ((long) value) & 0xFFFFFFFFL;//not sure if correct
                    value = (x & 1) != 0 ? -(int) ((x + 1) >> 1) : (int) (x >> 1);
                }
                data[i * size + k] = value;
            }
        }
        return data;
    }

    public float[] readPackedFloats(int count, int size) throws IOException {
        float[] data = new float[count * size];
        byte[] tmp = readCompressedData(count * size * 4);//a Float is 4 bytes
        // Convert interleaved array to floats
        for (int i = 0; i < count; ++i) {
            for (int k = 0; k < size; ++k) {
                int value = interleavedRetrive(tmp, i + k * count, count * size);
                data[i * size + k] = Float.intBitsToFloat(value);
            }
        }

        return data;
    }

    public byte[] readCompressedData(int size) throws IOException {
        int packedSize = readLittleInt();
        byte[] packed = new byte[packedSize + 5];//lzma properties are 5 bytes big
        if (read(packed) == -1) {
            throw new IOException("End of file reached while reading!");
        }
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream(size);
        
        CustomCoder coder = new CustomCoder(size);
        coder.code(new ByteArrayInputStream(packed), bout);
                
        byte[] data = bout.toByteArray();
        assert data.length == size;
        
        return data;
    }

    public static int interleavedRetrive(byte[] data, int offset, int stride) {
        byte b1 = data[offset + 3 * stride];
        byte b2 = data[offset + 2 * stride];
        byte b3 = data[offset + 1 * stride];
        byte b4 = data[offset];

        int i1 = ((int) b1) & 0xff;
        int i2 = ((int) b2) & 0xff;
        int i3 = ((int) b3) & 0xff;
        int i4 = ((int) b4) & 0xff;

        return i1 | (i2 << 8) | (i3 << 16) | (i4 << 24);
    }

    private static class CustomCoder implements Coder {

        private final Decoder d;
        private final int len;

        public CustomCoder(int packedLength) {
            d = new Decoder();
            len = packedLength;
        }

        @Override
        public void code(final InputStream in, final OutputStream out)
                throws IOException {

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
