package org.yah.tools.opencl.codegen.generator.kernel;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import org.yah.tools.opencl.codegen.builder.InterfaceBuilder;
import org.yah.tools.opencl.codegen.builder.InterfaceMethodBuilder;
import org.yah.tools.opencl.codegen.generator.type.JavaTypeVariable;
import org.yah.tools.opencl.codegen.naming.NamingStrategy.KernelNamingStrategy;
import org.yah.tools.opencl.codegen.naming.NamingStrategy.MethodNamingStrategy;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.enums.BufferProperty;
import org.yah.tools.opencl.enums.KernelArgAddressQualifier;
import org.yah.tools.opencl.enums.KernelArgTypeQualifier;

import javax.annotation.Nullable;
import java.util.Objects;

public class ArgumentInterfaceMethodsGenerator {

    private final KernelInterfaceGenerator kernelInterfaceGenerator;
    private final ParsedKernelArgument kernelArgument;

    public ArgumentInterfaceMethodsGenerator(KernelInterfaceGenerator kernelInterfaceGenerator, ParsedKernelArgument kernelArgument) {
        this.kernelInterfaceGenerator = Objects.requireNonNull(kernelInterfaceGenerator, "kernelInterfaceGenerator is null");
        this.kernelArgument = Objects.requireNonNull(kernelArgument, "kernelArgument is null");
    }

    public void generateMethods() {
        CLType type = kernelArgument.getType();
        if (type.isPointer()) {
            generateBufferArgumentMethods();
        } else {
            generateValueArgumentMethods();
        }
    }

    private void generateBufferArgumentMethods() {
        KernelArgAddressQualifier addressQualifier = kernelArgument.getAddressQualifier();
        if (addressQualifier == KernelArgAddressQualifier.LOCAL) {
            // can only set size
            generateLocalBufferMethods();
        } else {
            generateCreateBufferMethods();
            generateWriteBufferMethods();
            if (shouldRead(kernelArgument))
                generateReadBufferMethods();
        }
    }

    private void generateCreateBufferMethods() {
        generateCreateBufferWithSize();
        generateCreateBufferWithBuffer();
    }

    private void generateCreateBufferWithSize() {
        MethodNamingStrategy methodNamingStrategy = namingStrategy().createBufferWithSize(kernelArgument);
        addMethod(methodNamingStrategy.methodName())
                .addParameter(new Parameter(PrimitiveType.longType(), methodNamingStrategy.parameterName(0)))
                .addParameter(new Parameter(addImport(BufferProperty.class), methodNamingStrategy.parameterName(1)).setVarArgs(true))
                .withJavaDoc(kernelArgument.toString())
                .implementedBy(CLKernelGenerator.class, g -> g.implementCreateBufferWithSize(kernelArgument));
    }

    private void generateCreateBufferWithBuffer() {
        MethodNamingStrategy methodNamingStrategy = namingStrategy().createBufferWithBuffer(kernelArgument);
        addMethod(methodNamingStrategy.methodName())
                .addParameter(argumentType(), methodNamingStrategy.parameterName(0))
                .addParameter(new Parameter(addImport(BufferProperty.class), methodNamingStrategy.parameterName(1)).setVarArgs(true))
                .withJavaDoc(kernelArgument.toString())
                .implementedBy(CLKernelGenerator.class, g -> g.implementCreateBufferWithBuffer(kernelArgument));
    }

    private void generateWriteBufferMethods() {
        MethodNamingStrategy methodNamingStrategy = namingStrategy().writeBuffer(kernelArgument);
        addImport(Nullable.class);
        String methodName = methodNamingStrategy.methodName();
        addMethod(methodName)
                .addParameter(argumentType(), methodNamingStrategy.parameterName(0))
                .addParameter(PrimitiveType.longType(), methodNamingStrategy.parameterName(1))
                .addParameter(eventBufferParameter(methodNamingStrategy.parameterName(2)))
                .withJavaDoc(kernelArgument.toString())
                .implementedBy(CLKernelGenerator.class, g -> g.implementWriteBufferMethods(kernelArgument));

        // default writeBuffer
        addMethod(methodName)
                .addParameter(argumentType(), methodNamingStrategy.parameterName(0))
                .withJavaDoc(kernelArgument.toString())
                .setDefault(mbb -> mbb.andReturn("%s(%s, 0L, null);", methodName, mbb.getParameterName(0)));
    }

