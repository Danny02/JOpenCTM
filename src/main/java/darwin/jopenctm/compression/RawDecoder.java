/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.compression;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import darwin.jopenctm.*;
import darwin.tools.annotations.ServiceProvider;

import static darwin.jopenctm.compression.MeshDecoder.*;

/**
 *
 * @author daniel
 */
@ServiceProvider(MeshDecoder.class)
public class RawDecoder implements MeshDecoder
{
    public static final int RAW = 'R' | ('A' << 8) | ('W' << 16) | ('\0' << 24);

    @Override
    public Mesh decode(int triangleCount, int vertexCount, CtmInputStream in) throws IOException
    {
        float[] vertices = null, normals = null;
        int[] indices = null;
        List<AttributeData> tex = new LinkedList<>();
        List<AttributeData> att = new LinkedList<>();

        while (true) {
            int tag;
            try {
                tag = in.readInt();
            } catch (IOException ex) {
                break;
            }
            switch (tag) {
                case INDX:
                    indices = new int[triangleCount * 3];
                    for (int i = 0; i < indices.length; ++i) {
                        indices[i] = in.readInt();
                    }
                    break;
                case VERT:
                    vertices = new float[vertexCount * 3];
                    readFloat(vertices, in);
                    break;
                case NORM:
                    normals = new float[vertexCount * 3];
                    readFloat(normals, in);
                    break;
                case TEXC:
                    tex.add(readUVData(vertexCount, in));
                    break;
                case ATTR:
                    att.add(readAttrData(vertexCount, in));
                    break;
                default:
                    throw new IOException("Error: Unknown data tag \"" + unpack(tag) + "\"");
            }
        }
        return new Mesh(vertices, normals, indices, tex, att);
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
        float[] data = new float[vertCount * 2];
        readFloat(data, in);

        return new AttributeData(name, matname, 1, data);
    }

    private static AttributeData readAttrData(int vertCount, CtmInputStream in) throws IOException
    {
        String name = in.readString();
        float[] data = new float[vertCount * 4];
        readFloat(data, in);

        return new AttributeData(name, null, 1, data);
    }

    @Override
    public int getTag()
    {
        return RAW;
    }
}
