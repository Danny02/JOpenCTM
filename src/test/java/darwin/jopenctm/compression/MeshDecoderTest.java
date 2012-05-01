///*
// * Copyright (C) 2012 daniel
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package darwin.jopenctm.compression;
//
//import java.io.*;
//import junit.framework.TestCase;
//import org.junit.Test;
//
//import darwin.geometrie.io.obj.ObjModelReader;
//import darwin.geometrie.unpacked.Model;
//
///**
// *
// * @author daniel
// */
//public class MeshDecoderTest extends TestCase
//{
//
//    private Model[] mesh;
//
//    public MeshDecoderTest(String testName)
//    {
//        super(testName);
//    }
//
//    @Override
//    protected void setUp() throws Exception
//    {
//        try (InputStream is = getClass().getResourceAsStream("/Brunnen.obj")) {
//            if (is == null) {
//                fail("couldn't load model Brunnen");
//            }
//            mesh = new ObjModelReader().readModel(is);
//        }
//    }
//
//    /**
//     * Test of decode method, of class MeshDecoder.
//     */
//    @Test
//    public void testRawDecode() throws Exception
//    {
////        ModelWriter mw = new CtmModelWriter(new RawEncoder());
////
////        File tmpCtm = File.createTempFile("testModel", ".ctm");
////
////        try (OutputStream out = new FileOutputStream(tmpCtm)) {
////            mw.writeModel(out, mesh);
////            out.flush();
////
////            try (InputStream iss = new FileInputStream(tmpCtm)) {
////                ModelReader mre = new CtmModelReader();
////                Model[] loadedMesh = mre.readModel(iss);
////                if (!Arrays.equals(mesh, loadedMesh)) {
////                    fail("En- and Redecodeing an Mesh with RAW doesn't result in the same Mesh");
////                }
////            }
////        }
//    }
//}
