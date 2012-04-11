/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
