/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm;

/**
 *
 * @author daniel
 */
public class Mesh
{

    public final float[] vertices, normals;
    public final int[] indices;
    // Multiple sets of UV coordinate maps (optional)
    public final AttributeData[] texcoordinates;
    // Multiple sets of custom vertex attribute maps (optional)
    public final AttributeData[] attributs;

    public Mesh(float[] vertices, float[] normals, int[] indices, AttributeData[] texcoordinates, AttributeData[] attributs)
    {
        this.vertices = vertices;
        this.normals = normals;
        this.indices = indices;
        this.texcoordinates = texcoordinates;
        this.attributs = attributs;
    }

    public int getVertexCount()
    {
        return vertices.length;
    }

    public int getUVCount()
    {
        return texcoordinates.length;
    }

    public int getAttrCount()
    {
        return attributs.length;
    }

    public int getTriangleCount()
    {
        return indices.length / 3;
    }

    public boolean hasNormals()
    {
        return normals != null;
    }

    public float getAverageEdgeLength()
    {
        // Calculate the average edge length (Note: we actually sum up all the half-
        // edges, so in a proper solid mesh all connected edges are counted twice)

        float totalLength = 0;
        int edgeCount = 0;

        Mesh m = null; //TODO mesh zugriff
        for (int i = 0; i < getTriangleCount(); ++i) {
            int p1, p2;
            p1 = indices[i * 3 + 2] * 3;
            for (int j = 0; j < 3; ++j) {
                p2 = indices[i * 3 + j] * 3;
                float length = (vertices[p2] - vertices[p1]) * (vertices[p2] - vertices[p1]);
                length += (vertices[p2 + 1] - vertices[p1 + 1]) * (vertices[p2 + 1] - vertices[p1 + 1]);
                length += (vertices[p2 + 2] - vertices[p1 + 2]) * (vertices[p2 + 2] - vertices[p1 + 2]);
                totalLength += Math.sqrt(length);
                p1 = p2;
                ++edgeCount;
            }
        }

        return totalLength / edgeCount;
    }

    public boolean checkIntegrity()
    {
        // Check that we have all the mandatory data
        if (vertices == null || indices == null || vertices.length < 1
                || getTriangleCount() < 1) {
            return false;
        }

        if (indices.length % 3 != 0) {
            return false;
        }

        // Check that all indices are within range
        for (int ind : indices) {
            if (ind >= vertices.length) {
                return false;
            }
        }

        // Check that all vertices are finite (non-NaN, non-inf)
        for (float v : vertices) {
            if (isNotFinit(v)) {
                return false;
            }
        }

        // Check that all normals are finite (non-NaN, non-inf)
        if (normals != null) {
            for (float n : normals) {
                if (isNotFinit(n)) {
                    return false;
                }
            }
        }

        // Check that all UV maps are finite (non-NaN, non-inf)
        for (AttributeData map : texcoordinates) {
            for (float v : map.values) {
                if (isNotFinit(v)) {
                    return false;
                }
            }
        }

        // Check that all attribute maps are finite (non-NaN, non-inf)
        for (AttributeData map : attributs) {
            for (float v : map.values) {
                if (isNotFinit(v)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isNotFinit(float value)
    {
        Float v = value;
        return v.isInfinite() || v.isNaN();
    }
}
