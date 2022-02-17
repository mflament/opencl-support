package org.yah.tools.opencl.parser.model;

import org.yah.tools.opencl.parser.type.CLType;

public class ParsedKernelArgument extends ParsedVariableDeclaration {
    private final int index;

    public ParsedKernelArgument(String name, CLAddressSpace addressSpace, CLType type, int index) {
        super(name, addressSpace, type);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "ParsedKernelArgument{" +
                "index=" + index +
                ", name='" + getName() + '\'' +
                ", addressSpace=" + getAddressSpace() +
                ", type=" + getType().getName() +
                "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int index;
        private String name;
        private CLAddressSpace addressSpace = CLAddressSpace.PRIVATE;
        private CLType type;

        private Builder() {
        }

        public Builder withIndex(int index) {
            this.index = index;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAddressSpace(CLAddressSpace addressSpace) {
            this.addressSpace = addressSpace;
            return this;
        }

        public Builder withType(CLType type) {
            this.type = type;
            return this;
        }

        public ParsedKernelArgument build() {
            return new ParsedKernelArgument(name, addressSpace, type, index);
        }
    }
}
