package org.yah.tools.opencl.codegen.generator.model;

import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;

import javax.annotation.Nullable;

public class GeneratedKernelInterface extends GeneratedType<ParsedKernel, GeneratedKernelInterface> {

    private final ClassOrInterfaceType type;

    public GeneratedKernelInterface(ParsedKernel generatedFrom, String packageName, String simpleName) {
        super(generatedFrom, packageName, simpleName, true);
        type = new ClassOrInterfaceType(null, simpleName);
    }

    public ClassOrInterfaceType getType() {
        return type;
    }

    @Override
    protected GeneratedKernelInterface getThis() {
        return this;
    }

    public static boolean isArgumentSetter(MethodDeclaration methodDeclaration) {
        KernelMethodType methodType = GeneratedKernelInterface.getMethodType(methodDeclaration);
        return methodType == KernelMethodType.WRITE_BUFFER
                || methodType == KernelMethodType.WRITE_BUFFER_ASYNC
                || methodType == KernelMethodType.SET_VALUE
                || methodType == KernelMethodType.ALLOCATE_BUFFER;
    }

    public static KernelMethodType getMethodType(MethodDeclaration methodDeclaration) {
        return methodDeclaration.getData(METHOD_TYPE);
    }

    public static void setMethodType(MethodDeclaration methodDeclaration, KernelMethodType methodType) {
        methodDeclaration.setData(METHOD_TYPE, methodType);
    }

    @Nullable
    public static ParsedKernelArgument getKernelArgument(Node node) {
        return node.containsData(KERNEL_ARGUMENT) ? node.getData(KERNEL_ARGUMENT) : null;
    }

    public static void setKernelArgument(Node node, ParsedKernelArgument parameterType) {
        node.setData(KERNEL_ARGUMENT, parameterType);
    }

    private static final DataKey<KernelMethodType> METHOD_TYPE = new DataKey<KernelMethodType>() {
    };

    private static final DataKey<ParsedKernelArgument> KERNEL_ARGUMENT = new DataKey<ParsedKernelArgument>() {
    };

}
