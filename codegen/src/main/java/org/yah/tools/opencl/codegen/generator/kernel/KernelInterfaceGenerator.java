package org.yah.tools.opencl.codegen.generator.kernel;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import org.apache.commons.lang3.StringUtils;
import org.yah.tools.opencl.cmdqueue.NDRange;
import org.yah.tools.opencl.codegen.generator.*;
import org.yah.tools.opencl.codegen.generator.impl.BlockStmtBuilder;
import org.yah.tools.opencl.codegen.generator.model.old.GeneratedKernelArgument;
import org.yah.tools.opencl.codegen.generator.model.old.GeneratorKernel;
import org.yah.tools.opencl.codegen.parser.model.type.CLType;
import org.yah.tools.opencl.mem.CLMemObject;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.yah.tools.opencl.codegen.generator.impl.CodeGeneratorSupport.*;
import static org.yah.tools.opencl.enums.KernelArgAddressQualifier.CONSTANT;
import static org.yah.tools.opencl.enums.KernelArgAddressQualifier.GLOBAL;

public class KernelInterfaceGenerator {

    public static final ClassOrInterfaceType CLMEMOBJECT_TYPE = new ClassOrInterfaceType(null, CLMemObject.class.getSimpleName());
    public static final ClassOrInterfaceType RANGE_TYPE = new ClassOrInterfaceType(null, NDRange.class.getSimpleName());

    private final NamingStrategy namingStrategy;
    private final String kernelDoc;
    private final CompilationUnit kernelCompilationUnit;
    private final ClassOrInterfaceDeclaration interfaceDeclaration;
    private final Type thisType;

    KernelInterfaceGenerator(NamingStrategy namingStrategy, String packageName, GeneratorKernel kernel) {
        this.namingStrategy = namingStrategy;
        kernelCompilationUnit = new CompilationUnit(packageName);
        String interfaceName = namingStrategy.kernelName(kernel.getParsedKernel());
        interfaceDeclaration = new ClassOrInterfaceDeclaration(PUBLIC_MODIFIERS, true, interfaceName);
        kernelCompilationUnit.addType(interfaceDeclaration);
        thisType = new ClassOrInterfaceType(null, interfaceName);
        kernelDoc = createKernelDoc(kernel);
        interfaceDeclaration.setJavadocComment(kernelDoc);
        kernelCompilationUnit.addImport(NDRange.class);
    }

    CompilationUnit generate(GeneratorKernel kernel) {
        List<GeneratedKernelArgument> arguments = kernel.getArguments();
        arguments.forEach(this::generateSettersAndGetters);
        generateInvokeKernelMethods(arguments);
        generateAwaitMethod();
        extendsAutoCloseable(interfaceDeclaration);
        return kernelCompilationUnit;
    }

    private void generateSettersAndGetters(GeneratedKernelArgument argument) {
        createArgumentSeperatorComment(createSeperatorComment(argument));

        if (argument.isPointer())
            generatePointerArgument(argument);
        else if (argument.isOther())
            generateOtherArgument(argument);
        else if (argument.isScalar() || argument.isVector())
            generateScalarOrVectorArgument(argument);
    }

    private void generatePointerArgument(GeneratedKernelArgument argument) {
        Class<?> bufferClass = argument.getBufferClass();
        if (argument.isAnyAddressQualifier(CONSTANT, GLOBAL)) {
            // create CLMemObject getter/setter
            kernelCompilationUnit.addImport(CLMemObject.class);
            addMethod(argument, MethodKind.SETTER, MethodKind.MEMOBJECT)
                    .addParameter(CLMEMOBJECT_TYPE, ParameterKind.MEMOBJECT);
            addMethod(argument, MethodKind.GETTER, MethodKind.MEMOBJECT)
                    .setType(CLMEMOBJECT_TYPE);

            if (bufferClass != null) {
                kernelCompilationUnit.addImport(bufferClass);
                createBufferMethods(argument, bufferClass, MethodKind.SETTER);
            }

            kernelCompilationUnit.addImport(ByteBuffer.class);
            createBufferMethods(argument, ByteBuffer.class, MethodKind.SETTER);

            if (argument.canRead()) {
                if (bufferClass != null)
                    createBufferMethods(argument, bufferClass, MethodKind.GETTER);
                createBufferMethods(argument, ByteBuffer.class, MethodKind.GETTER);
            }
        }

        // create buffer size/allocate configuration for any address space

        if (bufferClass != null) {
            addMethod(argument, MethodKind.SETTER, MethodKind.ELEMENTS)
                    .addParameter(PrimitiveType.intType(), ParameterKind.BUFFER_ELEMENTS);
        }

        addMethod(argument, MethodKind.SETTER, MethodKind.SIZE)
                .addParameter(PrimitiveType.longType(), ParameterKind.BUFFER_BYTES)
                .setType(thisType);
    }

