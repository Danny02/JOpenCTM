package darwin.jopenctm.io;

import java.io.*;
import java.util.Random;

import org.junit.*;

import static org.junit.Assert.*;

/**
 *
 * @author daniel
 */
public class CtmStreamTest {

    private CtmOutputStream output;
    private ByteArrayOutputStream bout;

    @Before
    public void setUp() {
        bout = new ByteArrayOutputStream();
        output = new CtmOutputStream(bout);
    }

    public CtmInputStream getInputStream() {
        return new CtmInputStream(new ByteArrayInputStream(bout.toByteArray()));
    }

    /**
     * Test of readString method, of class CtmInputStream.
     */
    @Test
    public void testString() throws Exception {
        String test = "Dies ist ein Test String!";
        output.writeString(test);
        assertEquals(test, getInputStream().readString());
    }

    /**
     * Test of readLittleInt method, of class CtmInputStream.
     */
    @Test
    public void testLittleInt() throws Exception {
        int[] test = new int[]{-2, 0, 30, Integer.MAX_VALUE, Integer.MIN_VALUE};
        output.writeLittleIntArray(test);

        assertArrayEquals(test, getInputStream().readLittleIntArray(test.length));
    }

    /**
     * Test of readLittleFloat method, of class CtmInputStream.
     */
    @Test
    public void testLittleFloat() throws Exception {
        float[] test = new float[]{-2, 0, 30, Float.MAX_VALUE, Float.MIN_VALUE};
        output.writeLittleFloatArray(test);

        assertArrayEquals(test, getInputStream().readLittleFloatArray(test.length), 0f);
    }

    /**
     * Test of readPackedInts method, of class CtmInputStream.
     */
    @Test
    public void testPackedInts() throws Exception {
        Random rnd = new Random(13372);
        int[] data = new int[3 * rnd.nextInt(1000)];
        for (int i = 0; i < data.length; i++) {
            data[i] = rnd.nextInt();
        }

        output.writePackedInts(data, data.length / 3, 3, true);
        assertArrayEquals(data, getInputStream().readPackedInts(data.length / 3, 3, true));
    }

    @Test
    public void testPackedInts2() throws Exception {
        int[] data = new int[]{0, 1, 2, 0, 2, 3};

        output.writePackedInts(data, data.length / 3, 3, false);
        assertArrayEquals(data, getInputStream().readPackedInts(data.length / 3, 3, false));
    }

    /**
     * Test of readPackedFloats method, of class CtmInputStream.
     */
    @Test
    public void testPackedFloats() throws Exception {
        Random rnd = new Random(1337);
        float[] data = new float[3 * rnd.nextInt(1000)];
        for (int i = 0; i < data.length; i++) {
            data[i] = rnd.nextFloat();
        }

        output.writePackedFloats(data, data.length / 3, 3);
        assertArrayEquals(data, getInputStream().readPackedFloats(data.length / 3, 3), 0);
    }
    
    @Test
    public void testInterleaving()
    {
        Random rnd = new Random(13372);
        int[] data = new int[3 * rnd.nextInt(1000)];
        for (int i = 0; i < data.length; i++) {
            data[i] = rnd.nextInt();
        }
        
        byte[] tmp = new byte[4];
        for(int d : data)
        {
            CtmOutputStream.interleavedInsert(d, tmp, 0, 1);
            int val = CtmInputStream.interleavedRetrieve(tmp, 0, 1);
            assertEquals(val, d);
        }        
    }

    @Test
    public void testCompression() throws IOException {        
        Random rnd = new Random(13372);
        byte[] data = new byte[3*rnd.nextInt(10000)];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) -(rnd.nextInt(255) - 128);
        }

        output.writeCompressedData(data);

        assertArrayEquals(data, getInputStream().readCompressedData(data.length));
    }
}
