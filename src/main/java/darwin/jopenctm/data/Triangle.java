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

/**
 *
 * @author daniel
 */
public class Triangle implements Comparable<Triangle>
{
    int[] elements = new int[3];

    public Triangle(int[] source, int offset)
    {
        System.arraycopy(source, offset, elements, 0, 3);
    }

    public void copyBack(int[] dest, int offset)
    {
        System.arraycopy(elements, 0, dest, offset, 3);
    }

    @Override
    public int compareTo(Triangle o)
    {
        if (elements[0] != o.elements[0]) {
            return elements[0] - o.elements[0];
        } else if (elements[1] != o.elements[1]) {
            return elements[1] - o.elements[1];
        }
        return elements[2] - o.elements[2];
    }

}
