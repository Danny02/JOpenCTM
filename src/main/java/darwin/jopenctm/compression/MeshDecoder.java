/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.compression;

import java.io.IOException;

import darwin.jopenctm.CtmInputStream;
import darwin.jopenctm.Mesh;

/**
 *
 * @author daniel
 */
public interface MeshDecoder
{

    public static final int INDX = 'I' | ('N' << 8) | ('D' << 16) | ('X' << 24);
    public static final int VERT = 'V' | ('E' << 8) | ('R' << 16) | ('T' << 24);
    public static final int NORM = 'N' | ('O' << 8) | ('R' << 16) | ('M' << 24);
    public static final int TEXC = 'T' | ('E' << 8) | ('X' << 16) | ('C' << 24);
    public static final int ATTR = 'A' | ('T' << 8) | ('T' << 16) | ('R' << 24);

    public Mesh decode(int triangleCount, int vertexCount, CtmInputStream in) throws IOException;

    public int getTag();
}
