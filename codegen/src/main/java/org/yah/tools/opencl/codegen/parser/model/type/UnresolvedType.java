package org.yah.tools.opencl.codegen.parser.model.type;

import java.nio.ByteBuffer;
import java.util.Objects;

public class UnresolvedType implements CLType {

    private final String name;

    public UnresolvedType(String name) {
        this.name = Objects.requireNonNull(name, "name is null");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isUnresolved() {
        return true;
    }

    @Override
    public Class<?> getValueClass() {
        throw new UnsupportedOperationException("No value type for UnresolvedType");
    }

    @Override
    public Class<?> getBufferClass() {
        return ByteBuffer.class;
    }

    @Override
    public String toString() {
        return "CLUnresolvedType{" +
                "name='" + name + '\'' +
                '}';
    }
}
