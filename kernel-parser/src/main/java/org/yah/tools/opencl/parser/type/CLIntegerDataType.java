package org.yah.tools.opencl.parser.type;

import javax.annotation.Nullable;
import java.util.Arrays;

public enum CLIntegerDataType implements CLType {
    CHAR("char"),
    SHORT("short"),
    INT("int"),
    LONG("long");

    private final String clName;

    CLIntegerDataType(String clName) {
        this.clName = clName;
    }

    @Override
    public String getName() {
        return clName;
    }

    @Nullable
    public static CLIntegerDataType resolve(String name) {
        return Arrays.stream(values()).filter(t -> t.clName.equals(name)).findFirst().orElse(null);
    }
}