    private void createBufferMethods(GeneratedKernelArgument argument,
                                     Class<?> bufferClass,
                                     MethodKind... methodKinds) {
        int kinds = MethodKind.ids(methodKinds);
        // create set/getXXX(xxxBuffer buffer, long targetOffset) methods;
        MethodDeclaration methodDeclaration = addMethod(kinds, argument)
                .addParameter(new ClassOrInterfaceType(null, bufferClass.getSimpleName()), ParameterKind.BUFFER)
                .addParameter(PrimitiveType.longType(), ParameterKind.BUFFER_OFFSET)
                .declaration();
        String methodName = methodDeclaration.getNameAsString();
        // create default set/getXXXBuffer(ByteBuffer/xxxBuffer buffer) methods;
        addMethod(kinds, argument)
                .setDefault()
                .addParameter(new ClassOrInterfaceType(null, bufferClass.getSimpleName()), ParameterKind.BUFFER)
                .setBody(bb -> bb.withStatement("return %s(buffer, 0);", methodName));

        // create long set/getXXXAsync(xxxBuffer buffer, long targetOffset) methods;
        kinds |= MethodKind.ASYNC.getId();
        addMethod(kinds, argument)
                .addParameter(new ClassOrInterfaceType(null, bufferClass.getSimpleName()), ParameterKind.BUFFER)
                .addParameter(PrimitiveType.longType(), ParameterKind.BUFFER_OFFSET)
                .setType(PrimitiveType.longType());

        // create default long set/getXXXAsync(xxxBuffer buffer) methods;
        addMethod(kinds, argument)
                .setDefault()
                .addParameter(new ClassOrInterfaceType(null, bufferClass.getSimpleName()), ParameterKind.BUFFER)
                .setType(PrimitiveType.longType())
                .setBody(bb -> bb.withStatement("return %sAsync(buffer, 0);", methodName));
    }

    private void generateScalarOrVectorArgument(GeneratedKernelArgument argument) {
        CLType type = argument.getType();
        int kinds = MethodKind.SETTER.getId();
        Type parserType = argument.getParserType();
        if (type.isVector()) {
            int size = type.asVector().getSize();
            if (size <= 4) {
                MethodDeclarationBuilder method = addMethod(kinds, argument);
                IntStream.range(0, size).forEach(i -> method.addParameter(parserType, ParameterKind.VECTOR_COMPONENT));
            }
            Class<?> bufferClass = argument.getBufferClass();
            if (bufferClass != null) {
                addMethod(argument, MethodKind.SETTER)
                        .addParameter(new ClassOrInterfaceType(null, bufferClass.getSimpleName()), ParameterKind.BUFFER);
            }
            addMethod(argument, MethodKind.SETTER)
                    .addParameter(new ClassOrInterfaceType(null, ByteBuffer.class.getSimpleName()), ParameterKind.BUFFER);
        } else {
            addMethod(argument, MethodKind.SETTER)
                    .addParameter(argument.getParserType(), ParameterKind.VALUE);
        }
    }

    private void generateOtherArgument(GeneratedKernelArgument argument) {
        addMethod(argument, MethodKind.SETTER)
                .addParameter(argument.getParserType(), ParameterKind.ADDRESS)
                .setType(thisType);
    }

