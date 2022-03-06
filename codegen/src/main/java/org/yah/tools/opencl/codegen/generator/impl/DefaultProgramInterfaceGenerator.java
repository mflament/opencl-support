package org.yah.tools.opencl.codegen.generator.impl;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import org.yah.tools.opencl.codegen.generator.NamingStrategy;
import org.yah.tools.opencl.codegen.generator.ProgramInterfaceGenerator;
import org.yah.tools.opencl.codegen.generator.model.GeneratedKernelInterface;
import org.yah.tools.opencl.codegen.generator.model.GeneratedProgramInterface;
import org.yah.tools.opencl.codegen.generator.model.KernelMethodType;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.codegen.parser.model.type.CLType;
import org.yah.tools.opencl.codegen.parser.model.type.VectorType;
import org.yah.tools.opencl.enums.KernelArgAddressQualifier;
import org.yah.tools.opencl.mem.CLMemObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.yah.tools.opencl.codegen.generator.model.KernelMethodType.*;

public class DefaultProgramInterfaceGenerator implements ProgramInterfaceGenerator {

    private static final Type EVENT_TYPE = PrimitiveType.longType();

    private final NamingStrategy namingStrategy;

    public DefaultProgramInterfaceGenerator(NamingStrategy namingStrategy) {
        this.namingStrategy = Objects.requireNonNull(namingStrategy, "namingStrategy is null");
    }

    @Override
    public GeneratedProgramInterface generate(ParsedProgram program, String packageName) {
        List<GeneratedKernelInterface> kernelInterfaces = program.getKernels().stream()
                .map(pk -> generateKernelInterface(pk, packageName))
                .collect(Collectors.toList());
        return generateProgramInterface(program, packageName, kernelInterfaces);
    }

    private GeneratedProgramInterface generateProgramInterface(ParsedProgram program, String packageName,
                                                               List<GeneratedKernelInterface> kernelInterfaces) {
        GeneratedProgramInterface programInterface = new GeneratedProgramInterface(program, packageName, namingStrategy.programName(program));
        kernelInterfaces.forEach(gki -> generateGetKernelMethod(gki, programInterface));
        implementsAutoCloseable(programInterface);
        return programInterface;
    }

    /**
     * Create the program inrerface method to create the given kernel interface.
     */
    private void generateGetKernelMethod(GeneratedKernelInterface kernelInterface, GeneratedProgramInterface programInterface) {
        programInterface
                .addImport(kernelInterface)
                .addMethod(namingStrategy.getKernelMethodName(kernelInterface))
                .setType(new ClassOrInterfaceType(null, kernelInterface.getNameAsString()))
                .removeBody();
    }

    /**
     * Create the kernel interface declaration.
     */
    private GeneratedKernelInterface generateKernelInterface(ParsedKernel parsedKernel, String programPackageName) {
        GeneratedKernelInterface kernelInterface = new GeneratedKernelInterface(parsedKernel, programPackageName + ".kernels",
                namingStrategy.kernelName(parsedKernel));


        List<List<MethodDeclaration>> argumentMethods = parsedKernel.getArguments().stream()
                .map(argument -> generateKernelArgumentMethods(kernelInterface, argument))
                .collect(Collectors.toList());

        // keep only arguments that can be passed  to invoke method (having at least one setter method with single parameter)
        List<List<MethodDeclaration>> invokeAgumentMethods = argumentMethods.stream()
                .map(methods -> methods.stream()
                        .filter(DefaultProgramInterfaceGenerator::canUserHasInvokeParameter)
                        .collect(Collectors.toList()))
                .filter(methods -> !methods.isEmpty())
                .collect(Collectors.toList());

        generateInvokeMethods(kernelInterface, invokeAgumentMethods, false);
        generateInvokeMethods(kernelInterface, invokeAgumentMethods, true);

        GeneratedKernelInterface.setMethodType(implementsAutoCloseable(kernelInterface), KernelMethodType.CLOSE);

        return kernelInterface;
    }

    public List<MethodDeclaration> generateKernelArgumentMethods(GeneratedKernelInterface kernelInterface, ParsedKernelArgument argument) {
        List<MethodDeclaration> declarations = new ArrayList<>();
        if (argument.isPointer()) {
            if (argument.isAnyAddress(KernelArgAddressQualifier.CONSTANT, KernelArgAddressQualifier.GLOBAL)) {
                declarations.addAll(generateMemObjectMethods(kernelInterface, argument));
                declarations.addAll(generateBufferSetter(kernelInterface, argument));
                if (argument.canRead())
                    declarations.addAll(generateBufferGetter(kernelInterface, argument));
            }
            declarations.addAll(generateBufferSizeSetter(kernelInterface, argument));
        } else {
            declarations.addAll(generateValueSetter(kernelInterface, argument));
        }
        return declarations;
    }

