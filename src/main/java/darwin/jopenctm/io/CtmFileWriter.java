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

import darwin.jopenctm.compression.MeshEncoder;
import darwin.jopenctm.data.Mesh;
import darwin.jopenctm.errorhandling.InvalidDataException;

import static darwin.jopenctm.io.CtmFileReader.OCTM;

/**
 *
 * @author daniel
 */
public class CtmFileWriter {

    private final CtmOutputStream out;
    private final MeshEncoder encoder;

    public CtmFileWriter(OutputStream o, MeshEncoder e) {
        out = new CtmOutputStream(o);
        encoder = e;
    }

    public CtmFileWriter(OutputStream o, MeshEncoder e, int compressionLevel) {
        out = new CtmOutputStream(compressionLevel, o);
        encoder = e;
    }

    public void encode(Mesh m, String comment) throws IOException, InvalidDataException {
        // Determine flags
        int flags = 0;
        if (m.normals != null) {
            flags |= MeshInfo.HAS_NORMAL_BIT;
        }

        // Write header to stream
        out.writeLittleInt(OCTM);
        out.writeLittleInt(encoder.getFormatVersion());
        out.writeLittleInt(encoder.getTag());

        out.writeLittleInt(m.getVertexCount());
        out.writeLittleInt(m.getTriangleCount());
        out.writeLittleInt(m.getUVCount());
        out.writeLittleInt(m.getAttrCount());
        out.writeLittleInt(flags);
        out.writeString(comment);

        // Compress to stream
        encoder.encode(m, out);
    }
}
