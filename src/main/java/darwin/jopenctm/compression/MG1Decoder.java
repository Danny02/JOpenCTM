/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.compression;

import java.io.IOException;

import darwin.annotations.ServiceProvider;
import darwin.jopenctm.*;

import static darwin.jopenctm.CtmFileReader.*;

/**
 *
 * @author daniel
 */
@ServiceProvider(MeshDecoder.class)
public class MG1Decoder extends RawDecoder
{

    public static final int MG1_TAG = getTagInt("MG1\0");

    @Override
    public Mesh decode(MeshInfo minfo, CtmInputStream in) throws IOException
    {
        Mesh m = super.decode(minfo, in);
        restoreIndices(minfo.getTriangleCount(), m.indices);
        return m;
    }

    @Override
    public int getTag()
    {
        return MG1_TAG;
    }

    @Override
    protected void readFloatArray(float[] array, CtmInputStream in, int count, int size) throws IOException
    {
        in.readPackedFloats(array, count, size);
    }

    @Override
    protected void readIntArray(int[] array, CtmInputStream in, int count, int size, boolean signed) throws IOException
    {
        in.readPackedInts(array, count, size, signed);
    }

    private void restoreIndices(int triangleCount, int[] indices)
    {
        for (int i = 0; i < triangleCount; ++i) {
            // Step 1: Reverse derivative of the first triangle index
            if (i >= 1) {
                indices[i * 3] += indices[(i - 1) * 3];
            }

            // Step 2: Reverse delta from third triangle index to the first triangle
            // index
            indices[i * 3 + 2] += indices[i * 3];

            // Step 3: Reverse delta from second triangle index to the previous
            // second triangle index, if the previous triangle shares the same first
            // index, otherwise reverse the delta to the first triangle index
            if ((i >= 1) && (indices[i * 3] == indices[(i - 1) * 3])) {
                indices[i * 3 + 1] += indices[(i - 1) * 3 + 1];
            } else {
                indices[i * 3 + 1] += indices[i * 3];
            }
        }
    }
}
