package org.yah.tools.opencl.codegen.parser.type;

import java.util.Objects;

public class CLTypeVariable implements CLType {

    private final String name;
    private final CLType referenceType;

    public CLTypeVariable(String name, CLType referenceType) {
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

    public boolean isVector() {
        return referenceType.isVector();
    }

    public boolean isScalar() {
        return referenceType.isScalar();
    }

    @Override
    public CLType getComponentType() {
        return this;
    }

    @Override
    public boolean isCLTypeVariable() {
        return true;
    }

    @Override
    public CLTypeVariable asCLTypeVariable() {
        return this;
    }

    @Override
    public String toString() {
        return getName();
    }

}
