package org.yah.tools.opencl.codegen.parser.model.type;

import org.lwjgl.PointerBuffer;

import javax.annotation.Nullable;
import java.nio.*;
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
    public boolean isScalar() {
        return true;
    }

    @Override
    public ScalarDataType asScalar() {
        return this;
    }

    public Class<?> getValueClass() {
        switch (this) {
            case BOOL:
                return Boolean.TYPE;
            case CHAR:
            case UCHAR:
            case VOID:
                return Byte.TYPE;
            case SHORT:
            case USHORT:
            case HALF:
                return Short.TYPE;
            case INT:
            case UINT:
                return Integer.TYPE;
            case LONG:
            case ULONG:
            case SIZE_T:
            case PTRDIFF_T:
            case INTPTR_T:
            case UINTPTR_T:
                return Long.TYPE;
            case FLOAT:
                return Float.TYPE;
            case DOUBLE:
                return Double.TYPE;
            default:
                throw new IllegalStateException("Unhandled scalar type " + this);
        }
    }

    public Class<?> getBufferClass() {
        switch (this) {
            case SHORT:
            case USHORT:
            case HALF:
                return ShortBuffer.class;
            case INT:
            case UINT:
                return IntBuffer.class;
            case LONG:
            case ULONG:
                return LongBuffer.class;
            case SIZE_T:
            case PTRDIFF_T:
            case INTPTR_T:
            case UINTPTR_T:
                return PointerBuffer.class;
            case FLOAT:
                return FloatBuffer.class;
            case DOUBLE:
                return DoubleBuffer.class;
            default:
                return ByteBuffer.class;
        }
    }

    public int getJavaBytes() {
        switch (this) {
            case SHORT:
            case USHORT:
            case HALF:
                return 2;
            case INT:
            case UINT:
            case FLOAT:
                return 4;
            case LONG:
            case ULONG:
            case DOUBLE:
            case SIZE_T:
            case PTRDIFF_T:
            case INTPTR_T:
            case UINTPTR_T:
                return 8;
            default:
                return 1;
        }
    }

    @Nullable
    public static ScalarDataType resolve(String name) {
        return Arrays.stream(values()).filter(t -> t.clNames.contains(name)).findFirst().orElse(null);
    }
}
