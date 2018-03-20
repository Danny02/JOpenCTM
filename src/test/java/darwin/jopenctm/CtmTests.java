package darwin.jopenctm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    darwin.jopenctm.compression.MeshDecoderTest.class,
    darwin.jopenctm.io.CtmStreamTest.class
})
public class CtmTests {}