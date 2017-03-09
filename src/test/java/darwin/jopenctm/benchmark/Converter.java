package darwin.jopenctm.benchmark;

import darwin.geometrie.io.CtmModelWriter;
import darwin.geometrie.io.ModelReader;
import darwin.geometrie.io.ModelWriter;
import darwin.geometrie.io.obj.ObjModelReader;
import darwin.geometrie.unpacked.Model;
import darwin.jopenctm.compression.MG2Encoder;

import java.io.FileOutputStream;

import static java.lang.ClassLoader.getSystemResourceAsStream;

/**
 * Created by Daniel Heinrich on 09/03/2017.
 */
public class Converter {


    public static void main(String... args) throws Exception {
        long time = System.currentTimeMillis();

        ModelReader reader = new ObjModelReader();
        Model[] models = reader.readModel(getSystemResourceAsStream("100kTris.obj"));

        ModelWriter writer = new CtmModelWriter(new MG2Encoder(), "", 9);
        writer.writeModel(new FileOutputStream("test.ctm"), models);

        System.out.println(System.currentTimeMillis() - time);
    }
}
