package org.yah.tools.opencl.codegen.generator.type;

import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.codegen.parser.CLTypeVariables;
import org.yah.tools.opencl.codegen.parser.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.codegen.parser.type.CLTypeVariable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Resolved and expanded {@link CLTypeVariables}.
 * Since the same {@link CLType} parameter can be used as buffer and value, java type parameters can be duplicated.
 * Example:
 * <pre>
 *  macro: T=int
 *
 *  void kernel1(T value) {...}
 *
 *  void kernel2(T* buffer) {...};
 * </pre>
 * Will lead to 2 java type variable: &lt;T, TB&gt;
 */
public final class JavaTypeVariables {

    public static final JavaTypeVariables EMPTY = new JavaTypeVariables(Collections.emptyList());

    private final List<JavaTypeVariable> variables;

    private JavaTypeVariables(List<JavaTypeVariable> variables) {
        this.variables = CLUtils.copyOfList(Objects.requireNonNull(variables, "variables is null"));
    }

    public Collection<JavaTypeVariable> getVariables() {
        return variables;
    }

    public boolean isEmpty() {
        return variables.isEmpty();
    }

    public List<JavaTypeVariable> getKernelVariables(ParsedKernel kernel) {
        return variables.stream().filter(v -> v.isUsedBy(kernel)).collect(Collectors.toList());
    }

    public JavaTypeVariable get(String javaTypeName) {
        return variables.stream()
                .filter(v -> v.getName().equals(javaTypeName))
                .findFirst().orElseThrow(() -> new NoSuchElementException(javaTypeName));
    }

    private static CLType componentType(ParsedKernelArgument argument) {
        return argument.getType().getComponentType();
    }

    public static Builder builder(CLTypeVariables clTypeVariables) {
        return new Builder(clTypeVariables);
    }

    public Optional<JavaTypeVariable> find(ParsedKernelArgument argument) {
        return variables.stream().filter(v -> v.isUsedBy(argument)).findFirst();
    }

    public JavaTypeVariable get(ParsedKernelArgument argument) {
        return find(argument).orElseThrow(() -> new NoSuchElementException(argument.toString()));
    }

    public static final class Builder {
        private final CLTypeVariables clTypeVariables;
        private final Map<String, JavaTypeVariable.Builder> variableBuilders = new HashMap<>();

        private Builder(CLTypeVariables clTypeVariables) {
            this.clTypeVariables = Objects.requireNonNull(clTypeVariables, "clTypeVariables is null");
        }

        public Builder withKernels(Collection<ParsedKernel> kernels) {
            kernels.forEach(this::withKernel);
            return this;
        }

        public Builder withKernel(ParsedKernel kernel) {
            kernel.getArguments().stream()
                    .filter(a -> componentType(a).isCLTypeVariable())
                    .forEach(a -> {
                        CLTypeVariable tv = componentType(a).asCLTypeVariable();
                        JavaTypeVariable.Builder builder = variableBuilders.computeIfAbsent(tv.getName(), n -> JavaTypeVariable.builder(tv));
                        builder.withArgument(a);
                    });
            return this;
        }

        public JavaTypeVariables build() {
            List<JavaTypeVariable> variables = clTypeVariables.getNames().stream()
                    .map(variableBuilders::get)
                    .filter(Objects::nonNull)
                    .flatMap(builder -> builder.build().stream())
                    .collect(Collectors.toList());
            return new JavaTypeVariables(variables);
        }
    }
}
