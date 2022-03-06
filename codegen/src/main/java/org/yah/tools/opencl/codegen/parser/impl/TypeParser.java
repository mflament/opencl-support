package org.yah.tools.opencl.codegen.parser.impl;

import org.yah.tools.opencl.codegen.parser.model.type.*;

public class TypeParser {

    public CLType parse(String name) {
        boolean ptr = isPointer(name);
        if (ptr)
            name = name.substring(0, name.length() - 1);

        CLType type = VectorType.resolve(name);

        if (type == null)
            type = OtherDataType.resolve(name);

        if (type == null)
            type = ScalarDataType.resolve(name);

        if (type == null)
            type = new UnresolvedType(name);

        if (ptr)
            type = new PointerType(type);

        return type;
    }

    private static boolean isPointer(String name) {
        return name.endsWith("*");
    }


}
