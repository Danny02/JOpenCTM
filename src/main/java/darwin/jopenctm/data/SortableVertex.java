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
public class SortableVertex implements Comparable<SortableVertex>
{

    /**
     * Vertex X coordinate (used for sorting).
     */
    public final float x;
    /**
     * Grid index. This is the index into the 3D space subdivision grid.
     */
    public final int gridIndex;
    /**
     * Original index (before sorting).
     */
    public final int originalIndex;

    public SortableVertex(float x, int gridIndex, int originalIndex)
    {
        this.x = x;
        this.gridIndex = gridIndex;
        this.originalIndex = originalIndex;
    }

    @Override
    public int compareTo(SortableVertex o)
    {
        if (gridIndex != o.gridIndex) {
            return gridIndex - o.gridIndex;
        } else if (x < o.x) {
            return -1;
        } else if (x > o.x) {
            return 1;
        } else {
            return 0;
        }
    }
}
