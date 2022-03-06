package org.yah.tools.opencl.codegen.parser.model.attribute;

import java.util.Objects;

public class UnknownParsedAttribute implements ParsedAttribute {
    private final String attribute;

    public UnknownParsedAttribute(String attribute) {
        this.attribute = Objects.requireNonNull(attribute, "attribute is null");
    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public String toString() {
        return attribute;
    }
}
