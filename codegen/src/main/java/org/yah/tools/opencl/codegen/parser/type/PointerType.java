package org.yah.tools.opencl.codegen.parser.type;

import java.util.Objects;

public class PointerType implements CLType {

    private final CLType targetType;

    public PointerType(CLType targetType) {
        this.targetType = Objects.requireNonNull(targetType, "targetType is null");
    }

    public CLType getTargetType() {
        return targetType;
    }

    @Override
    public CLType getComponentType() {
        return targetType.getComponentType();
    }

    @Override
    public String getName() {
        return targetType.getName() + "*";
    }

    @Override
    public boolean isPointer() {
        return true;
    }

    @Override
    public PointerType asPointer() {
        return this;
    }

    @Override
    public String toString() {
        return getName();
    }

}
