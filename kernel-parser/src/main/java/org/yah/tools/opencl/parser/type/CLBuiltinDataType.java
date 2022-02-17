package org.yah.tools.opencl.parser.type;

import javax.annotation.Nullable;
import java.util.Arrays;

public enum CLBuiltinDataType implements CLType {
    INTPTR("intptr_t"),
    UINTPTR("uintptr_t"),
    PTRDIFF("ptrdiff_t"),
    SIZE("size_t"),
    IMAGE2D("image2d_t"),
    IMAGE3D("image3d_t"),
    SAMPLER("sampler_t"),
    EVENT("event_t");

    private final String clName;

    CLBuiltinDataType(String clName) {
        this.clName = clName;
    }

    @Override
    public String getName() {
        return clName;
    }

    @Nullable
    public static CLBuiltinDataType resolve(String name) {
        return Arrays.stream(values()).filter(t -> t.clName.equals(name)).findFirst().orElse(null);
    }
}
