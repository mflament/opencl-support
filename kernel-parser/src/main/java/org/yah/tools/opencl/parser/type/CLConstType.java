package org.yah.tools.opencl.parser.type;

public class CLConstType implements CLType {

    private final CLType type;

    public CLConstType(CLType type) {
        this.type = type;
    }

    public CLType getType() {
        return type;
    }

    @Override
    public String getName() {
        return "const " + type.getName();
    }
}
