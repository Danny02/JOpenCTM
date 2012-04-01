/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.compression;

import java.io.IOException;

import darwin.annotations.ServiceProvider;
import darwin.jopenctm.*;

import static darwin.jopenctm.CtmFileReader.*;
import static darwin.jopenctm.compression.MeshDecoder.*;

/**
 *
 * @author daniel
 */
@ServiceProvider(MeshDecoder.class)
public class RawDecoder implements MeshDecoder
{

    public static final int RAW_TAG = getTagInt("RAW\0");
    public static final int FORMAT_VERSION = 5;

    @Override
    public Mesh decode(MeshInfo minfo, CtmInputStream in) throws IOException
    {
        int vc = minfo.getVertexCount();
        float[] vertices = new float[vc * CTM_POSITION_ELEMENT_COUNT];
        float[] normals = null;
        if ((minfo.getFlags() & HAS_NORMAL_BIT) > 0) {
            normals = new float[vc];
        }
        int[] indices = new int[minfo.getTriangleCount() * 3];

        AttributeData[] tex = new AttributeData[minfo.getUvMapCount()];
        AttributeData[] att = new AttributeData[minfo.getAttrCount()];

        checkTag(in.readInt(), INDX);
        for (int i = 0; i < indices.length; ++i) {
            indices[i] = in.readInt();
        }

        checkTag(in.readInt(), VERT);
        readFloat(vertices, in);

        checkTag(in.readInt(), NORM);
        if (normals == null) {
            //TODO bad format warning, the normal flag wasn't set
            normals = new float[vc * CTM_NORMAL_ELEMENT_COUNT];
        }
        readFloat(normals, in);

        for (int i = 0; i < tex.length; ++i) {
            checkTag(in.readInt(), TEXC);
            tex[i] = readUVData(vc, in);
        }

        for (int i = 0; i < att.length; ++i) {
            checkTag(in.readInt(), ATTR);
            att[i] = readAttrData(vc, in);
        }

        return new Mesh(vertices, normals, indices, tex, att);
    }

    private void checkTag(int readTag, int expectedTag) throws IOException
    {
        if (readTag != expectedTag) {
            throw new IOException("Instead of the expected data tag(\"" + unpack(expectedTag)
                    + "\") the tag(\"" + unpack(readTag) + "\") was read!");
        }
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

    private static void readFloat(float[] array, CtmInputStream in) throws IOException
    {
        for (int i = 0; i < array.length; i++) {
            array[i] = in.readFloat();
        }
    }

    private static AttributeData readUVData(int vertCount, CtmInputStream in) throws IOException
    {
        String name = in.readString();
        String matname = in.readString();
        float[] data = new float[vertCount * CTM_UV_ELEMENT_COUNT];
        readFloat(data, in);

        return new AttributeData(name, matname, AttributeData.STANDART_UV_PRECISION, data);
    }

    private static AttributeData readAttrData(int vertCount, CtmInputStream in) throws IOException
    {
        String name = in.readString();
        float[] data = new float[vertCount * CTM_ATTR_ELEMENT_COUNT];
        readFloat(data, in);

        return new AttributeData(name, null, AttributeData.STANDART_PRECISION, data);
    }

    @Override
    public int getTag()
    {
        return RAW_TAG;
    }

    @Override
    public boolean isFormatSupported(int version)
    {
        return version == FORMAT_VERSION;
    }
}