    private void generateInvokeKernelMethods(List<GeneratedKernelArgument> arguments) {
        MethodDeclaration invokeDeclaration = addMethod(MethodKind.INVOKE).addRangeParameter().declaration();

        MethodDeclaration asyncInvokeDeclaration = addMethod(MethodKind.INVOKE, MethodKind.ASYNC)
                .addRangeParameter()
                .setType(PrimitiveType.longType())
                .declaration();

        InvokeKernelMethodParameterIterator iterator = newInvokeKernelMethodParameterIterator(arguments);
        while (iterator.hasNext()) {
            Parameter[] parameters = iterator.next();

            addMethod(MethodKind.INVOKE)
                    .setDefault()
                    .addRangeParameter()
                    .addParameters(parameters)
                    .createDefaultInvokeBody(invokeDeclaration, parameters);

            addMethod(MethodKind.INVOKE, MethodKind.ASYNC)
                    .setDefault()
                    .addRangeParameter()
                    .addParameters(parameters)
                    .setType(PrimitiveType.longType())
                    .createDefaultInvokeBody(asyncInvokeDeclaration, parameters);
        }
    }

    private void generateAwaitMethod() {
        addMethod(MethodKind.AWAIT)
                .addParameter(PrimitiveType.longType(), ParameterKind.EVENT)
                .setType(new VoidType());

        addMethod(MethodKind.AWAIT)
                .addParameter(PrimitiveType.longType(), ParameterKind.EVENT, true)
                .setType(new VoidType());
    }


    private MethodDeclarationBuilder addMethod(MethodKind... methodKinds) {
        return addMethod(MethodKind.ids(methodKinds), null);
    }

    private MethodDeclarationBuilder addMethod(GeneratedKernelArgument argument, MethodKind... methodKinds) {
        return addMethod(MethodKind.ids(methodKinds), argument);
    }

    private MethodDeclarationBuilder addMethod(int kinds, @Nullable GeneratedKernelArgument argument) {
        return new MethodDeclarationBuilder(kinds, argument);
    }


    private void createArgumentSeperatorComment(String doc) {
        int pad = (78 - doc.length()) / 2;
        String indent = StringUtils.repeat('*', pad);
        LineComment separatorLineComment = new LineComment(String.format("%s %s %s", indent, doc, indent));
        interfaceDeclaration.addOrphanComment(separatorLineComment);
    }

    private InvokeKernelMethodParameterIterator newInvokeKernelMethodParameterIterator(List<GeneratedKernelArgument> arguments) {
        List<Parameter[]> params = new ArrayList<>();
        for (GeneratedKernelArgument argument : arguments) {
            List<Parameter> argumentParameters = new ArrayList<>();
            if (argument.isPointer()) {
                Class<?> bufferClass = argument.getBufferClass();
                if (argument.isAnyAddressQualifier(CONSTANT, GLOBAL)) {
                    if (bufferClass != null)
                        argumentParameters.add(invokeParameter(bufferClass, argument, ParameterKind.BUFFER));
                    argumentParameters.add(invokeParameter(ByteBuffer.class, argument, ParameterKind.BUFFER));
                } // else can only be LOCAL since private arguments can not be pointer, only size can be set for local pointer

                if (bufferClass != null)
                    argumentParameters.add(invokeParameter(PrimitiveType.intType(), argument, ParameterKind.BUFFER_ELEMENTS));
                argumentParameters.add(invokeParameter(PrimitiveType.longType(), argument, ParameterKind.BUFFER_BYTES));
            } else if (argument.isVector()) {
                Class<?> bufferClass = argument.getBufferClass();
                if (bufferClass != null)
                    argumentParameters.add(invokeParameter(bufferClass, argument, ParameterKind.VALUE));
                argumentParameters.add(invokeParameter(ByteBuffer.class, argument, ParameterKind.VALUE));
            } else if (argument.isOther()) {
                argumentParameters.add(invokeParameter(PrimitiveType.longType(), argument, ParameterKind.ADDRESS));
            } else if (argument.isScalar()) {
                argumentParameters.add(invokeParameter(argument.getParserType(), argument, ParameterKind.VALUE));
            }
            params.add(argumentParameters.toArray(new Parameter[0]));
        }
        return new InvokeKernelMethodParameterIterator(params);
    }

