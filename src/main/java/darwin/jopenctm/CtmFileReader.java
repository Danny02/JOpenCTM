/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ServiceLoader;

import darwin.jopenctm.compression.MeshDecoder;

/**
 *
 * @author daniel
 */
public class CtmFileReader
{

    public static final int OCTM = 'O' | ('C' << 8) | ('T' << 16) | ('M' << 24);
    public static final int FORMAT_VERSION = 5;
    private Mesh mesh;
    private String comment;
    private boolean decoded;
    private final CtmInputStream in;

    public CtmFileReader(InputStream source)
    {
        in = new CtmInputStream(source);
    }

    public void decode() throws IOException
    {
        if (in.readInt() != OCTM) {
            throw new IOException("Bad format: the CTM file doesn't start with the OCTM tag!");
        }
        int formatVersion = in.readInt();
        if (formatVersion != FORMAT_VERSION) {
            throw new IOException("Unsupported format version(" + formatVersion + "). Only version " + FORMAT_VERSION + " supported!");
        }
        int methodTag = in.readInt();

        int vertexCount = in.readInt();

        int triangleCount = in.readInt();

        int UVMapCount = in.readInt();
        int attribMapCount = in.readInt();
        int flags = in.readInt();
        comment = in.readString();

        // Uncompress from stream
        Mesh m = null;
        ServiceLoader<MeshDecoder> services = ServiceLoader.load(MeshDecoder.class);
        for (MeshDecoder md : services) {
            if (md.getTag() == methodTag) {
                m = md.decode(triangleCount, vertexCount, in);
                break;
            }
        }

        if (m == null) {
            throw new IOException("No sutible decoder found for Mesh of compression type: " + unpack(methodTag));
        }

        // Check mesh integrity
        if (!m.checkIntegrity()) {
            throw new IOException("The integrity check of the mesh failed");
        }
        decoded = true;
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

    public String getFileComment() throws IOException
    {
        if (!decoded) {
            decode();
        }
        return comment;
    }

    public Mesh getMesh() throws IOException
    {
        if (!decoded) {
            decode();
        }
        return mesh;
    }
}
