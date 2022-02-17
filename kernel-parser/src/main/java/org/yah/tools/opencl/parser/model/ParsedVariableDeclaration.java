package org.yah.tools.opencl.parser.model;

import org.yah.tools.opencl.parser.type.CLType;

import java.util.Objects;

public class ParsedVariableDeclaration {
    private final String name;
    private final CLAddressSpace addressSpace;
    private final CLType type;

    public ParsedVariableDeclaration(String name, CLAddressSpace addressSpace, CLType type) {
        this.name = Objects.requireNonNull(name, "name is null");
        this.addressSpace = Objects.requireNonNull(addressSpace, "addressSpace is null");
        this.type = Objects.requireNonNull(type, "type is null");
    }

    public String getName() {
        return name;
    }

    public CLAddressSpace getAddressSpace() {
        return addressSpace;
    }

    public CLType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsedVariableDeclaration that = (ParsedVariableDeclaration) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ParsedVariableDeclaration{" +
                "name='" + name + '\'' +
                ", addressSpace=" + addressSpace +
                ", type=" + type +
                '}';
    }
}