    private Parameter invokeParameter(Class<?> type, GeneratedKernelArgument argument, ParameterKind parameterKind) {
        return invokeParameter(new ClassOrInterfaceType(null, type.getSimpleName()), argument, parameterKind);
    }

    private Parameter invokeParameter(Type type, GeneratedKernelArgument argument, ParameterKind parameterKind) {
        String name = namingStrategy.parameterName(MethodKind.INVOKE.getId(), parameterKind, argument);
        Parameter parameter = new Parameter(type, name);
        parameter.setData(ARGUMENT, argument);
        parameter.setData(PARAMETER_KIND, parameterKind);
        return parameter;
    }

    private static String createKernelDoc(GeneratorKernel kernel) {
        return kernel.getParsedKernel().toString();
    }

    private static String createSeperatorComment(GeneratedKernelArgument argument) {
        return String.format("%2d: %s", argument.getArgIndex(), argument.getParsedArgument());
    }

    private class MethodDeclarationBuilder {
        private final MethodDeclaration declaration;
        private final int methodKinds;
        @Nullable
        private final GeneratedKernelArgument argument;

        public MethodDeclarationBuilder(int methodKinds, @Nullable GeneratedKernelArgument argument) {
            this.methodKinds = methodKinds;
            this.argument = argument;
            String methodName = namingStrategy.methodName(methodKinds, argument);
            declaration = interfaceDeclaration.addMethod(methodName)
                    .removeBody()
                    .setType(thisType);
            if (argument != null) {
                declaration.setJavadocComment(argument.getJavaDoc());
                declaration.setData(ARGUMENT, argument);
            } else if (MethodKind.contains(methodKinds, MethodKind.INVOKE)) {
                declaration.setJavadocComment(kernelDoc);
            }
            declaration.setData(METHOD_KINDS, methodKinds);
        }

        MethodDeclarationBuilder addParameter(Type type, ParameterKind parameterKind) {
            return addParameter(type, parameterKind, false);
        }

        MethodDeclarationBuilder addParameter(Type type, ParameterKind parameterKind, boolean varArgs) {
            String parameterName = namingStrategy.parameterName(methodKinds, parameterKind, argument);
            Parameter parameter = new Parameter(type, parameterName);
            parameter.setData(PARAMETER_KIND, parameterKind);
            parameter.setData(ARGUMENT, argument);
            parameter.setVarArgs(varArgs);
            declaration.addParameter(parameter);
            return this;
        }

        MethodDeclarationBuilder addRangeParameter() {
            declaration.addParameter(new Parameter(RANGE_TYPE, "range"));
            return this;
        }

        MethodDeclarationBuilder setType(Type type) {
            declaration.setType(type);
            return this;
        }

        MethodDeclaration declaration() {
            return declaration;
        }

        MethodDeclarationBuilder setDefault() {
            declaration.setDefault(true);
            return this;
        }

        void setBody(Consumer<BlockStmtBuilder> consumer) {
            declaration.setBody(body(consumer));
        }

        MethodDeclarationBuilder addParameters(Parameter[] parameters) {
            Arrays.stream(parameters).forEach(declaration::addParameter);
            return this;
        }

        void createDefaultInvokeBody(MethodDeclaration invokeDeclaration, Parameter[] parameters) {
            StringBuilder statement = new StringBuilder();
            int methodKinds = invokeDeclaration.getData(METHOD_KINDS);
            boolean async = MethodKind.contains(methodKinds, MethodKind.ASYNC);
            if (async)
                statement.append("return ");

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = invokeDeclaration.getParameters().get(1 + i);
                GeneratedKernelArgument argument = parameters[i].getData(ARGUMENT);
                String setterName = namingStrategy.methodName(methodKinds, argument);
                statement.append(setterName).append("(").append(parameter.getNameAsString()).append(").\n");
            }
            statement.append(invokeDeclaration.getName()).append("(range);");
            body(bb -> bb.withStatement(statement.toString()));
        }

    }

}
