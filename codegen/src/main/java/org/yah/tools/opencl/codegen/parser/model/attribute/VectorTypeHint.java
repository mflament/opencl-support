package org.yah.tools.opencl.codegen.parser.model.attribute;

import org.yah.tools.opencl.codegen.parser.model.type.CLType;

import java.util.Objects;

public class VectorTypeHint implements ParsedAttribute {
    private final CLType type;

    public VectorTypeHint(CLType type) {
        this.type = Objects.requireNonNull(type, "type is null");
    }

    public CLType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("vec_type_hint(<%s>)", type);
    }
}
