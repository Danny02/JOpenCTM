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

import java.util.Arrays;

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

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Triangle other = (Triangle) obj;
        if (!Arrays.equals(this.elements, other.elements)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 89 * hash + Arrays.hashCode(this.elements);
        return hash;
    }
}
