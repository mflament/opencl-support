package org.yah.tools.opencl.codegen.parser;

import org.yah.tools.opencl.codegen.parser.type.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TypeResolver {

    private static final Pattern VECTOR_TYPE_REGEX = Pattern.compile("(\\w+)(2|3|4|8|16)");

    private final Map<String, CLType> cache = new HashMap<>();

    public CLType resolve(String decl) {
        CLType type = cache.get(decl);
        if (type == null) {
            type = parse(decl);
            cache.put(type.getName(), type);
        }
        return type;
    }

    public Map<String, CLType> resolve(Map<String, String> declarations) {
        Map<String, CLType> results = new LinkedHashMap<>();
        declarations.forEach((name, decl) -> results.put(name, resolve(decl)));
        return results;
    }

    public void registerTypeParamter(CLTypeParameter typeParameter) {
        cache.put(typeParameter.getName(), typeParameter);
    }

    private CLType parse(String decl) {
        boolean isPointer = decl.endsWith("*");
        String name;
        if (isPointer) {
            name = decl.substring(0, decl.length() - 1);
            CLType type = cache.get(name);
            if (type != null) return new PointerType(type);
        } else
            name = decl;

        CLType type = findFirstNotNull(Arrays.asList(
                () -> parseScalar(name),
                () -> parseVector(name),
                () -> parseMemeObject(name),
                () -> new UnresolvedType(name)));

        if (isPointer)
            type = new PointerType(type);

        return type;
    }

    @Nullable
    private MemObjectType parseMemeObject(String name) {
        return MemObjectType.resolve(name);
    }

    @Nullable
    private ScalarDataType parseScalar(String name) {
        return ScalarDataType.resolve(name);
    }

    @Nullable
    public VectorType parseVector(String name) {
        Matcher matcher = VECTOR_TYPE_REGEX.matcher(name);
        if (matcher.matches()) {
            String prefix = matcher.group(1);
            CLType componentType = resolve(prefix);
            return new VectorType(componentType, Integer.parseInt(matcher.group(2)));
        }
        return null;
    }

    private static CLType findFirstNotNull(List<Supplier<CLType>> suppliers) {
        for (Supplier<CLType> supplier : suppliers) {
            CLType value = supplier.get();
            if (value != null) return value;
        }
        throw new NoSuchElementException();
    }

}
