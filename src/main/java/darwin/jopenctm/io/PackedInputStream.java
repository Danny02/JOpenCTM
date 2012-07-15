///*
// * Copyright (C) 2012 Daniel Heinrich
// *
// * This library is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public
// * License as published by the Free Software Foundation; either
// * (version 2.1 of the License, or (at your option) any later version.
// *
// * This library is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with this library.  If not, see <http://www.gnu.org/licenses/> 
// * or write to the Free Software Foundation, Inc., 51 Franklin Street,
// * Fifth Floor, Boston, MA 02110-1301  USA.
// */
//package darwin.jopenctm.io;
//
//import java.io.*;
//import lzma.sdk.lzma.Decoder;
//import org.cservenak.streams.Coder;
//import org.cservenak.streams.CoderInputStream;
//
///**
// *
// * @author daniel
// */
//public final class PackedInputStream extends CoderInputStream {
//
//    protected PackedInputStream(byte[] packedData) throws IOException {
//        super(new ByteArrayInputStream(packedData), new CustomWrapper(packedData.length));
//    }
//
//    private static class CustomWrapper implements Coder {
//
//        private final Decoder d;
//        private final int len;
//
//        public CustomWrapper(int packedLength) {
//            d = new Decoder();
//            len = packedLength;
//        }
//
//        @Override
//        public void code(final InputStream in, final OutputStream out)
//                throws IOException {
//
//            byte[] properties = new byte[5];
//            if (in.read(properties) != 5) {
//                throw new IOException("LZMA file has no header!");
//            }
//
//            if (!d.setDecoderProperties(properties)) {
//                throw new IOException("Decoder properties cannot be set!");
//            }
//
//            if (!d.code(in, out, len)) {
//                throw new IOException("Decoding unsuccessful!");
//            }
//        }
//    }
//}