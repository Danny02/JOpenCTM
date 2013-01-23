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
import java.util.ServiceLoader;

import darwin.jopenctm.compression.MeshDecoder;
import darwin.jopenctm.data.Mesh;
import darwin.jopenctm.errorhandling.*;

/**
 *
 * @author daniel
 */
public class CtmFileReader
{

    public static final int OCTM = getTagInt("OCTM");
    private Mesh mesh;
    private String comment;
    private final CtmInputStream in;
    private boolean decoded;

    public CtmFileReader(InputStream source)
    {
        in = new CtmInputStream(source);
    }

    public Mesh decode() throws IOException, BadFormatException, InvalidDataException
    {
        if (decoded) {
            throw new RuntimeException("Ctm File got already decoded");
        }
        decoded = true;

        if (in.readLittleInt() != OCTM) {
            throw new BadFormatException("The CTM file doesn't start with the OCTM tag!");
        }

        final int formatVersion = in.readLittleInt();
        final int methodTag = in.readLittleInt();

        final MeshInfo mi = new MeshInfo(in.readLittleInt(),//vertex count
                in.readLittleInt(), //triangle count
                in.readLittleInt(), //uvmap count
                in.readLittleInt(), //attribute count
                in.readLittleInt());                  //flags

        comment = in.readString();

        // Uncompress from stream
        Mesh m = null;
        ServiceLoader<MeshDecoder> services = ServiceLoader.load(MeshDecoder.class);
        for (MeshDecoder md : services) {
            if (md.isFormatSupported(methodTag, formatVersion)) {
                m = md.decode(mi, in);
                break;
            }
        }

        if (m == null) {
            throw new IOException("No sutible decoder found for Mesh of compression type: " + unpack(methodTag) + ", version " + formatVersion);
        }

        // Check mesh integrity
        m.checkIntegrity();

        return m;
    }

    public static String unpack(int tag)
    {
        byte[] chars = new byte[4];
        chars[0] = (byte) (tag & 0xff);
        chars[1] = (byte) ((tag >> 8) & 0xff);
        chars[2] = (byte) ((tag >> 16) & 0xff);
        chars[3] = (byte) ((tag >> 24) & 0xff);
        return new String(chars);
    }

    /**
     * before calling this method the first time, the decode method has to be
     * called.
     * <p/>
     * @throws RuntimeExceptio- if the file wasn't decoded before.
     */
    public String getFileComment()
    {
        if (!decoded) {
            throw new RuntimeException("The CTM file is not decoded yet.");
        }
        return comment;
    }

    public static int getTagInt(String tag)
    {
        char[] chars = tag.toCharArray();
        assert chars.length == 4 : "A tag has to be constructed out of 4 characters!";
        return chars[0] | (chars[1] << 8) | (chars[2] << 16) | (chars[3] << 24);
    }
}
