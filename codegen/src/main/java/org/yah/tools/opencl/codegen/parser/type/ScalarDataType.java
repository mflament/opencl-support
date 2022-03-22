package org.yah.tools.opencl.codegen.parser.type;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public enum ScalarDataType implements CLType {
    BOOL, // bool can not be passed as kernel argument so should never be used
    CHAR,
    UCHAR("unsigned char"),
    SHORT,
    USHORT("unsigned short"),
    INT,
    UINT("unsigned int"),
    LONG,
    ULONG("unsigned long"),
    FLOAT,
    DOUBLE,
    HALF,
    SIZE_T,
    PTRDIFF_T,
    INTPTR_T,
    UINTPTR_T,
    VOID;

    private final Set<String> clNames;

    ScalarDataType(String... otherNames) {
        Set<String> names = new LinkedHashSet<>();
        names.add(name().toLowerCase());
        names.addAll(Arrays.asList(otherNames));
        clNames = Collections.unmodifiableSet(names);
    }

    @Override
    public String getName() {
        return clNames.iterator().next();
    }

    @Override
    public ScalarDataType getComponentType() {
        return this;
    }

    @Override
    public boolean isScalar() {
        return true;
    }

    @Override
    public ScalarDataType asScalar() {
        return this;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Nullable
    public static ScalarDataType resolve(String name) {
        return Arrays.stream(values()).filter(t -> t.clNames.contains(name)).findFirst().orElse(null);
    }
}
