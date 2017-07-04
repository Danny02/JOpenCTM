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

import darwin.jopenctm.io.*;

/**
 * @author daniel
 */
public class Grid {

    /**
     * Axis-aligned bounding box for the grid
     */
    private final Vec3f min, max;
    /**
     * How many divisions per axis (minimum 1).
     */
    private final Vec3i division;

    public static Grid fromStream(CtmInputStream in) throws IOException {
        return new Grid(Vec3f.read(in), Vec3f.read(in), Vec3i.read(in));
    }

    public Grid(Vec3f min, Vec3f max, Vec3i division) {
        this.min = min;
        this.max = max;
        this.division = division;
    }

    public void writeToStream(CtmOutputStream out) throws IOException {
        min.write(out);
        max.write(out);
        division.write(out);
    }

    public boolean checkIntegrity() {
        if (division.getX() < 1) {
            return false;
        }
        if (division.getY() < 1) {
            return false;
        }
        if (division.getZ() < 1) {
            return false;
        }

        if (max.getX() < min.getX()) {
            return false;
        }
        if (max.getY() < min.getY()) {
            return false;
        }
        if (max.getZ() < min.getZ()) {
            return false;
        }

        return true;
    }

    public Vec3f getMin() {
        return min;
    }

    public Vec3f getMax() {
        return max;
    }

    public Vec3i getDivision() {
        return division;
    }

    public Vec3f getSize() {
        return new Vec3f(
                calcSize(max.getX(), min.getX(), division.getX()),
                calcSize(max.getY(), min.getY(), division.getY()),
                calcSize(max.getZ(), min.getZ(), division.getZ())
        );
    }

    private float calcSize(float max, float min, int division) {
        return (max - min) / division;
    }
}
