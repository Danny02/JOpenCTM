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

    int INDX = getTagInt("INDX");
    int VERT = getTagInt("VERT");
    int NORM = getTagInt("NORM");
    int TEXC = getTagInt("TEXC");
    int ATTR = getTagInt("ATTR");

    Mesh decode(MeshInfo minfo, CtmInputStream in) throws IOException, BadFormatException, InvalidDataException;

    boolean isFormatSupported(int tag, int version);
}
