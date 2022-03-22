package org.yah.tools.opencl.codegen.parser.type;

import javax.annotation.Nullable;
import java.util.Arrays;

public enum MemObjectType implements CLType {
    IMAGE2D("image2d_t"),
    IMAGE3D("image3d_t"),
    IMAGE2D_ARRAY("image2d_array_t"),
    IMAGE1D_T("image1d_t"),
    IMAGE1D_BUFFER("image1d_buffer_t"),
    IMAGE1D_ARRAY("image1d_array_t"),
    SAMPLER("sampler_t"),
    EVENT("event_t");

    private final String clName;

    MemObjectType(String clName) {
        this.clName = clName;
    }

    @Override
    public String getName() {
        return clName;
    }

    @Override
    public CLType getComponentType() {
        return ScalarDataType.LONG;
    }

    @Override
    public boolean isMemObjectType() {
        return true;
    }

    @Override
    public MemObjectType asMemObjectType() {
        return this;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Nullable
    public static MemObjectType resolve(String name) {
        return Arrays.stream(values()).filter(t -> t.clName.equals(name)).findFirst().orElse(null);
    }
}
