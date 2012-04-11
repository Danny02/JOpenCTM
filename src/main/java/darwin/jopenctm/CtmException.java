/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm;

/**
 *
 * @author daniel
 */
public class CtmException extends Exception
{

    public CtmException(CTMError error)
    {
        super(error.name());
    }
}
