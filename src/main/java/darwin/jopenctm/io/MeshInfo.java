/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.io;

/**
 *
 * @author daniel
 */
public class MeshInfo
{

    public static final int HAS_NORMAL_BIT = 1;

    private final int vertexCount, triangleCount, uvMapCount, attrCount, flags;

    public MeshInfo(int vertexCount, int triangleCount, int uvMapCount, int attrCount, int flags)
    {
        this.vertexCount = vertexCount;
        this.triangleCount = triangleCount;
        this.uvMapCount = uvMapCount;
        this.attrCount = attrCount;
        this.flags = flags;
    }

    public int getAttrCount()
    {
        return attrCount;
    }

    public int getTriangleCount()
    {
        return triangleCount;
    }

    public int getUvMapCount()
    {
        return uvMapCount;
    }

    public int getVertexCount()
    {
        return vertexCount;
    }

    public boolean hasNormals()
    {
        return (flags & HAS_NORMAL_BIT) > 0;
    }
}
