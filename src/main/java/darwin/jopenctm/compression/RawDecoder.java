/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.compression;

import darwin.jopenctm.data.AttributeData;
import darwin.jopenctm.io.MeshInfo;
import darwin.jopenctm.io.CtmInputStream;
import java.io.IOException;

import darwin.annotations.ServiceProvider;
import darwin.jopenctm.data.Mesh;
import darwin.jopenctm.errorhandling.BadFormatException;
import darwin.jopenctm.errorhandling.InvalidDataException;

import static darwin.jopenctm.io.CtmFileReader.*;
import static darwin.jopenctm.data.Mesh.*;

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
    public Mesh decode(MeshInfo minfo, CtmInputStream in) throws IOException, BadFormatException, InvalidDataException
    {
        int vc = minfo.getVertexCount();

        AttributeData[] tex = new AttributeData[minfo.getUvMapCount()];
        AttributeData[] att = new AttributeData[minfo.getAttrCount()];

        checkTag(in.readLittleInt(), INDX);
        int[] indices = readIntArray(in, minfo.getTriangleCount(), 3, false);

        checkTag(in.readLittleInt(), VERT);
        float[] vertices = readFloatArray(in, vc * CTM_POSITION_ELEMENT_COUNT, 1);

        float[] normals = null;
        if (minfo.hasNormals()) {
            checkTag(in.readLittleInt(), NORM);
            normals = readFloatArray(in, vc, CTM_NORMAL_ELEMENT_COUNT);
        }

        for (int i = 0; i < tex.length; ++i) {
            checkTag(in.readLittleInt(), TEXC);
            tex[i] = readUVData(vc, in);
        }

        for (int i = 0; i < att.length; ++i) {
            checkTag(in.readLittleInt(), ATTR);
            att[i] = readAttrData(vc, in);
        }

        return new Mesh(vertices, normals, indices, tex, att);
    }

    protected void checkTag(int readTag, int expectedTag) throws BadFormatException
    {
        if (readTag != expectedTag) {
            throw new BadFormatException("Instead of the expected data tag(\"" + unpack(expectedTag)
                    + "\") the tag(\"" + unpack(readTag) + "\") was read!");
        }
    }

    protected int[] readIntArray(CtmInputStream in, int count, int size, boolean signed) throws IOException
    {
        int[] array = new int[count * size];
        for (int i = 0; i < array.length; i++) {
            array[i] = in.readLittleInt();
        }
        return array;
    }

    protected float[] readFloatArray(CtmInputStream in, int count, int size) throws IOException
    {
        float[] array = new float[count * size];
        for (int i = 0; i < array.length; i++) {
            array[i] = in.readLittleFloat();
        }
        return array;
    }

    private AttributeData readUVData(int vertCount, CtmInputStream in) throws IOException
    {
        String name = in.readString();
        String matname = in.readString();
        float[] data = readFloatArray(in, vertCount, CTM_UV_ELEMENT_COUNT);

        return new AttributeData(name, matname, AttributeData.STANDART_UV_PRECISION, data);
    }

    private AttributeData readAttrData(int vertCount, CtmInputStream in) throws IOException
    {
        String name = in.readString();
        float[] data = readFloatArray(in, vertCount, CTM_ATTR_ELEMENT_COUNT);

        return new AttributeData(name, null, AttributeData.STANDART_PRECISION, data);
    }

    @Override
    public boolean isFormatSupported(int tag, int version)
    {
        return tag == RAW_TAG && version == FORMAT_VERSION;
    }
}
