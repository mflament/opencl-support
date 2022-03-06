package org.yah.tools.opencl.codegen.generator.kernel;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.PrimitiveType;
import org.apache.commons.lang3.StringUtils;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.codegen.generator.*;
import org.yah.tools.opencl.codegen.generator.impl.BlockStmtBuilder;
import org.yah.tools.opencl.codegen.generator.model.old.GeneratedKernelArgument;
import org.yah.tools.opencl.codegen.generator.model.old.KernelImplementation;
import org.yah.tools.opencl.codegen.generator.model.old.KernelInterface;
import org.yah.tools.opencl.codegen.parser.model.type.CLType;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.CLBuffer;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.yah.tools.opencl.codegen.generator.impl.CodeGeneratorSupport.*;

public class CLKernelImplementationGenerator {

    public KernelImplementation generateImplementation(@Nullable String packageName, KernelInterface kernelInterface) {
        ClassOrInterfaceDeclaration interfaceDeclaration = kernelInterface.getCompilationUnit().getType(0).asClassOrInterfaceDeclaration();
        String interfaceName = interfaceDeclaration.getNameAsString();
        ImplementationBuilder implementationBuilder = new ImplementationBuilder(packageName + ".kernels", "CL" + interfaceName, kernelInterface.getCompilationUnit())
                .addField(CLKernel.class, "kernel", true)
                .addField(CLCommandQueue.class, "commandQueue", true);

        findMethods(interfaceDeclaration, MethodKind.KERNEL_ARGUMENT_MEMOBJECT_GETTER)
                .map(md -> md.getData(ARGUMENT).getJavaName())
                .forEach(name -> implementationBuilder.addField(CLBuffer.class, name, false));

        implementationBuilder.addConstructor()
                .implementMethods((methodDeclaration, blockBuilder) -> implementMethod(kernelInterface, methodDeclaration, blockBuilder));

//        if (readableArguments(kernelInterface).findAny().isPresent()) {
//            implementationBuilder.addImport(CLEventsBuffer.class);
//            implementationBuilder.addImport(BufferProperties.class);
//            implementationBuilder.addMethod("createCLBuffer",
//                    methodDeclaration -> createCLBufferDeclaration(implementationBuilder, methodDeclaration),
//                    Modifier.Keyword.PRIVATE);
//        }
        return new KernelImplementation(kernelInterface, implementationBuilder.build());
    }

    private void createCLBufferDeclaration(ImplementationBuilder implementationBuilder, MethodDeclaration methodDeclaration) {
        methodDeclaration
                .setType(CLBuffer.class.getSimpleName())
                .addParameter(new Parameter(PrimitiveType.intType(), "bytes"))
                .setBody(body(bodyBuilder -> bodyBuilder.withStatement(
                        "return commandQueue.getContext().buildBuffer()\n" +
                                "                .withProperties(BufferProperties.MEM_ALLOC_HOST_PTR, BufferProperties.MEM_HOST_READ_ONLY)\n" +
                                "                .build(bytes);"
                )));
    }

    private void implementMethod(KernelInterface kernelInterface, MethodDeclaration methodDeclaration, BlockStmtBuilder blockBuilder) {
        int argIndex = 0;
        String methodName = methodDeclaration.getNameAsString();
//        if (isCloseMethod(methodDeclaration)) {
//            blockBuilder.withStatement("kernel.close();");
//        } else if (isSetter(methodName)) {
//            implementSetter(kernelInterface, methodDeclaration, blockBuilder);
//        } else if (isGetter(methodName)) {
//            implementGetter(kernelInterface, methodDeclaration, blockBuilder);
//        } else {
//            implementInvoke(kernelInterface, methodDeclaration, blockBuilder);
//        }
    }

    private void implementSetter(KernelInterface kernelInterface, MethodDeclaration methodDeclaration, BlockStmtBuilder builder) {
        GeneratedKernelArgument generatorArgument = getGeneratorArgument(methodDeclaration);
        String args, kernelSetter;
        String methodName = methodDeclaration.getNameAsString();
        String param0Name = methodDeclaration.getParameters().get(0).getNameAsString();
        if (methodDeclaration.containsData(SCALAR_BYTES)) {
            int elementSize = methodDeclaration.getData(SCALAR_BYTES);
            kernelSetter = "setArgSize";
            if (elementSize == 1)
                args = param0Name;
            else
                args = "(long)" + param0Name + " * " + elementSize;
        } else {
            args = methodDeclaration.getParameters().stream()
                    .map(Parameter::getNameAsString)
                    .collect(Collectors.joining(", "));
            kernelSetter = "setArg";
        }

        builder.withStatement("kernel.%s(%d, %s);", kernelSetter, generatorArgument.getArgIndex(), args)
                .withStatement("return this;");
    }

    private void implementGetter(KernelInterface kernelInterface,
                                 MethodDeclaration methodDeclaration,
                                 BlockStmtBuilder builder) {
        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        if (parameters.isEmpty()) {
            String fieldName = StringUtils.uncapitalize(methodDeclaration.getNameAsString().substring(3));
            builder.withStatement("return " + fieldName + ";");
        } else {
            GeneratedKernelArgument generatorArgument = getGeneratorArgument(methodDeclaration);

//            String fieldName = generatorArgument.getParsedArgumentName() + "Buffer";
//            String getElementCount;
//            Parameter parameter = parameters.get(0);
//            Type parameterType = parameter.getType();
//            if (parameterType.isArrayType())
//                getElementCount = parameter.getNameAsString() + ".length";
//            else
//                getElementCount = parameter.getNameAsString() + ".remaining()";
//            ScalarDataType argType = generatorArgument.resolveScalarType();
//            builder.withStatement("if (%s == null)\n %s = createCLBuffer(%s * %d);", fieldName, fieldName, getElementCount, argType.getBytes())
//                    .withStatement("commandQueue.read(%s, %s, true, 0, CLEventsBuffer.EMPTY_PARAM);", fieldName, parameter.getName())
//                    .withStatement("return this;");
        }
    }

    private static int getElementBytes(CLType type) {
        if (type.isPointer())
            type = type.asPointer().getType();

        if (type.isScalar())
            return type.asScalar().getJavaBytes();

        if (type.isVector())
            return type.asVector().getSize() * type.asVector().getScalarType().getJavaBytes();

        return 1;
    }


    private void implementInvoke(KernelInterface kernelInterface, MethodDeclaration methodDeclaration, BlockStmtBuilder builder) {
        String methodName = methodDeclaration.getNameAsString();
        boolean wait = methodName.endsWith("AndWait");
        if (wait) {
            builder.withStatements("long event = commandQueue.run(kernel, range);",
                    "commandQueue.waitForEvent(event);");
        } else {
            builder.withStatement("return commandQueue.run(kernel, range);");
        }
    }

    private static Stream<MethodDeclaration> findMethods(ClassOrInterfaceDeclaration interfaceDeclaration, MethodKind methodKind, MethodKind... additionalTypes) {
        EnumSet<MethodKind> set = EnumSet.of(methodKind, additionalTypes);
        return interfaceDeclaration.getMethods().stream()
                .filter(m -> m.containsData(METHOD_KINDS) && set.contains(m.getData(METHOD_KINDS)));
    }

    private static boolean isSetter(String methodName) {
        return methodName.startsWith("set");
    }

    private static boolean isGetter(String methodName) {
        return methodName.startsWith("get");
    }

    private static GeneratedKernelArgument getGeneratorArgument(MethodDeclaration methodDeclaration) {
        return methodDeclaration.getData(KernelInterfaceGenerator.ARGUMENT);
    }
}
