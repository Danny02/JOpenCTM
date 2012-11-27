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
package darwin.jopenctm.data;

import java.io.IOException;

import darwin.jopenctm.io.CtmInputStream;
import darwin.jopenctm.io.CtmOutputStream;

/**
 *
 * @author daniel
 */
public class Grid {

    /**
     * Axis-aligned bounding box for the grid
     */
    private final float[] min, max;
    /**
     * How many divisions per axis (minimum 1).
     */
    private final int[] division;

    public static Grid fromStream(CtmInputStream in) throws IOException {
        return new Grid(in.readLittleFloatArray(3),
                        in.readLittleFloatArray(3),
                        in.readLittleIntArray(3));
    }

    public Grid(float[] min, float[] max, int[] division) {
        this.min = min;
        this.max = max;
        this.division = division;
    }

    public void writeToStream(CtmOutputStream out) throws IOException {
        out.writeLittleFloatArray(min);
        out.writeLittleFloatArray(max);
        out.writeLittleIntArray(division);
    }

    public boolean checkIntegrity() {
        if (min.length != 3) {
            return false;
        }
        if (max.length != 3) {
            return false;
        }
        if (division.length != 3) {
            return false;
        }

        for (int d : division) {
            if (d < 1) {
                return false;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (max[i] < min[i]) {
                return false;
            }
        }
        return true;
    }

    public float[] getMin() {
        return min;
    }

    public float[] getMax() {
        return max;
    }

    public int[] getDivision() {
        return division;
    }

    public float[] getSize() {
        float[] size = new float[3];
        for (int i = 0; i < 3; i++) {
            size[i] = (max[i] - min[i]) / division[i];
        }
        return size;
    }
}
