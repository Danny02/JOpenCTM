package darwin.jopenctm.benchmark;

import darwin.geometrie.io.CtmModelWriter;
import darwin.geometrie.io.ModelReader;
import darwin.geometrie.io.ModelWriter;
import darwin.geometrie.io.WrongFileTypeException;
import darwin.geometrie.io.obj.ObjModelReader;
import darwin.geometrie.unpacked.Model;
import darwin.jopenctm.compression.MG2Encoder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.io.OutputStream;

import static java.lang.ClassLoader.getSystemResourceAsStream;

/**
 * Created by Daniel Heinrich on 09/03/2017.
 */
@State(Scope.Benchmark)
public class Converter {

    public static final OutputStream DUMMY_SINK = new OutputStream() {
        @Override
        public void write(int b) throws IOException {
        }
    };

    private Model[] models;

    @Setup
    public void loadModels() throws IOException, WrongFileTypeException {
        ModelReader reader = new ObjModelReader();
        models = reader.readModel(getSystemResourceAsStream("100kTris.obj"));
    }

    @Benchmark
    public void convertMg2() throws Exception {
        ModelWriter writer = new CtmModelWriter(new MG2Encoder(), "", 9);
        writer.writeModel(DUMMY_SINK, models);
    }

    @Benchmark
    public void convertMg2MemLess() throws Exception {
        ModelWriter writer = new CtmModelWriter(new MG2MemLessEncoder(), "", 9);
        writer.writeModel(DUMMY_SINK, models);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Converter.class.getSimpleName())
                .warmupIterations(10)
                .measurementIterations(5)
                .forks(1)
                .addProfiler(GCProfiler.class)
                .build();

        new Runner(opt).run();
    }
}
