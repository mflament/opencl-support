package org.yah.tools.opencl.codegen.generator;

import java.util.Objects;

public class GeneratedTypeParameter {
    /**
     * java type name
     */
    private final String name;

    private final boolean isPointer;

    public GeneratedTypeParameter(String name, boolean isPointer) {
        this.name = Objects.requireNonNull(name, "name is null");
        this.isPointer = isPointer;
    }

    public String getName() {
        return name;
    }

    public boolean isPointer() {
        return isPointer;
    }

    @Override
    public String toString() {
        return "GeneratedTypeParameter{" +
                "name='" + name + '\'' +
                ", isPointer=" + isPointer +
                '}';
    }
}