    private Collection<MethodDeclaration> generateMemObjectMethods(GeneratedKernelInterface kernelInterface, ParsedKernelArgument argument) {
        // set CLMemObject
        List<MethodDeclaration> declarations = new ArrayList<>();
        declarations.add(newKernelMethod(kernelInterface, argument, GET_BUFFER).setType(CLMemObject.class).build());
        declarations.add(newKernelMethod(kernelInterface, argument, SET_BUFFER).addParameter(CLMemObject.class).build());
        return declarations;
    }

    private Collection<MethodDeclaration> generateBufferSetter(GeneratedKernelInterface kernelInterface, ParsedKernelArgument argument) {
        List<MethodDeclaration> declarations = new ArrayList<>();
        Class<?> bufferClass = argument.getType().getBufferClass();

        declarations.addAll(generateBufferMethods(kernelInterface, argument, bufferClass, WRITE_BUFFER));
        declarations.addAll(generateBufferMethods(kernelInterface, argument, bufferClass, WRITE_BUFFER_ASYNC));

        if (bufferClass != ByteBuffer.class) {
            declarations.addAll(generateBufferMethods(kernelInterface, argument, ByteBuffer.class, WRITE_BUFFER));
            declarations.addAll(generateBufferMethods(kernelInterface, argument, ByteBuffer.class, WRITE_BUFFER_ASYNC));
        }

        return declarations;
    }

    private Collection<MethodDeclaration> generateBufferGetter(GeneratedKernelInterface kernelInterface, ParsedKernelArgument argument) {
        List<MethodDeclaration> declarations = new ArrayList<>();
        Class<?> bufferClass = argument.getType().getBufferClass();

        declarations.addAll(generateBufferMethods(kernelInterface, argument, bufferClass, READ_BUFFER));
        declarations.addAll(generateBufferMethods(kernelInterface, argument, bufferClass, READ_BUFFER_ASYNC));

        if (bufferClass != ByteBuffer.class) {
            declarations.addAll(generateBufferMethods(kernelInterface, argument, ByteBuffer.class, READ_BUFFER));
            declarations.addAll(generateBufferMethods(kernelInterface, argument, ByteBuffer.class, READ_BUFFER_ASYNC));
        }

        return declarations;
    }

    private List<MethodDeclaration> generateBufferMethods(GeneratedKernelInterface kernelInterface,
                                                          ParsedKernelArgument argument,
                                                          Class<?> bufferClass,
                                                          KernelMethodType methodType) {
        List<MethodDeclaration> declarations = new ArrayList<>();
        MethodDeclaration delegateDeclaration = newKernelMethod(kernelInterface, argument, methodType)
                .addParameter(bufferClass)
                .addParameter(Long.class)
                .build();
        declarations.add(delegateDeclaration);
        declarations.add(newKernelMethod(kernelInterface, argument, methodType)
                .addParameter(bufferClass)
                .blockStmt((md, bb) -> bb.withStatement("return %s(%s, 0L);", delegateDeclaration.getNameAsString(), md.getParameter(0).getNameAsString()))
                .build());
        return declarations;
    }

    private Collection<MethodDeclaration> generateBufferSizeSetter(GeneratedKernelInterface kernelInterface, ParsedKernelArgument argument) {
        List<MethodDeclaration> declarations = new ArrayList<>();
        Class<?> bufferClass = argument.getType().getBufferClass();
        if (bufferClass != ByteBuffer.class)
            declarations.add(newKernelMethod(kernelInterface, argument, ALLOCATE_BUFFER_ELEMENT).addParameter(Integer.TYPE).build());
        declarations.add(newKernelMethod(kernelInterface, argument, ALLOCATE_BUFFER).addParameter(Long.TYPE).build());
        return declarations;
    }

    private Collection<MethodDeclaration> generateValueSetter(GeneratedKernelInterface kernelInterface, ParsedKernelArgument argument) {
        List<MethodDeclaration> declarations = new ArrayList<>();
        CLType type = argument.getType();
        if (type.isScalar() || type.isOther()) {
            Class<?> valueClass = type.getValueClass();
            declarations.add(newKernelMethod(kernelInterface, argument, SET_VALUE).addParameter(valueClass).build());
        } else if (type.isVector()) {
            VectorType vectorType = type.asVector();
            Class<?> valueClass = type.getValueClass();
            int vectorSize = vectorType.getSize();
            if (vectorSize <= 4) {
                KernelArgumentMethodDeclarationBuilder builder = newKernelMethod(kernelInterface, argument, SET_VALUE);
                for (int i = 0; i < vectorSize; i++)
                    builder.addParameter(valueClass);
                declarations.add(builder.build());
            }

            Class<?> bufferClass = vectorType.getBufferClass();
            declarations.add(newKernelMethod(kernelInterface, argument, SET_VALUE).addParameter(bufferClass).build());
            if (bufferClass != ByteBuffer.class)
                declarations.add(newKernelMethod(kernelInterface, argument, SET_VALUE).addParameter(ByteBuffer.class).build());
        } else if (type.isUnresolved()) {
            declarations.add(newKernelMethod(kernelInterface, argument, SET_VALUE).addParameter(ByteBuffer.class).build());
        } else {
            throw new IllegalArgumentException("Invalid type " + type);
        }
        return declarations;
    }

