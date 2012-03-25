/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm;

import java.io.IOException;
import java.io.OutputStream;

import darwin.jopenctm.compression.MeshEncoder;

import static darwin.jopenctm.CtmFileReader.*;

/**
 *
 * @author daniel
 */
public class CtmFileWriter
{

    public final int HAS_NORMAL_BIT = 1;
    private final CtmOutputStream out;
    private final MeshEncoder encoder;

    public CtmFileWriter(OutputStream o, MeshEncoder e)
    {
        out = new CtmOutputStream(o);
        encoder = e;
    }

    public void encode(Mesh m, String comment) throws IOException
    {
        // Check mesh integrity
        if (!m.checkIntegrity()) {
            throw new IOException("The integrity check of the mesh failed");
        }

        // Determine flags
        int flags = 0;
        if (m.normals != null) {
            flags |= HAS_NORMAL_BIT;
        }

        // Write header to stream
        out.writeInt(OCTM);
        out.writeInt(FORMAT_VERSION);
        out.writeInt(encoder.getTag());

        out.writeInt(m.getVertexCount());
        out.writeInt(m.getTriangleCount());
        out.writeInt(m.getUVCount());
        out.writeInt(m.getAttrCount());
        out.writeInt(flags);
        out.writeString(comment);

        // Compress to stream
        encoder.encode(m, out);
    }
}
