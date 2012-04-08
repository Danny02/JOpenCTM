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

    public static final int OCTM = getTagInt("OCTM");
    public static final int CTM_ATTR_ELEMENT_COUNT = 4;
    public static final int CTM_NORMAL_ELEMENT_COUNT = 3;
    public static final int CTM_POSITION_ELEMENT_COUNT = 3;
    public static final int CTM_UV_ELEMENT_COUNT = 2;
    private Mesh mesh;
    private String comment;
    private final CtmInputStream in;

    public CtmFileReader(InputStream source)
    {
        in = new CtmInputStream(source);
    }

    public Mesh decode() throws IOException
    {
        if (in.readLittleInt() != OCTM) {
            throw new IOException("Bad format: the CTM file doesn't start with the OCTM tag!");
        }
        int formatVersion = in.readLittleInt();
        int methodTag = in.readLittleInt();

        MeshInfo mi = new MeshInfo(in.readLittleInt(),//vertex count
                in.readLittleInt(), //triangle count
                in.readLittleInt(), //uvmap count
                in.readLittleInt(), //attribute count
                in.readLittleInt());                  //flags

        comment = in.readString();

        // Uncompress from stream
        Mesh m = null;
        ServiceLoader<MeshDecoder> services = ServiceLoader.load(MeshDecoder.class);
        boolean tagSup = false, verSup = false;
        for (MeshDecoder md : services) {
            tagSup |= md.getTag() == methodTag;
            if (md.getTag() == methodTag) {
                if (verSup = md.isFormatSupported(formatVersion)) {
                    m = md.decode(mi, in);
                    break;
                }
            }
        }

        if (!tagSup) {
            throw new IOException("No sutible decoder found for Mesh of compression type: " + unpack(methodTag));

        } else if (!verSup) {
            throw new IOException("No sutible decoder found for Mesh of compression type: " + unpack(methodTag) + ", version " + formatVersion);
        }

        // Check mesh integrity
        if (!m.checkIntegrity()) {
            throw new IOException("The integrity check of the mesh failed");
        }
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

    public String getFileComment() throws IOException
    {
        if (mesh == null) {
            mesh = decode();
        }
        return comment;
    }

    public Mesh getMesh() throws IOException
    {
        //TODO was wenn das decoden fehl schlaegt oder schonmal faehl geschlagen sit
        if (mesh == null) {
            mesh = decode();
        }
        return mesh;
    }

    public static int getTagInt(String tag)
    {
        char[] chars = tag.toCharArray();
        assert chars.length == 4 : "A tag has to be constructed out of 4 characters!";
        return chars[0] | (chars[1] << 8) | (chars[2] << 16) | (chars[3] << 24);
    }
}
