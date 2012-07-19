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

import org.junit.Before;
import org.junit.Test;

import darwin.jopenctm.data.AttributeData;
import darwin.jopenctm.data.Mesh;
import darwin.jopenctm.io.CtmFileReader;
import darwin.jopenctm.io.CtmFileWriter;

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
            float[] vertices = new float[]{0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0};
            int[] indices = new int[]{0, 1, 2, 0, 2, 3};
            mesh = new Mesh(vertices, null, indices,
                            new AttributeData[0], new AttributeData[0]);
            mesh.checkIntegrity();
        }
    }

    @Test
    public void testMG1IndicePacking() {
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
        //test can not work, because the MG2 coding reorders triangles and does lossy compression
//        testEncoder(new MG2Encoder());
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
}
