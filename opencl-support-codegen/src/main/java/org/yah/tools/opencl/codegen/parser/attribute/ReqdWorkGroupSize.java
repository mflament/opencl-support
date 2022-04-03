package org.yah.tools.opencl.codegen.parser.attribute;

public class ReqdWorkGroupSize implements ParsedAttribute {
    private final int x, y, z;

    public ReqdWorkGroupSize(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    @Override
    public String toString() {
        return String.format("reqd_work_group_size(%d, %d, %d)", x, y, z);
    }
}
