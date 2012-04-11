/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
