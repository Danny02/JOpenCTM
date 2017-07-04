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

import darwin.jopenctm.errorhandling.InvalidDataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
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

    public void checkIntegrity() throws InvalidDataException {
        List<String> errors = validate();
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("The model is not valid:");
            for (String e : errors) {
                sb.append("\n\t- ").append(e);
            }
            throw new InvalidDataException(sb.toString());
        }
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        // Check that we have all the mandatory data
        if (vertices == null || vertices.length < 1) {
            errors.add("The vertex array is NULL or empty!");
        }
        if (indices == null || getTriangleCount() < 1) {
            errors.add("The index array does not describe a single triangle!" +
                    (indices != null ? " - " + Arrays.toString(indices) : ""));
        }

        if (indices.length % 3 != 0) {
            errors.add("The indices array size is not a multiple of three! - " + indices.length);
        }

        if (vertices.length % CTM_POSITION_ELEMENT_COUNT != 0) {
            errors.add("The vertex array size is not a multiple of CTM_POSITION_ELEMENT_COUNT("
                    + CTM_POSITION_ELEMENT_COUNT + ")! - " + vertices.length);
        }

        if (normals != null && normals.length % CTM_NORMAL_ELEMENT_COUNT != 0) {
            errors.add("The normal array size is not a multiple of CTM_NORMAL_ELEMENT_COUNT(" +
                    CTM_NORMAL_ELEMENT_COUNT + ")! - " + normals.length);
        }

        int nCount = normals.length / CTM_NORMAL_ELEMENT_COUNT;
        if (normals != null && nCount != getVertexCount()) {
            errors.add("There aren't the same number of normals as vertices! - n:" + nCount + " v:" + getVertexCount());
        }

        // Check that all indices are within range
        for (int i = 0; i < indices.length; i++) {
            if (indices[i] >= getVertexCount()) {
                errors.add("element(" + i + ") of the indices array points to a none existing vertex(id: " +
                        indices[i] + ")");
            }
        }

        // Check that all vertices are finite (non-NaN, non-inf)
        for (int i = 0; i < vertices.length; i++) {
            if (isNotFinite(vertices[i])) {
                errors.add("vertex value (" + i + ": " + vertices[i] + ") is not finite!");
            }
        }

        // Check that all normals are finite (non-NaN, non-inf)
        if (normals != null) {
            for (int i = 0; i < normals.length; i++) {
                if (isNotFinite(normals[i])) {
                    errors.add("normal value (" + i + ": " + normals[i] + ") is not finite!");
                }
            }
        }

        // Check that all UV maps are finite (non-NaN, non-inf)
        for (int i = 0; i < texcoordinates.length; i++) {
            AttributeData map = texcoordinates[i];
            for (int j = 0; j < map.values.length; j++) {
                if (isNotFinite(map.values[j])) {
                    errors.add("texcoord(" + i + ") value (" + j + ": " + map.values[j] + ") is not finite!");
                }
            }

            if (map.values.length % CTM_UV_ELEMENT_COUNT != 0) {
                errors.add("The uv(" + i + ") values size is not a multiple of CTM_UV_ELEMENT_COUNT(" +
                        CTM_UV_ELEMENT_COUNT + ")! - " + map.values.length);
            }
            int uvCount = map.values.length / CTM_UV_ELEMENT_COUNT;
            if (uvCount != getVertexCount()) {
                errors.add("There aren't the same number of uv(" + i + ") values as vertices! - n:"
                        + uvCount + " v:" + getVertexCount());
            }
        }

        // Check that all attribute maps are finite (non-NaN, non-inf)
        for (int i = 0; i < attributes.length; i++) {
            AttributeData map = attributes[i];
            for (int j = 0; j < map.values.length; j++) {
                if (isNotFinite(map.values[j])) {
                    errors.add("attribute(" + i + ") value (" + j + ": " + map.values[j] + ") is not finite!");
                }
            }

            if (map.values.length % CTM_ATTR_ELEMENT_COUNT != 0) {
                errors.add("The generic attribute(" + i + ") values size is not a multiple of CTM_ATTR_ELEMENT_COUNT(" +
                        CTM_ATTR_ELEMENT_COUNT + ")! - " + map.values.length);
            }
            int atCount = map.values.length / CTM_ATTR_ELEMENT_COUNT;
            if (atCount != getVertexCount()) {
                errors.add("There aren't the same number of attribute(" + i + ") values as vertices! - n:"
                        + atCount + " v:" + getVertexCount());
            }
        }

        return errors;
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
