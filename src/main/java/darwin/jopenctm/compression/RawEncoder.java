/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.compression;

import java.io.IOException;

import darwin.jopenctm.*;
import darwin.annotations.ServiceProvider;

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
        out.writeInt(INDX);
        for (int i : m.indices) {
            out.writeInt(i);
        }

        out.writeInt(VERT);
        writeArray(m.vertices, out);

        // Write normals
        if (m.normals != null) {
            out.writeInt(NORM);
            writeArray(m.normals, out);
        }

        for (AttributeData ad : m.texcoordinates) {
            out.writeInt(TEXC);
            out.writeString(ad.name);
            out.writeString(ad.materialName);
            writeArray(ad.values, out);
        }

        for (AttributeData ad : m.attributs) {
            out.writeInt(ATTR);
            out.writeString(ad.name);
            writeArray(ad.values, out);
        }
    }

    private static void writeArray(float[] array, CtmOutputStream out) throws IOException
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
