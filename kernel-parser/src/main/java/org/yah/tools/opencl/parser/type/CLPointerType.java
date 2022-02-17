package org.yah.tools.opencl.parser.type;

public class CLPointerType implements CLType {

    private final CLType type;

    public CLPointerType(CLType type) {
        this.type = type;
    }

    public CLType getType() {
        return type;
    }

    @Override
    public String getName() {
        return type.getName() + "*";
    }
}
