package org.yah.tools.opencl.parser.type;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CLVectorType implements CLType {

    private static final Pattern NAME_REGEX = Pattern.compile("(\\w+)(2|3|4|8|16)");

    private final CLScalarDataType scalarDataType;
    private final int size;

    public CLVectorType(CLScalarDataType scalarDataType, int size) {
        this.scalarDataType = Objects.requireNonNull(scalarDataType, "scalarDataType is null");
        this.size = size;
    }

    public CLScalarDataType getScalarDataType() {
        return scalarDataType;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String getName() {
        return scalarDataType.getName() + size;
    }

    @Nullable
    public static CLVectorType resolve(String name) {
        Matcher matcher = NAME_REGEX.matcher(name);
        if (matcher.matches()) {
            String scalarName = matcher.group(1);
            CLScalarDataType scalarDataType = CLScalarDataType.resolve(scalarName);
            if (scalarDataType != null)
                return new CLVectorType(scalarDataType, Integer.parseInt(matcher.group(2)));
        }
        return null;
    }

}
