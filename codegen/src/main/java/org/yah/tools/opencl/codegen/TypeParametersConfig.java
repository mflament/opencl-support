package org.yah.tools.opencl.codegen;

import org.yah.tools.opencl.CLUtils;

import java.util.*;
import java.util.stream.Collectors;

public final class TypeParametersConfig {

    private final List<String> names;
    private final List<ParameterTypeArguments> argumentDeclarations;

    private TypeParametersConfig(Collection<String> names, List<List<String>> typeDeclarations) {
        Objects.requireNonNull(names, "macroNames is null");
        Objects.requireNonNull(typeDeclarations, "typeDeclarations is null");
        this.names = Collections.unmodifiableList(new ArrayList<>(names));
        this.argumentDeclarations = Collections.unmodifiableList(typeDeclarations.stream()
                .map(ParameterTypeArguments::new)
                .collect(Collectors.toList()));
    }

    public List<String> getNames() {
        return names;
    }

    public List<ParameterTypeArguments> getArgumentDeclarations() {
        return argumentDeclarations;
    }

    public List<String> getArgumentDeclarations(String name) {
        int index = getIndexOf(name);
        return argumentDeclarations.stream()
                .map(ad -> ad.typeDeclarations.get(index))
                .collect(Collectors.toList());
    }

    public final class ParameterTypeArguments {

        private final List<String> typeDeclarations;

        public ParameterTypeArguments(List<String> typeDeclarations) {
            if (typeDeclarations.size() != names.size())
                throw new IllegalArgumentException("Invalid type declarations " + typeDeclarations + ", expecting names " + names);
            this.typeDeclarations = CLUtils.copyOf(typeDeclarations);
        }

        public Map<String, String> getTypeDeclarations() {
            Map<String, String> res = new LinkedHashMap<>();
            for (int i = 0; i < names.size(); i++)
                res.put(names.get(i), typeDeclarations.get(i));
            return res;
        }

        public String getTypeDeclaration(String macroName) {
            int typeParamIndex = getIndexOf(macroName);
            return typeDeclarations.get(typeParamIndex);
        }
    }

    private int getIndexOf(String macroName) {
        int index = names.indexOf(macroName);
        if (index < 0)
            throw new NoSuchElementException(macroName);
        return index;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Set<String> names;

        private Builder() {
        }

        public Builder addName(String name) {
            if (names == null)
                names = new LinkedHashSet<>();
            names.add(name);
            return this;
        }

        public Builder withNames(String... names) {
            return withNames(Arrays.asList(names));
        }

        public Builder withNames(Collection<String> names) {
            this.names = new LinkedHashSet<>(names);
            return this;
        }

        public BuilderWithNames withTypes(String... declarations) {
            return withTypes(Arrays.asList(declarations));
        }

        public BuilderWithNames withTypes(List<String> declarations) {
            if (names == null || names.isEmpty())
                throw new UnsupportedOperationException("No type parameter names");
            return new BuilderWithNames(names).withTypes(declarations);
        }

    }

    public static final class BuilderWithNames {
        private final Set<String> names;
        private final List<List<String>> declarations = new ArrayList<>();

        private BuilderWithNames(Set<String> names) {
            this.names = new LinkedHashSet<>(names);
        }

        public BuilderWithNames withTypes(String... declarations) {
            return withTypes(Arrays.asList(declarations));
        }

        public BuilderWithNames withTypes(List<String> declarations) {
            this.declarations.add(declarations);
            return this;
        }

        public TypeParametersConfig build() {
            return new TypeParametersConfig(names, declarations);
        }
    }
}
