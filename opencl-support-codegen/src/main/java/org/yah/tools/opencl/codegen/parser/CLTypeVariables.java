package org.yah.tools.opencl.codegen.parser;

import org.yah.tools.opencl.codegen.parser.type.CLType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public final class CLTypeVariables {

    public static final CLTypeVariables EMPTY = new CLTypeVariables(Collections.emptyList(), Collections.emptyList());

    private final List<String> names;
    private final List<String[]> declarations;

    public CLTypeVariables(List<String> names, List<String[]> declarations) {
        this.names = Objects.requireNonNull(names, "names is null");
        this.declarations = Objects.requireNonNull(declarations, "declarations is null");
    }

    public List<String> getNames() {
        return names;
    }

    public String[] getDeclarations(int index) {
        return declarations.get(index);
    }

    public List<Map<String, CLType>> resolve(CLTypeResolver clTypeResolver) {
        if (names.isEmpty())
            return Collections.emptyList();

        return declarations.stream()
                .map(decls -> resolve(decls, clTypeResolver))
                .collect(Collectors.toList());
    }

    private Map<String, CLType> resolve(String[] decls, CLTypeResolver clTypeResolver) {
        Map<String, CLType> res = new LinkedHashMap<>();
        for (int i = 0; i < decls.length; i++) {
            CLType resolved = clTypeResolver.resolve(decls[i]);
            if (!(resolved.isScalar() || resolved.isVector()))
                throw new UnsupportedOperationException("Unhandled type parameter " + resolved + ", only scalar and vetor are supported");
            res.put(names.get(i), resolved);
        }
        return res;
    }

    public boolean isEmpty() {
        return names.isEmpty();
    }

    @Override
    public String toString() {
        return "CLTypeVariables{" +
                "names=" + names +
                ", declarations=" + declarations +
                '}';
    }

    public static Builder builder(String... names) {
        return new Builder(Arrays.asList(names));
    }

    public List<String> getVariableDeclarations(String name) {
        int index = names.indexOf(name);
        if (index < 0)
            return Collections.emptyList();
        return declarations.stream().map(decls -> decls[index]).collect(Collectors.toList());
    }

    public static final class Builder {
        private final List<String> names;
        private final List<String[]> declarations = new ArrayList<>();

        private Builder(List<String> names) {
            this.names = names;
        }

        public Builder withTypes(String... declarations) {
            if (declarations.length != names.size())
                throw new IllegalArgumentException("Invalid declarations " + Arrays.toString(declarations) + ", expecting " + names.size() + " declarations");
            this.declarations.add(declarations);
            return this;
        }

        public Builder withTypes(Map<String, String> declarations) {
            String[] types = names.stream()
                    .map(declarations::get)
                    .filter(Objects::nonNull)
                    .toArray(String[]::new);
            return withTypes(types);
        }

        public CLTypeVariables build() {
            if (names.isEmpty())
                return CLTypeVariables.EMPTY;
            return new CLTypeVariables(names, declarations);
        }
    }

    public static CLTypeVariables parse(@Nullable List<String> declarations) {
        if (declarations == null || declarations.isEmpty())
            return CLTypeVariables.EMPTY;

        Builder builder = new Builder(parseNames(declarations));
        for (String declaration : declarations) {
            builder.withTypes(parseTypeDeclarations(declaration));
        }
        return builder.build();
    }

    private static List<String> parseNames(List<String> declarations) {
        return new ArrayList<>(parseTypeDeclarations(declarations.get(0)).keySet());
    }

    private static Map<String, String> parseTypeDeclarations(String declaration) {
        Map<String, String> map = new LinkedHashMap<>();
        Arrays.stream(declaration.split("[;,]"))
                .map(part -> part.split("="))
                .forEach(parts -> map.put(parts[0], parts[1]));
        return map;
    }

}
