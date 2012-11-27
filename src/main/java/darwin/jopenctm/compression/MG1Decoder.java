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
import darwin.jopenctm.data.Mesh;
import darwin.jopenctm.errorhandling.BadFormatException;
import darwin.jopenctm.errorhandling.InvalidDataException;
import darwin.jopenctm.io.CtmInputStream;
import darwin.jopenctm.io.MeshInfo;

import static darwin.jopenctm.io.CtmFileReader.getTagInt;

/**
 *
 * @author daniel
 */
@ServiceProvider(MeshDecoder.class)
public class MG1Decoder extends RawDecoder
{

    public static final int MG1_TAG = getTagInt("MG1\0");

    @Override
    public Mesh decode(MeshInfo minfo, CtmInputStream in) throws IOException, BadFormatException, InvalidDataException
    {
        Mesh m = super.decode(minfo, in);
        restoreIndices(minfo.getTriangleCount(), m.indices);
        return m;
    }

    @Override
    public boolean isFormatSupported(int tag, int version)
    {
        return tag == MG1_TAG && version == FORMAT_VERSION;
    }

    @Override
    protected float[] readFloatArray(CtmInputStream in, int count, int size) throws IOException
    {
        return in.readPackedFloats(count, size);
    }

    @Override
    protected int[] readIntArray(CtmInputStream in, int count, int size, boolean signed) throws IOException
    {
        return in.readPackedInts(count, size, signed);
    }

    public void restoreIndices(int triangleCount, int[] indices)
    {
        for (int i = 0; i < triangleCount; ++i) {
            // Step 1: Reverse derivative of the first triangle index
            if (i >= 1) {
                indices[i * 3] += indices[(i - 1) * 3];
            }

            // Step 2: Reverse delta from third triangle index to the first triangle
            // index
            indices[i * 3 + 2] += indices[i * 3];

            // Step 3: Reverse delta from second triangle index to the previous
            // second triangle index, if the previous triangle shares the same first
            // index, otherwise reverse the delta to the first triangle index
            if ((i >= 1) && (indices[i * 3] == indices[(i - 1) * 3])) {
                indices[i * 3 + 1] += indices[(i - 1) * 3 + 1];
            } else {
                indices[i * 3 + 1] += indices[i * 3];
            }
        }
    }
}
