package org.yah.tools.opencl.codegen.parser.type;

import java.util.Objects;

public class CLTypeParameter implements CLType {

    private final String name;
    private final CLType referenceType;

    public CLTypeParameter(String name, CLType referenceType) {
        this.name = Objects.requireNonNull(name, "name is null");
        this.referenceType = Objects.requireNonNull(referenceType, "referenceType is null");
    }

    @Override
    public String getName() {
        return name;
    }

    public CLType getReferenceType() {
        return referenceType;
    }

    @Override
    public CLType getComponentType() {
        return this;
    }

    @Override
    public boolean isCLTypeParameter() {
        return true;
    }

    @Override
    public CLTypeParameter asCLTypeParameter() {
        return this;
    }

    @Override
    public String toString() {
        return getName();
    }
}
