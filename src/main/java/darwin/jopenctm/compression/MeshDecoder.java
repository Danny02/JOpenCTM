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

import darwin.jopenctm.data.Mesh;
import darwin.jopenctm.errorhandling.*;
import darwin.jopenctm.io.*;

import static darwin.jopenctm.io.CtmFileReader.getTagInt;

/**
 *
 * @author daniel
 */
public interface MeshDecoder
{

    public static final int INDX = getTagInt("INDX");
    public static final int VERT = getTagInt("VERT");
    public static final int NORM = getTagInt("NORM");
    public static final int TEXC = getTagInt("TEXC");
    public static final int ATTR = getTagInt("ATTR");

    public Mesh decode(MeshInfo minfo, CtmInputStream in) throws IOException, BadFormatException, InvalidDataException;

    public boolean isFormatSupported(int tag, int version);
}
