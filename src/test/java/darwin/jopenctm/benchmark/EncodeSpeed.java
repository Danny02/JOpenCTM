package darwin.jopenctm.benchmark;

import darwin.jopenctm.compression.MG1Encoder;
import darwin.jopenctm.data.AttributeData;
import darwin.jopenctm.data.Mesh;
import darwin.jopenctm.errorhandling.BadFormatException;
import darwin.jopenctm.errorhandling.InvalidDataException;
import darwin.jopenctm.io.CtmFileReader;
import darwin.jopenctm.io.CtmOutputStream;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import static darwin.jopenctm.data.AttributeData.STANDARD_UV_PRECISION;

/**
 * Created by Daniel Heinrich on 06/03/2017.
 */
public class EncodeSpeed {
    private static final int SMALL = 10;
    private static final int SMALL_RND = calcRndSize(SMALL);

    private static final Mesh mesh = randomMesh(1000);

    private static int calcRndSize(int qubeSize) {
        return 6 * qubeSize * qubeSize + 12 * qubeSize + 8;
    }

    public static class GlobalState {
        MG1Encoder mg1Encoder = new MG1Encoder();
        CtmOutputStream out = new CtmOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        });
    }

    public void encode(GlobalState state) throws IOException {
        state.mg1Encoder.encode(mesh, state.out);
    }

    private static Mesh randomMesh(int vertexCount) {
        Random rnd = new Random(1337);

        int[] indices = new int[vertexCount * 3];
        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] uv = new float[vertexCount * 2];

        for (int i = 0; i < indices.length; i++) {
            indices[i] = rnd.nextInt(vertexCount);
            vertices[i] = rnd.nextFloat();
            normals[i] = rnd.nextFloat();
        }
        for (int i = 0; i < uv.length; i++) {
            uv[i] = rnd.nextFloat();
        }

        AttributeData[] uvAttr = new AttributeData[]{
                new AttributeData("uv1", "test", STANDARD_UV_PRECISION, uv)
        };

        Mesh mesh = new Mesh(vertices, normals, indices, uvAttr, new AttributeData[0]);
        try {
            mesh.checkIntegrity();
        } catch (InvalidDataException e) {
            throw new RuntimeException(e);
        }
        return mesh;
    }

    public static void main(String... args) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("medium.ctm").toURI()));

        for (int i = 0; i < 1000; i++) {
            System.currentTimeMillis();
            blackhole(read(data));
        }

        int count = 15000;
        long time = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            blackhole(read(data));
        }
        System.out.println((System.currentTimeMillis() - time) / (float)count);
    }

    private static void blackhole(Object o){}

    private static Mesh read(byte[] data) throws Exception {
        CtmFileReader reader = new CtmFileReader(new ByteArrayInputStream(data));
        return reader.decode();
    }
}
