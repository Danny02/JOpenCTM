/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.compression;

import java.io.IOException;

import darwin.annotations.ServiceProvider;
import darwin.jopenctm.*;

import static darwin.jopenctm.compression.MeshDecoder.*;

/**
 *
 * @author daniel
 */
@ServiceProvider(MeshEncoder.class)
public class RawEncoder implements MeshEncoder
{

    @Override
    public void encode(Mesh m, CtmOutputStream out) throws IOException
    {
        int vc = m.getVertexCount();

        out.writeInt(INDX);
        writeIndicies(m.indices, out);

        out.writeInt(VERT);
        writeFloatArray(m.vertices, out, vc * 3, 1);

        // Write normals
        if (m.normals != null) {
            out.writeInt(NORM);
            writeFloatArray(m.normals, out, vc, 3);
        }

        for (AttributeData ad : m.texcoordinates) {
            out.writeInt(TEXC);
            out.writeString(ad.name);
            out.writeString(ad.materialName);
            writeFloatArray(ad.values, out, vc, 2);
        }

        for (AttributeData ad : m.attributs) {
            out.writeInt(ATTR);
            out.writeString(ad.name);
            writeFloatArray(ad.values, out, vc, 4);
        }
    }

    protected void writeIndicies(int[] indices, CtmOutputStream out) throws IOException
    {
        for (int i : indices) {
            out.writeInt(i);
        }
    }

    protected void writeFloatArray(float[] array, CtmOutputStream out, int count, int size) throws IOException
    {
        for (float v : array) {
            out.writeFloat(v);
        }
    }

    @Override
    public int getTag()
    {
        return RawDecoder.RAW_TAG;
    }

    @Override
    public int getFormatVersion()
    {
        return RawDecoder.FORMAT_VERSION;
    }
}
