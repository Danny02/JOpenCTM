/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.compression;

import java.io.*;
import junit.framework.TestCase;
import org.apache.log4j.*;

import darwin.geometrie.io.*;
import darwin.geometrie.io.obj.ObjModelReader;

/**
 *
 * @author daniel
 */
public class MeshDecoderTest extends TestCase
{

    public MeshDecoderTest(String testName)
    {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Test of decode method, of class MeshDecoder.
     */
    public void testDecode() throws Exception
    {


        InputStream is = getClass().getResourceAsStream("/Brunnen.obj");
        if(is == null)
            fail("couldn't load model Brunnen");
        ModelReader mr = new ObjModelReader();

        ModelWriter mw = new CtmModelWriter(new RawEncoder());

        File tmpCtm = File.createTempFile("testModel", ".ctm");
        try(OutputStream out = new FileOutputStream(tmpCtm);)
        {
            mw.writeModel(out , mr.readModel(is));
            out.flush();
        }

        System.out.println(tmpCtm.getAbsolutePath());
        System.out.println(tmpCtm.length());

        mr = new CtmModelReader();
        is = new FileInputStream(tmpCtm);
        mr.readModel(is);
    }

    /**
     * Test of getTag method, of class MeshDecoder.
     */
    public void testGetTag()
    {
        System.out.println("getTag");
//        MeshDecoder instance = new MeshDecoderImpl();
//        int expResult = 0;
//        int result = instance.getTag();
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isFormatSupported method, of class MeshDecoder.
     */
    public void testIsFormatSupported()
    {
        System.out.println("isFormatSupported");
        int version = 0;
//        MeshDecoder instance = new MeshDecoderImpl();
//        boolean expResult = false;
//        boolean result = instance.isFormatSupported(version);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public static void main(String[] args) throws Exception
    {
        Logger.getLogger("darwin").addAppender(new ConsoleAppender(new SimpleLayout()));
//     Gerade in ner Marketing Vorlesung gesagt bekommen, dass Leute die mit dem Appel Logo beblitz werden kreativer werden ... ajaaa
        new MeshDecoderTest("").testDecode();
    }
}
