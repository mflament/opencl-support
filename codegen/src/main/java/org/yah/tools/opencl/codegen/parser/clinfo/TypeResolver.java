package org.yah.tools.opencl.codegen.parser.clinfo;

import org.yah.tools.opencl.codegen.parser.model.type.CLType;

import java.util.HashMap;
import java.util.Map;

public class TypeResolver {

    private final Map<String, CLType> cache = new HashMap<>();
    private final TypeParser parser;

    public TypeResolver() {
        this.parser = new TypeParser();
    }

    public CLType resolve(String name) {
        CLType type = cache.get(name);
        if (type == null) {
            type = parser.parse(name);
            cache.put(type.getName(), type);
        }
        return type;
    }



}
