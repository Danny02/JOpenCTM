package darwin.jopenctm.data;

import darwin.jopenctm.io.CtmInputStream;
import darwin.jopenctm.io.CtmOutputStream;

import java.io.IOException;

public class Vec3i {
    private final int x,y,z;

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vec3i from(int[] data) {
        if(data.length != 3)
        {
            throw new IllegalArgumentException("The data has not the size of 3 but instead: " + data.length);
        }

        return new Vec3i(data[0],data[1],data[2]);
    }

    public static Vec3i read(CtmInputStream in) throws IOException {
        return new Vec3i(in.readLittleInt(),in.readLittleInt(),in.readLittleInt());
    }

    public void write(CtmOutputStream out) throws IOException {
        out.writeLittleInt(x);
        out.writeLittleInt(y);
        out.writeLittleInt(z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
