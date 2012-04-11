/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.compression;

import darwin.jopenctm.data.Triangle;
import java.io.IOException;
import java.util.Arrays;

import darwin.annotations.ServiceProvider;
import darwin.jopenctm.io.CtmOutputStream;
import darwin.jopenctm.compression.MeshEncoder;
import darwin.jopenctm.compression.RawEncoder;

/**
 *
 * @author daniel
 */
@ServiceProvider(MeshEncoder.class)
public class MG1Encoder extends RawEncoder
{

    @Override
    public int getTag()
    {
        return MG1Decoder.MG1_TAG;
    }

    @Override
    protected void writeFloatArray(float[] array, CtmOutputStream out, int count, int size) throws IOException
    {
        out.writePackedFloats(array, count,
                size);
    }

    @Override
    protected void writeIndicies(int[] indices, CtmOutputStream out) throws IOException
    {
        int[] tmp = new int[indices.length];
        System.arraycopy(indices, 0, tmp, 0, tmp.length);
        rearrangeTriangles(indices);
        makeIndexDeltas(indices);
        out.writePackedInts(tmp, indices.length / 3, 3, false);
    }

    /**
     * Re-arrange all triangles for optimal compression.
     */
    public void rearrangeTriangles(int[] indices)
    {
        assert indices.length % 3 == 0;
        // Step 1: Make sure that the first index of each triangle is the smallest
        // one (rotate triangle nodes if necessary)
        for (int off = 0; off < indices.length; off += 3) {
            if ((indices[off + 1] < indices[off]) && (indices[off + 1] < indices[off + 2])) {
                int tmp = indices[off];
                indices[off] = indices[off + 1];
                indices[off + 1] = indices[off + 2];
                indices[off + 2] = tmp;
            } else if ((indices[off + 2] < indices[off]) && (indices[off + 2] < indices[off + 1])) {
                int tmp = indices[off];
                indices[off] = indices[off + 2];
                indices[off + 2] = indices[off + 1];
                indices[off + 1] = tmp;
            }
        }

        // Step 2: Sort the triangles based on the first triangle index
        Triangle[] tris = new Triangle[indices.length / 3];
        for (int i = 0; i < tris.length; i++) {
            int off = i * 3;
            tris[i] = new Triangle(indices, off);
        }

        Arrays.sort(tris);

        for (int i = 0; i < tris.length; i++) {
            int off = i * 3;
            tris[i].copyBack(indices, off);
        }
    }

    /**
     * Calculate various forms of derivatives in order to reduce data entropy.
     */
    public void makeIndexDeltas(int[] indices)
    {
        assert indices.length % 3 == 0;

        for (int i = indices.length / 3 - 1; i >= 0; --i) {
            // Step 1: Calculate delta from second triangle index to the previous
            // second triangle index, if the previous triangle shares the same first
            // index, otherwise calculate the delta to the first triangle index
            if ((i >= 1) && (indices[i * 3] == indices[(i - 1) * 3])) {
                indices[i * 3 + 1] -= indices[(i - 1) * 3 + 1];
            } else {
                indices[i * 3 + 1] -= indices[i * 3];
            }

            // Step 2: Calculate delta from third triangle index to the first triangle
            // index
            indices[i * 3 + 2] -= indices[i * 3];

            // Step 3: Calculate derivative of the first triangle index
            if (i >= 1) {
                indices[i * 3] -= indices[(i - 1) * 3];
            }
        }
    }
}
