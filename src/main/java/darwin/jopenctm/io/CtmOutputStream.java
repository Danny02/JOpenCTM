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

import darwin.jlzmaio.LzmaOutputStream;
import java.io.*;

/**
 *
 * @author daniel
 */
public class CtmOutputStream extends DataOutputStream {

    private final int compressionLevel;

    public CtmOutputStream(OutputStream out) {
        this(5, out);
    }

    public CtmOutputStream(int compressionLevel, OutputStream out) {
        super(out);
        this.compressionLevel = compressionLevel;
    }

    public void writeString(String text) throws IOException {
        if (text != null) {
            writeLittleInt(text.length());
            write(text.getBytes());
        } else {
            writeLittleInt(0);
        }
    }

    public void writeLittleInt(int v) throws IOException {
        out.write(v & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 24) & 0xFF);
    }

    public void writeLittleIntArray(int[] v) throws IOException {
        for (int a : v) {
            writeLittleInt(a);
        }
    }

    public void writeLittleFloat(float v) throws IOException {
        writeLittleInt(Float.floatToIntBits(v));
    }

    public void writeLittleFloatArray(float[] v) throws IOException {
        for (float a : v) {
            writeLittleFloat(a);
        }
    }

    public void writePackedInts(int[] data, int count, int size, boolean signed) throws IOException {
        assert data.length >= count * size : "The data to be written is smaller"
                + " as stated by other parameters. Needed: " + (count * size) + " Provided: " + data.length;
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
                interleavedInsert(value, tmp, i + k * count, count * size);
            }
        }

        writeCompressedData(tmp);
    }

    public void writePackedFloats(float[] data, int count, int size) throws IOException {
        assert data.length >= count * size : "The data to be written is smaller"
                + " as stated by other parameters. Needed: " + (count * size) + " Provided: " + data.length;
        // Allocate memory for interleaved array
        byte[] tmp = new byte[count * size * 4];

        // Convert floats to an interleaved array
        for (int x = 0; x < count; ++x) {
            for (int y = 0; y < size; ++y) {
                int value = Float.floatToIntBits(data[x * size + y]);
                interleavedInsert(value, tmp, x + y * count, count * size);
            }
        }
        writeCompressedData(tmp);
    }

    public static void interleavedInsert(int value, byte[] data, int offset, int stride) {
        data[offset + 3 * stride] = (byte) (value & 0xff);
        data[offset + 2 * stride] = (byte) ((value >> 8) & 0xff);
        data[offset + stride] = (byte) ((value >> 16) & 0xff);
        data[offset] = (byte) ((value >> 24) & 0xff);
    }

    public void writeCompressedData(byte[] data) throws IOException {
        //some magic size as in the OpenCTM reference implementation
        ByteArrayOutputStream bout = new ByteArrayOutputStream(1000 + data.length);

//        Encoder enc = new Encoder();
//        enc.setEndMarkerMode(true);
//        if (compressionLevel <= 5) {
//            enc.setDictionarySize(1 << (compressionLevel * 2 + 14));
//        } else if (compressionLevel == 6) {
//            enc.setDictionarySize(1 << 25);
//        } else {
//            enc.setDictionarySize(1 << 26);
//        }
//        enc.setNumFastBytes(compressionLevel < 7 ? 32 : 64);

//        try (OutputStream lzout = new LzmaOutputStream(bout, new CustomWrapper(enc))) {
        try (OutputStream lzout = new LzmaOutputStream(bout)) {
            lzout.write(data);
        }

        //This is the custom way of OpenCTM to write the LZMA properties
        this.writeLittleInt(bout.size());
//        enc.writeCoderProperties(this);
        bout.writeTo(this);
//        write(data);
    }

//    private static class CustomWrapper extends LzmaEncoderWrapper {
//
//        private final Encoder e;
//
//        CustomWrapper(Encoder encoder) {
//            super(encoder);
//            e = encoder;
//        }
//
//        @Override
//        public void code(InputStream in, OutputStream out) throws IOException {
//            //both int attributs aren't used inside the method
//            e.code(in, out, -1, -1, null);
//        }
//    }
}
