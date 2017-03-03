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
package darwin.jopenctm.compression;

import java.io.IOException;

import darwin.annotations.ServiceProvider;
import darwin.jopenctm.data.*;
import darwin.jopenctm.errorhandling.*;
import darwin.jopenctm.io.*;

import static darwin.jopenctm.compression.CommonAlgorithms.*;
import static darwin.jopenctm.data.Mesh.*;
import static java.lang.Math.*;

/**
 *
 * @author daniel
 */
@ServiceProvider(MeshDecoder.class)
public class MG2Decoder extends MG1Decoder {

    public static final int MG2_Tag = CtmFileReader.getTagInt("MG2\0");
    public static final int MG2_HEADER_TAG = CtmFileReader.getTagInt("MG2H");
    public static final int GIDX = CtmFileReader.getTagInt("GIDX");

    @Override
    public boolean isFormatSupported(int tag, int version) {
        return tag == MG2_Tag && version == RawDecoder.FORMAT_VERSION;
    }

    @Override
    public Mesh decode(MeshInfo minfo, CtmInputStream in) throws IOException, BadFormatException, InvalidDataException {
        int vc = minfo.getVertexCount();

        checkTag(in.readLittleInt(), MG2_HEADER_TAG);
        float vertexPrecision = in.readLittleFloat();
        float normalPrecision = in.readLittleFloat();

        Grid grid = Grid.fromStream(in);
        if(!grid.checkIntegrity()) {
            throw new InvalidDataException("The vertex size grid is corrupt!");
        }

        float[] vertices = readVertices(in, grid, vc, vertexPrecision);

        int[] indices = readIndices(in, minfo.getTriangleCount(), vc);

        float[] normals = null;
        if (minfo.hasNormals()) {
            normals = readNormals(in, vertices, indices, normalPrecision, vc);
        }

        AttributeData[] uvData = new AttributeData[minfo.getUvMapCount()];
        for (int i = 0; i < uvData.length; i++) {
            uvData[i] = readUvData(in, vc);
        }

        AttributeData[] attributes = new AttributeData[minfo.getAttrCount()];
        for (int i = 0; i < attributes.length; i++) {
            attributes[i] = readAttribute(in, vc);
        }

        return new Mesh(vertices, normals, indices, uvData, attributes);
    }

    private float[] readVertices(CtmInputStream in, Grid grid, int vcount, float precision) throws IOException, BadFormatException {
        checkTag(in.readLittleInt(), VERT);
        int[] intVertices = in.readPackedInts(vcount, CTM_POSITION_ELEMENT_COUNT, false);

        checkTag(in.readLittleInt(), GIDX);
        int[] gridIndices = in.readPackedInts(vcount, 1, false);
        for (int i = 1; i < vcount; i++) {
            gridIndices[i] += gridIndices[i - 1];
        }

        return restoreVertices(intVertices, gridIndices, grid, precision);
    }

    private int[] readIndices(CtmInputStream in, int triCount, int vcount) throws IOException, InvalidDataException, BadFormatException {
        checkTag(in.readLittleInt(), INDX);
        int[] indices = in.readPackedInts(triCount, 3, false);
        restoreIndices(triCount, indices);
        for (int i : indices) {
            if (i > vcount) {
                throw new InvalidDataException("One element of the indices array "
                                               + "points to a none existing vertex(id: " + i + ")");
            }
        }
        return indices;
    }

    private float[] readNormals(CtmInputStream in, float[] vertices, int[] indices,
                                float normalPrecision, int vcount) throws IOException, BadFormatException {
        checkTag(in.readLittleInt(), NORM);
        int[] intNormals = in.readPackedInts(vcount, CTM_NORMAL_ELEMENT_COUNT, false);
        return restoreNormals(intNormals, vertices, indices, normalPrecision);
    }

    private AttributeData readUvData(CtmInputStream in, int vcount) throws IOException, BadFormatException, InvalidDataException {
        checkTag(in.readLittleInt(), TEXC);
        String name = in.readString();
        String material = in.readString();
        float precision = in.readLittleFloat();
        if (precision <= 0f) {
            throw new InvalidDataException("A uv precision value <= 0.0 was read");
        }

        int[] intCoords = in.readPackedInts(vcount, CTM_UV_ELEMENT_COUNT, true);
        float[] data = restoreUVCoords(precision, intCoords);

        return new AttributeData(name, material, precision, data);
    }

