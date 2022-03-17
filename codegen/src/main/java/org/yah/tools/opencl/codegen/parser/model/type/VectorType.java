package org.yah.tools.opencl.codegen.parser.model.type;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VectorType implements CLType {

    private static final Pattern NAME_REGEX = Pattern.compile("(\\w+)(2|3|4|8|16)");

    private final ScalarDataType scalarDataType;
    private final int size;

    public VectorType(ScalarDataType scalarDataType, int size) {
        this.scalarDataType = Objects.requireNonNull(scalarDataType, "scalarDataType is null");
        this.size = size;
    }

    @Override
    public String getName() {
        return scalarDataType.getName() + size;
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

    public ScalarDataType getScalarType() {
        return scalarDataType;
    }

    @Nullable
    public static VectorType resolve(String name) {
        Matcher matcher = NAME_REGEX.matcher(name);
        if (matcher.matches()) {
            String scalarName = matcher.group(1);
            ScalarDataType scalarDataType = ScalarDataType.resolve(scalarName);
            if (scalarDataType != null)
                return new VectorType(scalarDataType, Integer.parseInt(matcher.group(2)));
        }
        return null;
    }

}