    private void generateInvokeMethods(GeneratedKernelInterface kernelInterface,
                                       List<List<MethodDeclaration>> invokeAgumentMethods,
                                       boolean async) {
        KernelMethodType methodType = async ? INVOKE_ASYNC : INVOKE;
        String mehodName = namingStrategy.getMehodName(methodType, null);
        kernelInterface.addMethod(mehodName).removeBody();

        int argCount = invokeAgumentMethods.size();
        int[] indices = new int[argCount];
        int argIndex = argCount - 1;
        while (argIndex >= 0) {
            MethodDeclaration methodDeclaration = kernelInterface.addMethod(mehodName, Modifier.Keyword.DEFAULT);
            StringBuilder stmtBuilder = new StringBuilder();
            if (async)
                stmtBuilder.append("return ");
            for (int i = 0; i < argCount; i++) {
                MethodDeclaration setterMethod = invokeAgumentMethods.get(i).get(indices[i]);
                Parameter setterParameter = setterMethod.getParameters().get(0);
                ParsedKernelArgument kernelArgument = GeneratedKernelInterface.getKernelArgument(setterMethod);
                String parameterName = namingStrategy.getParameterName(methodType, kernelArgument, i);
                methodDeclaration.addParameter(setterParameter.getType(), parameterName);
                stmtBuilder.append(setterMethod.getNameAsString()).append("(").append(parameterName).append(").");
            }
            stmtBuilder.append(mehodName).append("();");
            methodDeclaration.setBody(BlockStmtBuilder.build(bb -> bb.withStatement(stmtBuilder.toString())));

            while (argIndex >= 0 && ++indices[argIndex] == invokeAgumentMethods.get(argIndex).size()) {
                indices[argIndex] = 0;
                argIndex--;
            }
        }
    }

    private KernelArgumentMethodDeclarationBuilder newKernelMethod(GeneratedKernelInterface kernelInterface,
                                                                   ParsedKernelArgument argument,
                                                                   KernelMethodType methodType) {
        return new KernelArgumentMethodDeclarationBuilder(kernelInterface, argument, methodType);
    }

    private class KernelArgumentMethodDeclarationBuilder {
        private final GeneratedKernelInterface kernelInterface;
        private final ParsedKernelArgument kernelArgument;
        private final KernelMethodType methodType;
        private final MethodDeclaration methodDeclaration;

        public KernelArgumentMethodDeclarationBuilder(GeneratedKernelInterface kernelInterface,
                                                      ParsedKernelArgument kernelArgument,
                                                      KernelMethodType methodType) {
            this.kernelInterface = kernelInterface;
            this.kernelArgument = kernelArgument;
            this.methodType = methodType;
            methodDeclaration = kernelInterface.addMethod(namingStrategy.getMehodName(methodType, kernelArgument));
            methodDeclaration.setType(methodType.isAsync() ? EVENT_TYPE : kernelInterface.getType());
            GeneratedKernelInterface.setKernelArgument(methodDeclaration, kernelArgument);
            GeneratedKernelInterface.setMethodType(methodDeclaration, methodType);
        }

        public KernelArgumentMethodDeclarationBuilder addParameter(Class<?> parameterClass) {
            Type type = kernelInterface.addType(parameterClass);
            int parameterIndex = methodDeclaration.getParameters().size();
            String parameterName = namingStrategy.getParameterName(methodType, kernelArgument, parameterIndex);
            Parameter parameter = new Parameter(type, parameterName);
            methodDeclaration.addParameter(parameter);
            GeneratedKernelInterface.setKernelArgument(parameter, kernelArgument);
            return this;
        }

        public KernelArgumentMethodDeclarationBuilder setType(Class<?> returnClass) {
            methodDeclaration.setType(returnClass);
            return this;
        }

        public MethodDeclaration build() {
            return methodDeclaration;
        }

        public KernelArgumentMethodDeclarationBuilder blockStmt(BiConsumer<MethodDeclaration, BlockStmtBuilder> consumer) {
            methodDeclaration.setDefault(true).setBody(BlockStmtBuilder.build(bb -> consumer.accept(methodDeclaration, bb)));
            return this;
        }

    }

    private static boolean canUserHasInvokeParameter(MethodDeclaration methodDeclaration) {
        return GeneratedKernelInterface.isArgumentSetter(methodDeclaration) && methodDeclaration.getParameters().size() == 1;
    }

    public static MethodDeclaration implementsAutoCloseable(ClassOrInterfaceDeclaration declaration) {
        return declaration.addImplementedType(AutoCloseable.class)
                .addMethod("close").addMarkerAnnotation(Override.class).removeBody()
                .removeBody();
    }

}
