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
package darwin.jopenctm.data;

import java.util.Arrays;

import darwin.jopenctm.errorhandling.InvalidDataException;

/**
 *
 * @author daniel
 */
public class Mesh {

    public static final int CTM_ATTR_ELEMENT_COUNT = 4;
    public static final int CTM_NORMAL_ELEMENT_COUNT = 3;
    public static final int CTM_POSITION_ELEMENT_COUNT = 3;
    public static final int CTM_UV_ELEMENT_COUNT = 2;
    //
    public final float[] vertices, normals;
    public final int[] indices;
    // Multiple sets of UV coordinate maps (optional)
    public final AttributeData[] texcoordinates;
    // Multiple sets of custom vertex attribute maps (optional)
    public final AttributeData[] attributes;

    public Mesh(float[] vertices, float[] normals, int[] indices, AttributeData[] texcoordinates, AttributeData[] attributes) {
        this.vertices = vertices;
        this.normals = normals;
        this.indices = indices;
        this.texcoordinates = texcoordinates;
        this.attributes = attributes;
    }

    public int getVertexCount() {
        return vertices.length / CTM_POSITION_ELEMENT_COUNT;
    }

    public int getUVCount() {
        return texcoordinates.length;
    }

    public int getAttrCount() {
        return attributes.length;
    }

    public int getTriangleCount() {
        return indices.length / 3;
    }

    public boolean hasNormals() {
        return normals != null;
    }

    public float getAverageEdgeLength() {
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

    public void checkIntegrity() throws InvalidDataException {

        // Check that we have all the mandatory data
        if (vertices == null || indices == null || vertices.length < 1
            || getTriangleCount() < 1) {
            throw new InvalidDataException("The vertice or indice array is NULL"
                                           + " or empty!");
        }

        if (indices.length % 3 != 0) {
            throw new InvalidDataException("The indices array size is not a multiple of three!");
        }

        if (vertices.length % CTM_POSITION_ELEMENT_COUNT != 0) {
            throw new InvalidDataException("The vertex array size is not a multiple of CTM_POSITION_ELEMENT_COUNT!");
        }

        if (normals != null && normals.length % CTM_NORMAL_ELEMENT_COUNT != 0) {
            throw new InvalidDataException("The normal array size is not a multiple of CTM_NORMAL_ELEMENT_COUNT!");
        }
        if (normals != null && normals.length / CTM_NORMAL_ELEMENT_COUNT != getVertexCount()) {
            throw new InvalidDataException("There aren't the same number of normals as vertices");
        }

        // Check that all indices are within range
        for (int ind : indices) {
            if (ind >= getVertexCount()) {
                throw new InvalidDataException("One element of the indices array "
                                               + "points to a none existing vertex(id: " + ind + ")");
            }
        }

        // Check that all vertices are finite (non-NaN, non-inf)
        for (float v : vertices) {
            if (isNotFinite(v)) {
                throw new InvalidDataException("One of the vertices values is not finite!");
            }
        }

        // Check that all normals are finite (non-NaN, non-inf)
        if (normals != null) {
            for (float n : normals) {
                if (isNotFinite(n)) {
                    throw new InvalidDataException("One of the normal values is not finite!");
                }
            }
        }

        // Check that all UV maps are finite (non-NaN, non-inf)
        for (AttributeData map : texcoordinates) {
            for (float v : map.values) {
                if (isNotFinite(v)) {
                    throw new InvalidDataException("One of the texcoord values is not finite!");
                }
            }

            if (map.values.length % CTM_UV_ELEMENT_COUNT != 0) {
                throw new InvalidDataException("The uv values size is not a multiple of CTM_UV_ELEMENT_COUNT!");
            }
            if (map.values.length / CTM_UV_ELEMENT_COUNT != getVertexCount()) {
                throw new InvalidDataException("There aren't the same number of uv values as vertices");
            }
        }

        // Check that all attribute maps are finite (non-NaN, non-inf)
        for (AttributeData map : attributes) {
            for (float v : map.values) {
                if (isNotFinite(v)) {
                    throw new InvalidDataException("One of the attribute values is not finite!");
                }
            }

            if (map.values.length % CTM_ATTR_ELEMENT_COUNT != 0) {
                throw new InvalidDataException("The generic attribute values size is not a multiple of CTM_ATTR_ELEMENT_COUNT!");
            }
            if (map.values.length / CTM_ATTR_ELEMENT_COUNT != getVertexCount()) {
                throw new InvalidDataException("There aren't the same number of attribute values as vertices");
            }
        }
    }

    private boolean isNotFinite(float value) {
        Float v = value;
        return v.isInfinite() || v.isNaN();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Arrays.hashCode(this.vertices);
        hash = 67 * hash + Arrays.hashCode(this.normals);
        hash = 67 * hash + Arrays.hashCode(this.indices);
        hash = 67 * hash + Arrays.deepHashCode(this.texcoordinates);
        hash = 67 * hash + Arrays.deepHashCode(this.attributes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Mesh other = (Mesh) obj;
        if (!Arrays.equals(this.vertices, other.vertices)) {
            return false;
        }
        if (!Arrays.equals(this.normals, other.normals)) {
            return false;
        }
        if (!Arrays.equals(this.indices, other.indices)) {
            return false;
        }
        if (!Arrays.deepEquals(this.texcoordinates, other.texcoordinates)) {
            return false;
        }
        if (!Arrays.deepEquals(this.attributes, other.attributes)) {
            return false;
        }
        return true;
    }
}
