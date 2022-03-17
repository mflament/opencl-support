package org.yah.tools.opencl.codegen.parser.clinfo;

import org.yah.tools.opencl.codegen.parser.model.type.*;

public class TypeParser {

    public CLType parse(String name) {
        CLType type = VectorType.resolve(name);

        if (type == null)
            type = MemObjectType.resolve(name);

        if (type == null)
            type = ScalarDataType.resolve(name);

        if (type == null)
            type = new UnresolvedType(name);

        return type;
    }

}