    private AttributeData readAttribute(CtmInputStream in, int vc) throws IOException, BadFormatException, InvalidDataException {
        checkTag(in.readLittleInt(), ATTR);

        String name = in.readString();
        float precision = in.readLittleFloat();
        if (precision <= 0f) {
            throw new InvalidDataException("An attribute precision value <= 0.0 was read");
        }

        int[] intData = in.readPackedInts(vc, CTM_ATTR_ELEMENT_COUNT, true);
        float[] data = restoreAttribs(precision, intData);

        return new AttributeData(name, null, precision, data);
    }

    /**
     * Calculate inverse derivatives of the vertex attributes.
     */
    private float[] restoreAttribs(float precision, int[] intAttribs) {
        int ae = CTM_ATTR_ELEMENT_COUNT;
        int vc = intAttribs.length / ae;
        float[] values = new float[intAttribs.length];
        int[] prev = new int[ae];
        for (int i = 0; i < vc; ++i) {
            // Calculate inverse delta, and convert to floating point
            for (int j = 0; j < ae; ++j) {
                int value = intAttribs[i * ae + j] + prev[j];
                values[i * ae + j] = value * precision;
                prev[j] = value;
            }
        }
        return values;
    }

    /**
     * Calculate inverse derivatives of the UV coordinates.
     */
    private float[] restoreUVCoords(float precision, int[] intUVCoords) {
        int vc = intUVCoords.length / CTM_UV_ELEMENT_COUNT;
        float[] values = new float[intUVCoords.length];
        int prevU = 0, prevV = 0;
        for (int i = 0; i < vc; ++i) {
            // Calculate inverse delta
            int u = intUVCoords[i * CTM_UV_ELEMENT_COUNT] + prevU;
            int v = intUVCoords[i * CTM_UV_ELEMENT_COUNT + 1] + prevV;

            // Convert to floating point
            values[i * CTM_UV_ELEMENT_COUNT] = u * precision;
            values[i * CTM_UV_ELEMENT_COUNT + 1] = v * precision;

            prevU = u;
            prevV = v;
        }
        return values;
    }

    /**
     * Convert the normals back to cartesian coordinates.
     */
    private float[] restoreNormals(int[] intNormals, float[] vertices, int[] indices, float normalPrecision) {

        // Calculate smooth normals (nominal normals)
        float[] smoothNormals = calcSmoothNormals(vertices, indices);
        float[] normals = new float[vertices.length];

        int vc = vertices.length / CTM_POSITION_ELEMENT_COUNT;
        int ne = CTM_NORMAL_ELEMENT_COUNT;

        for (int i = 0; i < vc; ++i) {
            // Get the normal magnitude from the first of the three normal elements
            float magn = intNormals[i * ne] * normalPrecision;

            // Get phi and theta (spherical coordinates, relative to the smooth normal).
            double thetaScale, theta;
            int intPhi = intNormals[i * ne + 1];
            double phi = intPhi * (0.5 * PI) * normalPrecision;
            if (intPhi == 0) {
                thetaScale = 0.0f;
            } else if (intPhi <= 4) {
                thetaScale = PI / 2.0f;
            } else {
                thetaScale = (2.0f * PI) / intPhi;
            }
            theta = intNormals[i * ne + 2] * thetaScale - PI;

            // Convert the normal from the angular representation (phi, theta) back to
            // cartesian coordinates
            double[] n2 = new double[3];
            n2[0] = sin(phi) * cos(theta);
            n2[1] = sin(phi) * sin(theta);
            n2[2] = cos(phi);
            float[] basisAxes = makeNormalCoordSys(smoothNormals, i * ne);
            double[] n = new double[3];
            for (int j = 0; j < 3; ++j) {
                n[j] = basisAxes[j] * n2[0]
                       + basisAxes[3 + j] * n2[1]
                       + basisAxes[6 + j] * n2[2];
            }

            // Apply normal magnitude, and output to the normals array
            for (int j = 0; j < 3; ++j) {
                normals[i * ne + j] = (float) (n[j] * magn);
            }
        }

        return normals;
    }
}
