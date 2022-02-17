package org.yah.tools.opencl.parser.type;

public class CLVoidType implements CLType {

    public static final CLVoidType INSTANCE = new CLVoidType();

    private CLVoidType() {}

    @Override
    public String getName() {
        return "void";
    }

    public static CLVoidType resolve(String name) {
        if (name.equals(INSTANCE.getName()))
            return INSTANCE;
        return null;
    }
}
