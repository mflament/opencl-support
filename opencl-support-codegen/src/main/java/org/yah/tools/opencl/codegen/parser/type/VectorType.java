package org.yah.tools.opencl.codegen.parser.type;

import java.util.Objects;

public class VectorType implements CLType {

    /**
     * Either a {@link ScalarDataType} or a {@link CLTypeVariable}
     */
    private final CLType componentType;

    private final int size;

    public VectorType(CLType componentType, int size) {
        Objects.requireNonNull(componentType, "componentType is null");
        if (!(componentType.isScalar() || componentType.isCLTypeVariable()))
            throw new IllegalArgumentException("Invaldi component type " + componentType);
        this.componentType = componentType;
        this.size = size;
    }

    @Override
    public CLType getComponentType() {
        return componentType;
    }

    @Override
    public String getName() {
        return componentType.getName() + size;
    }

    @Override
    public boolean isVector() {
        return true;
    }

    @Override
    public VectorType asVector() {
        return this;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return getName();
    }


}
