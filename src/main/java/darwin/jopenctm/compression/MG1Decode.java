/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.compression;

import java.io.IOException;

import darwin.annotations.ServiceProvider;
import darwin.jopenctm.*;

import static darwin.jopenctm.CtmFileReader.*;

/**
 *
 * @author daniel
 */
@ServiceProvider(MeshDecoder.class)
public class MG1Decode implements MeshDecoder
{

    public static final int MG1_TAG = getTagInt("MG1\0");
    public static final int FORMAT_VERSION = 5;

    @Override
    public Mesh decode(MeshInfo minfo, CtmInputStream in) throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTag()
    {
        return MG1_TAG;
    }

    @Override
    public boolean isFormatSupported(int version)
    {
        return version == FORMAT_VERSION;
    }
}
