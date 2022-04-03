package org.yah.tools.opencl.codegen.generator.kernel;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.yah.tools.opencl.codegen.builder.ImplementationsBuilder;
import org.yah.tools.opencl.codegen.builder.MethodBodyGenerator;
import org.yah.tools.opencl.codegen.generator.type.JavaTypeVariable;
import org.yah.tools.opencl.codegen.generator.type.JavaTypeVariables;
import org.yah.tools.opencl.codegen.naming.NamingStrategy.KernelImplementationNamingStrategy;
import org.yah.tools.opencl.codegen.parser.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.codegen.parser.type.ScalarDataType;
import org.yah.tools.opencl.codegen.parser.type.VectorType;
import org.yah.tools.opencl.enums.KernelArgAddressQualifier;
import org.yah.tools.opencl.enums.KernelArgTypeQualifier;
import org.yah.tools.opencl.generated.AbstractGeneratedKernel;
import org.yah.tools.opencl.mem.CLBuffer;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.yah.tools.opencl.codegen.generator.kernel.ArgumentInterfaceMethodsGenerator.shouldRead;

public class CLKernelGenerator {

    private final ParsedKernel kernel;
    private final ImplementationsBuilder<CLKernelGenerator> implementationsBuilder;
    private final KernelImplementationNamingStrategy namingStrategy;
    private final JavaTypeVariables typeVariables;
    private final Map<String, CLType> typeArguments;

    public CLKernelGenerator(ParsedKernel kernel, String basePackage, KernelImplementationNamingStrategy namingStrategy,
                             JavaTypeVariables typeVariables, Map<String, CLType> typeArguments) {
        this.kernel = Objects.requireNonNull(kernel, "kernel is null");
        this.typeVariables = Objects.requireNonNull(typeVariables, "typeVariables is null");
        this.typeArguments = Objects.requireNonNull(typeArguments, "typeArguments is null");
        this.namingStrategy = Objects.requireNonNull(namingStrategy, "namingStrategy is null");
        this.implementationsBuilder = new ImplementationsBuilder<>(namingStrategy.packageName(basePackage),
                namingStrategy.className(), this, this::resolveTypeParameter);
    }

    public CompilationUnit generate(ClassOrInterfaceDeclaration kernelInterfaceDeclaration, ClassOrInterfaceDeclaration programDeclaration) {
        ClassOrInterfaceDeclaration declaration = implementationsBuilder.getDeclaration();
        kernel.getArguments().stream()
                .filter(a -> a.getType().isPointer() && a.getAddressQualifier() != KernelArgAddressQualifier.LOCAL)
                .map(this::createBufferField)
                .forEach(declaration::addMember);

        declaration.addExtendedType(addImport(AbstractGeneratedKernel.class));
        declaration.addConstructor(Modifier.Keyword.PUBLIC)
                .addParameter(addImport(programDeclaration), "program")
                .addParameter(String.class, "kernelName")
                .setBody(new BlockStmt().addStatement("super(program, kernelName);"));

        implementationsBuilder.implementInterface(kernelInterfaceDeclaration);

        return implementationsBuilder.build();
    }

    public ClassOrInterfaceDeclaration getDeclaration() {
        return implementationsBuilder.getDeclaration();
    }

    public CompilationUnit getCompilationUnit() {
        return implementationsBuilder.getCompilationUnit();
    }

    private FieldDeclaration createBufferField(ParsedKernelArgument kernelArgument) {
        ClassOrInterfaceType type = addImport(CLBuffer.class);
        String fieldName = namingStrategy.bufferFieldName(kernelArgument);
        return new FieldDeclaration(NodeList.nodeList(Modifier.privateModifier()), new VariableDeclarator(type, fieldName));
    }

    private ClassOrInterfaceType addImport(Class<?> type) {
        return implementationsBuilder.addImport(type);
    }

    private ClassOrInterfaceType addImport(ClassOrInterfaceDeclaration declaration) {
        return implementationsBuilder.addImport(declaration);
    }

    MethodBodyGenerator implementCreateBufferWithSize(ParsedKernelArgument argument) {
        String fieldName = namingStrategy.bufferFieldName(argument);
        String bufferProperties = shouldRead(argument) ? "DEFAULT_READ_PROPERTIES" : "DEFAULT_WRITE_PROPERTIES";
        return mbb -> mbb.addStatement("%s = closeAndCreate(%d, %s, %s, %s, builder -> builder.build(%s));",
                        fieldName, argument.getArgIndex(), fieldName, mbb.getParameterName(1), bufferProperties, bufferSizeStmt(argument, mbb.getParameterName(0)))
                .andReturn("this");
    }