    private void generateReadBufferMethods() {
        MethodNamingStrategy methodNamingStrategy = namingStrategy().readBuffer(kernelArgument);

        String methodName = methodNamingStrategy.methodName();
        addMethod(methodName)
                .addParameter(argumentType(), methodNamingStrategy.parameterName(0))
                .addParameter(PrimitiveType.longType(), methodNamingStrategy.parameterName(1))
                .addParameter(eventBufferParameter(methodNamingStrategy.parameterName(2)))
                .withJavaDoc(kernelArgument.toString())
                .implementedBy(CLKernelGenerator.class, g -> g.implementReadBufferMethods(kernelArgument));

        // default readBuffer
        addMethod(methodName)
                .addParameter(argumentType(), methodNamingStrategy.parameterName(0))
                .withJavaDoc(kernelArgument.toString())
                .setDefault(mbb -> mbb.andReturn("%s(%s, 0L, null);", methodName, mbb.getParameterName(0)));
    }

    private void generateValueArgumentMethods() {
        MethodNamingStrategy methodNamingStrategy = namingStrategy().setValue(kernelArgument);

        addMethod(methodNamingStrategy.methodName())
                .addParameter(argumentType(), methodNamingStrategy.parameterName(0))
                .withJavaDoc(kernelArgument.toString())
                .implementedBy(CLKernelGenerator.class, g -> g.implementSetValue(kernelArgument));

        CLType type = kernelArgument.getType();
        if (type.isVector() && type.asVector().getSize() <= namingStrategy().maxVectorComponentParameters()) {
            int size = type.asVector().getSize();
            InterfaceMethodBuilder methodBuilder = addMethod(methodNamingStrategy.methodName())
                    .withJavaDoc(kernelArgument.toString())
                    .implementedBy(CLKernelGenerator.class, g -> g.implementSetVectorComponents(kernelArgument));
            methodNamingStrategy = namingStrategy().setSetVectorComponents(kernelArgument);
            CLType componentType = type.asVector().getComponentType();
            for (int i = 0; i < size; i++) {
                methodBuilder.addParameter(createType(componentType), methodNamingStrategy.parameterName(i));
            }
        }
    }

    private void generateLocalBufferMethods() {
        MethodNamingStrategy mns = namingStrategy().setLocalBufferSize(kernelArgument);
        addMethod(mns.methodName())
                .addParameter(new Parameter(PrimitiveType.longType(), mns.parameterName(0)))
                .withJavaDoc(kernelArgument.toString())
                .implementedBy(CLKernelGenerator.class, g -> g.implementLocalBufferMethods(kernelArgument));
    }

    private Parameter eventBufferParameter(String parameterName) {
        return kernelInterfaceGenerator.eventBufferParameter(parameterName);
    }

    private InterfaceBuilder interfaceBuilder() {
        return kernelInterfaceGenerator.getInterfaceBuilder();
    }

    private Type addImport(Class<?> type) {
        return interfaceBuilder().addImport(type);
    }

    private Type argumentType() {
        return createType(kernelArgument.getType());
    }

    private Type createType(CLType type) {
        if (type.getComponentType().isCLTypeVariable()) {
            JavaTypeVariable javaTypeVariable = kernelInterfaceGenerator.getTypeVariables().get(kernelArgument);
            return new TypeParameter(javaTypeVariable.getName());
        }

        return AstTypeGenerator.createType(type, kernelInterfaceGenerator.getInterfaceBuilder());
    }

    private KernelNamingStrategy namingStrategy() {
        return kernelInterfaceGenerator.getKernelNamingStrategy();
    }

    private Type interfaceType() {
        return kernelInterfaceGenerator.interfaceType();
    }

    private InterfaceMethodBuilder addMethod(String methodName) {
        InterfaceBuilder interfaceBuilder = interfaceBuilder();
        return interfaceBuilder
                .addMethod(methodName)
                .setType(interfaceType());
    }

    static boolean shouldRead(ParsedKernelArgument kernelArgument) {
        // if arg is const, device can not write, host do not need to read
        return !kernelArgument.getTypeQualifiers().contains(KernelArgTypeQualifier.CONST);
    }

}
