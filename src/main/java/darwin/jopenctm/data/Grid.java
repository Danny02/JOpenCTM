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
package darwin.jopenctm.data;

import java.io.IOException;

import darwin.jopenctm.io.CtmInputStream;
import darwin.jopenctm.io.CtmOutputStream;

/**
 *
 * @author daniel
 */
public class Grid
{

    /**
     * Axis-aligned boudning box for the grid
     */
    public final float[] min = new float[3];
    public final float[] max = new float[3];
    /**
     * How many divisions per axis (minimum 1).
     */
    public final int[] division = new int[3];
    /**
     * Size of each grid box.
     */
    public final float[] size = new float[3];

    public void writeToStream(CtmOutputStream out) throws IOException
    {
        for (int i = 0; i < 3; i++) {
            out.writeLittleFloat(min[i]);
        }

        for (int i = 0; i < 3; i++) {
            out.writeLittleFloat(max[i]);
        }
        for (int i = 0; i < 3; i++) {
            out.writeLittleInt(division[i]);
        }
    }

    public void readFromStream(CtmInputStream in) throws IOException
    {
        for (int i = 0; i < 3; i++) {
            min[i] = in.readLittleFloat();
        }
        for (int i = 0; i < 3; i++) {
            max[i] = in.readLittleFloat();
        }
        for (int i = 0; i < 3; i++) {
            division[i] = in.readLittleInt();
        }

        for (int d : division) {
            if (d < 1) {
                throw new IOException("Bad Format");
            }
        }
        for (int i = 0; i < 3; i++) {
            if (max[i] < min[i]) {
                throw new IOException("Bad Format");
            }
        }
        for (int i = 0; i < 3; i++) {
            size[i] = (max[i] - min[i]) / division[i];
        }
    }
}
