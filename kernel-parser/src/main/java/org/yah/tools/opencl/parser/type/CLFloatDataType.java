package org.yah.tools.opencl.parser.type;

import javax.annotation.Nullable;
import java.util.Arrays;

public enum CLFloatDataType implements CLType {
    FLOAT("float"),
    HALF("half");

    private final String clName;

    CLFloatDataType(String clName) {
        this.clName = clName;
    }

    @Override
    public String getName() {
        return clName;
    }

    @Nullable
    public static CLFloatDataType resolve(String name) {
        return Arrays.stream(values()).filter(t -> t.clName.equals(name)).findFirst().orElse(null);
    }
}
