package darwin.jopenctm.data;

import darwin.jopenctm.io.CtmInputStream;
import darwin.jopenctm.io.CtmOutputStream;

import java.io.IOException;

public class Vec3f {
    private final float x,y,z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vec3f from(float[] data) {
        if(data.length != 3)
        {
            throw new IllegalArgumentException("The data has not the size of 3 but instead: " + data.length);
        }

        return new Vec3f(data[0],data[1],data[2]);
    }

    public static Vec3f read(CtmInputStream in) throws IOException {
        return new Vec3f(in.readLittleFloat(),in.readLittleFloat(),in.readLittleFloat());
    }

    public void write(CtmOutputStream out) throws IOException {
        out.writeLittleFloat(x);
        out.writeLittleFloat(y);
        out.writeLittleFloat(z);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
