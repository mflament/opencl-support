package org.yah.tools.opencl.codegen.parser.model.attribute;

public class WorkGroupSizeHint implements ParsedAttribute {
    private final int x,y,z;

    public WorkGroupSizeHint(int x, int y, int z) {
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
        return String.format("work_group_size_hint(%d, %d, %d)", x, y, z);
    }
}
