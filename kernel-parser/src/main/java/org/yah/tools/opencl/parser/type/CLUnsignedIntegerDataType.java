package org.yah.tools.opencl.parser.type;

import javax.annotation.Nullable;
import java.util.Arrays;

public enum CLUnsignedIntegerDataType implements CLType {
    UCHAR("uchar"),
    USHORT("ushort"),
    UINT("uint"),
    ULONG("ulong");

    private final String clName;

    CLUnsignedIntegerDataType(String clName) {
        this.clName = clName;
    }

    @Override
    public String getName() {
        return clName;
    }

    @Nullable
    public static CLUnsignedIntegerDataType resolve(String name) {
        if (name.startsWith("unsigned ")) {
            String signedName = name.substring("unsigned ".length());
            return resolveFromInteger(signedName);
        }
        return Arrays.stream(values())
                .filter(t -> t.clName.equals(name))
                .findFirst().orElse(null);
    }

    @Nullable
    private static CLUnsignedIntegerDataType resolveFromInteger(String signedName) {
        CLIntegerDataType signedDataType = CLIntegerDataType.resolve(signedName);
        if (signedDataType == null)
            return null;
        switch (signedDataType) {
            case CHAR:
                return UCHAR;
            case SHORT:
                return USHORT;
            case INT:
                return UINT;
            case LONG:
                return ULONG;
            default:
                return null;
        }
    }
}
