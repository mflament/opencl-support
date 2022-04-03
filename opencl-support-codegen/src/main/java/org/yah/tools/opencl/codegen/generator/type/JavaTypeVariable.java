package org.yah.tools.opencl.codegen.generator.type;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.codegen.builder.JavaTypeBuilder;
import org.yah.tools.opencl.codegen.generator.kernel.AstTypeGenerator;
import org.yah.tools.opencl.codegen.parser.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.codegen.parser.type.CLTypeVariable;

import java.nio.Buffer;
import java.util.*;

public final class JavaTypeVariable {

    /**
     * java type name
     */
    private final CLTypeVariable typeVariable;
    private final List<ParsedKernelArgument> usedByArguments;
    private final String name;
    private final boolean buffer;

    public JavaTypeVariable(CLTypeVariable typeVariable, String name, boolean buffer, List<ParsedKernelArgument> usedByArguments) {
        this.typeVariable = Objects.requireNonNull(typeVariable, "typeVariable is null");
        this.name = Objects.requireNonNull(name, "name is null");
        this.buffer = buffer;
        this.usedByArguments = CLUtils.copyOfList(usedByArguments);
    }

    public String getName() {
        return name;
    }

    public CLTypeVariable getTypeVariable() {
        return typeVariable;
    }

    public boolean isBuffer() {
        return buffer;
    }

    public boolean isValue() {
        return !buffer;
    }

    public List<ParsedKernelArgument> getUsedByArguments() {
        return usedByArguments;
    }

    public boolean isUsedBy(ParsedKernel kernel) {
        return usedByArguments.stream().anyMatch(argument -> argument.getKernel() == kernel);
    }

    public boolean isUsedBy(ParsedKernelArgument argument) {
        return usedByArguments.contains(argument);
    }

    public static boolean isBuffer(CLType type) {
        if (type.isCLTypeVariable())
            type = type.asCLTypeVariable().getReferenceType();
        return type.isPointer() || type.isVector() || type.isUnresolved();
    }

    public TypeParameter createTypeParameter(JavaTypeBuilder typeBuilder) {
        TypeParameter tp;
        if (buffer)
            tp = new TypeParameter(name, NodeList.nodeList(typeBuilder.addImport(Buffer.class)));
        else
            tp = new TypeParameter(name);
        return tp;
    }

    public ClassOrInterfaceType createParameterArgument(Map<String, CLType> typeArguments, JavaTypeBuilder typeBuilder) {
        String clTypeName = typeVariable.getName();
        CLType clType = typeArguments.get(clTypeName);
        if (clType == null)
            throw new NoSuchElementException(clTypeName);

        if (buffer) {
            CLType componentType = clType.getComponentType();
            return AstTypeGenerator.createBufferType(componentType.asScalar(), typeBuilder);
        }

        return AstTypeGenerator.createPrimitiveType(clType.asScalar()).toBoxedType();
    }

    public static Builder builder(CLTypeVariable typeVariable) {
        return new Builder(typeVariable);
    }

    public static class Builder {
        // cl type name
        private final CLTypeVariable typeVariable;
        private final List<ParsedKernelArgument> usedAsBufferByArguments = new ArrayList<>();
        private final List<ParsedKernelArgument> usedAsValueByArguments = new ArrayList<>();
        private boolean buffer;
        private boolean value;

        private Builder(CLTypeVariable typeVariable) {
            this.typeVariable = Objects.requireNonNull(typeVariable, "typeVariable is null");
        }

        public void withArgument(ParsedKernelArgument argument) {
            CLType componentType = argument.getType().getComponentType();
            if (componentType != typeVariable)
                throw new IllegalArgumentException("Invalid argument " + argument);
            boolean b = isBuffer(argument.getType());
            buffer |= b;
            value |= !b;
            if (b)
                usedAsBufferByArguments.add(argument);
            else
                usedAsValueByArguments.add(argument);
        }

        public List<JavaTypeVariable> build() {
            String name = typeVariable.getName();
            if (buffer && value) {
                return Arrays.asList(
                        new JavaTypeVariable(typeVariable, name, false, usedAsValueByArguments),
                        new JavaTypeVariable(typeVariable, name + "B", true, usedAsBufferByArguments)
                );
            } else if (buffer) {
                return Collections.singletonList(new JavaTypeVariable(typeVariable, name, true, usedAsBufferByArguments));
            } else {
                return Collections.singletonList(new JavaTypeVariable(typeVariable, name, false, usedAsValueByArguments));
            }
        }

    }
}
