/*
 * Copyright (C) 2012 Daniel Heinrich
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * (version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301  USA.
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