    MethodBodyGenerator implementCreateBufferWithBuffer(ParsedKernelArgument argument) {
        String fieldName = namingStrategy.bufferFieldName(argument);
        String bufferProperties = shouldRead(argument) ? "DEFAULT_READ_PROPERTIES" : "DEFAULT_WRITE_PROPERTIES";
        return mbb -> mbb.addStatement("%s = closeAndCreate(%d, %s, %s, %s, builder -> builder.build(%s));",
                        fieldName, argument.getArgIndex(), fieldName, mbb.getParameterName(1), bufferProperties,
                        mbb.getParameterName(0))
                .andReturn("this");
    }

    MethodBodyGenerator implementWriteBufferMethods(ParsedKernelArgument argument) {
        String fieldName = namingStrategy.bufferFieldName(argument);
        return mbb -> mbb.addStatement("getCommandQueue().write(%s, %s, %s, null, %s);",
                        mbb.getParameterName(0), fieldName, mbb.getParameterName(1), mbb.getParameterName(2))
                .andReturn("this");
    }

    MethodBodyGenerator implementReadBufferMethods(ParsedKernelArgument argument) {
        String fieldName = namingStrategy.bufferFieldName(argument);
        return mbb -> mbb.addStatement("getCommandQueue().read(%s, %s, %s, null, %s);",
                        fieldName, mbb.getParameterName(0), mbb.getParameterName(1), mbb.getParameterName(2))
                .andReturn("this");
    }

    MethodBodyGenerator implementLocalBufferMethods(ParsedKernelArgument argument) {
        return mbb -> mbb.addStatement("kernel.setArg(%d, %s);", argument.getArgIndex(), mbb.getParameterName(0))
                .andReturn("this");
    }

    MethodBodyGenerator implementSetValue(ParsedKernelArgument argument) {
        return mbb -> mbb.addStatement("kernel.setArg(%d, %s);", argument.getArgIndex(), mbb.getParameterName(0))
                .andReturn("this");
    }

    MethodBodyGenerator implementSetVectorComponents(ParsedKernelArgument argument) {
        VectorType vectorType = argument.getType().asVector();
        return mbb -> {
            String args = IntStream.range(0, vectorType.getSize())
                    .mapToObj(mbb::getParameterName)
                    .collect(Collectors.joining(", "));
            return mbb.addStatement("kernel.setArg(%d, %s);", argument.getArgIndex(), args).andReturn("this");
        };
    }

    MethodBodyGenerator implementInvokeMethod() {
        return mbb -> mbb
                .addStatement("run(%s, %s);", mbb.getParameterName(0), mbb.getParameterName(1))
                .andReturn("this");
    }

    private String bufferSizeStmt(ParsedKernelArgument argument, String parameterName) {
        ScalarDataType componentType = getComponentType(argument);
        int size = getBufferElementSize(componentType.asScalar());
        if (size > 1)
            return parameterName + " * " + size + "L";
        return parameterName;
    }

    private ScalarDataType getComponentType(ParsedKernelArgument argument) {
        CLType componentType = argument.getType().getComponentType();
        if (componentType.isCLTypeVariable()) {
            JavaTypeVariable typeVariable = typeVariables.get(argument);
            componentType = typeArguments.get(typeVariable.getTypeVariable().getName());
            if (componentType == null)
                throw new NoSuchElementException(argument.getType().toString());
            return componentType.getComponentType().asScalar();
        }
        return componentType.asScalar();
    }

    private Optional<Type> resolveTypeParameter(String javaTypeName) {
        return typeVariables.getVariables().stream()
                .filter(v -> v.getName().equals(javaTypeName))
                .findFirst()
                .map(this::createParameterArgument);
    }

    private ClassOrInterfaceType createParameterArgument(JavaTypeVariable javaTypeVariable) {
        return javaTypeVariable.createParameterArgument(typeArguments, implementationsBuilder);
    }

    private static int getBufferElementSize(ScalarDataType dataType) {
        switch (dataType) {
            case SHORT:
            case USHORT:
            case HALF:
                return 2;
            case BOOL:
            case INT:
            case UINT:
            case FLOAT:
                return 4;
            case LONG:
            case ULONG:
            case DOUBLE:
                return 8;
            case SIZE_T:
            case PTRDIFF_T:
            case INTPTR_T:
            case UINTPTR_T:
                // TODO get device address size
                return 8;
            default:
                return 1;
        }

    }

}
