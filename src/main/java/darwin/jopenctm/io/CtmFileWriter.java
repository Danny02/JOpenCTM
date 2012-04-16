/*
 * Copyright (C) 2012 daniel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.jopenctm.io;

import darwin.jopenctm.data.Mesh;
import java.io.IOException;
import java.io.OutputStream;

import darwin.jopenctm.compression.MeshEncoder;
import darwin.jopenctm.errorhandling.InvalidDataException;

import static darwin.jopenctm.io.CtmFileReader.*;

/**
 *
 * @author daniel
 */
public class CtmFileWriter
{

    private final CtmOutputStream out;
    private final MeshEncoder encoder;

    public CtmFileWriter(OutputStream o, MeshEncoder e)
    {
        out = new CtmOutputStream(o);
        encoder = e;
    }

    public void encode(Mesh m, String comment) throws IOException, InvalidDataException
    {
        // Check mesh integrity
        m.checkIntegrity();

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
