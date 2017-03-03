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

import java.io.*;

import darwin.jopenctm.data.*;
import darwin.jopenctm.io.*;

import org.junit.*;

import static org.junit.Assert.*;

/**
 *
 * @author daniel
 */
public class MeshDecoderTest {

    private Mesh mesh;

    @Before
    public void setUp() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/Brunnen.obj")) {
            if (is == null) {
                fail("couldn't load model Brunnen");
            }
            int[] indices = new int[]{0, 1, 2, 0, 2, 3};
            float[] vertices = new float[]{0, 0, 0,
                                           1, 0, 0,
                                           1, 1, 0,
                                           0, 1, 0};
            float[] normals = new float[]{
                1, 1, 1,
                1, 1, 1,
                1, 1, 1,
                1, 1, 1
            };

            AttributeData[] uv = new AttributeData[]{
                new AttributeData("uv1", "test", AttributeData.STANDARD_UV_PRECISION,
                                  new float[]{
                    0.01f, 0.6f,
                    0.43f, 0.12f,
                    0.9331f, 0.632f,
                    0.141f, 0.823f
                })
            };

            mesh = new Mesh(vertices, normals, indices,
                             uv, new AttributeData[0]);
            mesh.checkIntegrity();
        }
    }

    @Test
    public void testMG1IndicesPacking() {
        int[] ind = new int[mesh.indices.length];
        System.arraycopy(mesh.indices, 0, ind, 0, mesh.indices.length);

        MG1Encoder enc = new MG1Encoder();
        enc.rearrangeTriangles(ind);
        enc.makeIndexDeltas(ind);

        MG2Decoder dec = new MG2Decoder();
        dec.restoreIndices(ind.length / 3, ind);

        assertArrayEquals(ind, mesh.indices);
    }

    /**
     * Test of decode method, of class MeshDecoder.
     */
    @Test
    public void testRawCoder() throws Exception {
        testEncoder(new RawEncoder());
    }

    @Test
    public void testMG1Coder() throws Exception {
        testEncoder(new MG1Encoder());
    }

    @Test
    public void testMG2Coder() throws Exception {
        MG2Encoder encoder = new MG2Encoder();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CtmFileWriter writer = new CtmFileWriter(out, encoder);
            writer.encode(mesh, null);
            out.flush();

            try (InputStream iss = new ByteArrayInputStream(out.toByteArray())) {
                CtmFileReader reader = new CtmFileReader(iss);

                MG2MeshEqualsTest(encoder, mesh, reader.decode());
            }
        }
    }

    private void testEncoder(MeshEncoder encoder) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CtmFileWriter writer = new CtmFileWriter(out, encoder);
            writer.encode(mesh, null);
            out.flush();

            try (InputStream iss = new ByteArrayInputStream(out.toByteArray())) {
                CtmFileReader reader = new CtmFileReader(iss);
                Mesh loadedMesh = reader.decode();

                String tag = CtmFileReader.unpack(encoder.getTag()).substring(0, 3);
                assertEquals("En- and Redecodeing an Mesh with " + tag
                             + " doesn't result in the same Mesh",
                             mesh, loadedMesh);
            }
        }
    }

    private void MG2MeshEqualsTest(MG2Encoder enc, Mesh orig, Mesh read) {
        assertEquals("Trianglecount differs", orig.getTriangleCount(), read.getTriangleCount());
        assertEquals("Vertexcount differs", orig.getVertexCount(), read.getVertexCount());
        assertEquals("Only one has normals", orig.hasNormals(), read.hasNormals());

        Grid grid = enc.setupGrid(orig.vertices);
        SortableVertex[] sorted = enc.sortVertices(grid, orig.vertices);
        int[] indexLUT = new int[sorted.length];
        for (int i = 0; i < sorted.length; ++i) {
            indexLUT[sorted[i].originalIndex] = i;
        }

        for (int i = 0; i < orig.getVertexCount(); i++) {
            int newIndex = indexLUT[i];

            for (int e = 0; e < Mesh.CTM_POSITION_ELEMENT_COUNT; e++) {
                assertTrue("positions not in precision", compare(orig.vertices[i * 3 + e],
                                                                 read.vertices[newIndex * 3 + e],
                                                                 enc.vertexPrecision * 2));
            }
            if (orig.hasNormals()) {
                for (int e = 0; e < Mesh.CTM_NORMAL_ELEMENT_COUNT; e++) {
                    assertTrue("normals not in precision", compare(orig.normals[i * 3 + e],
                                                                   read.normals[newIndex * 3 + e],
                                                                   enc.normalPrecision * 10));
                }
            }
        }

        testAttributeArrays(orig.texcoordinates, read.texcoordinates, indexLUT);
        testAttributeArrays(orig.attributes, read.attributes, indexLUT);
    }

    private void testAttributeArrays(AttributeData[] a, AttributeData[] b, int[] indexLUT) {
        if ((a == null || a.length == 0) && (b == null || b.length == 0)) {
            return;
        }

        assertEquals(a.length, b.length);

        for (int i = 0; i < a.length; i++) {
            assertEquals(a[i].materialName, b[i].materialName);
            assertEquals(a[i].name, b[i].name);
            assertEquals(a[i].precision, b[i].precision, 0);

            float[] orig = a[i].values;
            float[] read = b[i].values;

            assertEquals(orig.length, read.length);

            int count = orig.length / indexLUT.length;

            assertEquals(count * indexLUT.length, orig.length);


            for (int vi = 0; vi < indexLUT.length; vi++) {
                int newIndex = indexLUT[vi];

                for (int e = 0; e < count; e++) {
                    assertTrue("Attributes not in precision", compare(orig[vi * count + e],
                                                                     read[newIndex * count + e],
                                                                     a[i].precision * 2));
                }
            }

        }
    }

    private boolean compare(float a, float b, float precision) {
        return Math.abs(a - b) < precision;
    }
}
