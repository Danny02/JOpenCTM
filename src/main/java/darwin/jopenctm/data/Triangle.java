/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
