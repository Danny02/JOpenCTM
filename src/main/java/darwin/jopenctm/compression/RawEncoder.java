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

import darwin.annotations.ServiceProvider;
import darwin.jopenctm.data.AttributeData;
import darwin.jopenctm.data.Mesh;
import darwin.jopenctm.io.CtmOutputStream;
import java.io.IOException;

import static darwin.jopenctm.compression.MeshDecoder.*;

/**
 *
 * @author daniel
 */
@ServiceProvider(MeshEncoder.class)
public class RawEncoder implements MeshEncoder
{

    @Override
    public void encode(Mesh m, CtmOutputStream out) throws IOException
    {
        int vc = m.getVertexCount();

        out.writeLittleInt(INDX);
        writeIndicies(m.indices, out);

        out.writeLittleInt(VERT);
        writeFloatArray(m.vertices, out, vc * 3, 1);

        // Write normals
        if (m.normals != null) {
            out.writeLittleInt(NORM);
            writeFloatArray(m.normals, out, vc, 3);
        }

        for (AttributeData ad : m.texcoordinates) {
            out.writeLittleInt(TEXC);
            out.writeString(ad.name);
            out.writeString(ad.materialName);
            writeFloatArray(ad.values, out, vc, 2);
        }

        for (AttributeData ad : m.attributs) {
            out.writeLittleInt(ATTR);
            out.writeString(ad.name);
            writeFloatArray(ad.values, out, vc, 4);
        }
    }

    protected void writeIndicies(int[] indices, CtmOutputStream out) throws IOException
    {
        out.writeLittleIntArray(indices);
    }

    protected void writeFloatArray(float[] array, CtmOutputStream out, int count, int size) throws IOException
    {
        out.writeLittleFloatArray(array);
    }

    @Override
    public int getTag()
    {
        return RawDecoder.RAW_TAG;
    }

    @Override
    public int getFormatVersion()
    {
        return RawDecoder.FORMAT_VERSION;
    }
}
