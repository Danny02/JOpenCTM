/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.compression;

import java.io.IOException;

import darwin.jopenctm.*;

import static darwin.jopenctm.CtmFileReader.*;

/**
 *
 * @author daniel
 */
public interface MeshDecoder
{

    public static final int INDX = getTagInt("INDX");
    public static final int VERT = getTagInt("VERT");
    public static final int NORM = getTagInt("NORM");
    public static final int TEXC = getTagInt("TEXC");
    public static final int ATTR = getTagInt("ATTR");

    public Mesh decode(MeshInfo minfo, CtmInputStream in) throws IOException;

    public boolean isFormatSupported(int tag, int version);

}
