/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
