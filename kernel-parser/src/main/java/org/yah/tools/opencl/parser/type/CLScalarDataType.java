package org.yah.tools.opencl.parser.type;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public enum CLScalarDataType implements CLType {
    CHAR("char"),
    UCHAR("uchar"),
    SHORT("short"),
    USHORT("ushort"),
    INT("int"),
    UINT("uint"),
    LONG("long"),
    ULONG("ulong"),
    FLOAT("float");

    private final String clName;

    CLScalarDataType(String clName) {
        this.clName = clName;
    }

    @Override
    public String getName() {
        return clName;
    }

    @Nullable
    public static CLScalarDataType resolve(String name) {
        return Arrays.stream(values()).filter(t -> t.clName.equals(name)).findFirst().orElse(null);
    }
}
