/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.compression;

import java.io.IOException;

import darwin.jopenctm.CtmOutputStream;
import darwin.jopenctm.Mesh;

/**
 *
 * @author daniel
 */
public interface MeshEncoder
{

    public void encode(Mesh m, CtmOutputStream out) throws IOException;

    public int getTag();

    public int getFormatVersion();
}
