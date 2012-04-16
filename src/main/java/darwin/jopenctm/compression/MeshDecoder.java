/*
 * Copyright (C) 2012 daniel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.jopenctm.compression;

import java.io.IOException;

import darwin.jopenctm.data.Mesh;
import darwin.jopenctm.errorhandling.BadFormatException;
import darwin.jopenctm.errorhandling.InvalidDataException;
import darwin.jopenctm.io.CtmInputStream;
import darwin.jopenctm.io.MeshInfo;

import static darwin.jopenctm.io.CtmFileReader.*;

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
