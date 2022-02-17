package org.yah.tools.opencl.parser.type;

public class CLBoolType implements CLType {

    public static final CLBoolType INSTANCE = new CLBoolType();

    private CLBoolType() {}

    @Override
    public String getName() {
        return "bool";
    }

    public static CLBoolType resolve(String name) {
        if (name.equals(INSTANCE.getName()))
            return INSTANCE;
        return null;
    }
}
