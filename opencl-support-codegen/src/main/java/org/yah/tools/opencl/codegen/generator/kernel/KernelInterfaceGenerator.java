package org.yah.tools.opencl.codegen.generator.kernel;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.codegen.builder.InterfaceBuilder;
import org.yah.tools.opencl.codegen.generator.ProgramGeneratorRequest;
import org.yah.tools.opencl.codegen.generator.type.JavaTypeVariables;
import org.yah.tools.opencl.codegen.naming.NamingStrategy;
import org.yah.tools.opencl.codegen.naming.NamingStrategy.KernelNamingStrategy;
import org.yah.tools.opencl.codegen.parser.ParsedKernel;
import org.yah.tools.opencl.ndrange.NDRange;

import javax.annotation.Nullable;
import java.util.Objects;

public class KernelInterfaceGenerator {

    private final ParsedKernel parsedKernel;
    private final KernelNamingStrategy kernelNamingStrategy;
    private final InterfaceBuilder interfaceBuilder;
    private final JavaTypeVariables typeVariables;

    public KernelInterfaceGenerator(ParsedKernel parsedKernel,
                                    ProgramGeneratorRequest request,
                                    KernelNamingStrategy kernelNamingStrategy,
                                    JavaTypeVariables typeVariables) {
        this.parsedKernel = Objects.requireNonNull(parsedKernel, "parsedKernel is null");
        this.kernelNamingStrategy = Objects.requireNonNull(kernelNamingStrategy, "kernelNamingStrategy is null");
        Objects.requireNonNull(typeVariables, "typeVariables is null");

        String kernelPackage = kernelNamingStrategy.kernelPackage(request.getBasePackage());
        interfaceBuilder = new InterfaceBuilder(kernelPackage, kernelNamingStrategy.interfaceName());

        request.getKernelInterfaces().stream()
                .map(interfaceBuilder::addImport)
                .forEach(interfaceBuilder::addExtendedType);

        this.typeVariables = Objects.requireNonNull(typeVariables, "typeVariables is null");
        typeVariables.getKernelVariables(parsedKernel).stream()
                .map(v -> v.createTypeParameter(interfaceBuilder))
                .forEach(interfaceBuilder::addTypeParameter);
    }

    public KernelNamingStrategy getKernelNamingStrategy() {
        return kernelNamingStrategy;
    }

    public InterfaceBuilder getInterfaceBuilder() {
        return interfaceBuilder;
    }

    public JavaTypeVariables getTypeVariables() {
        return typeVariables;
    }

    public ClassOrInterfaceDeclaration getDeclaration() {
        return interfaceBuilder.getDeclaration();
    }

    public CompilationUnit getCompilationUnit() {
        return interfaceBuilder.getCompilationUnit();
    }

    public CompilationUnit generate() {
        interfaceBuilder.withJavaDoc(parsedKernel.toString());

        parsedKernel.getArguments().stream()
                .map(a -> new ArgumentInterfaceMethodsGenerator(this, a))
                .forEach(ArgumentInterfaceMethodsGenerator::generateMethods);

        generateInvokeMethods();

        interfaceBuilder.makeAutoCloseable();

        return interfaceBuilder.getCompilationUnit();
    }

    private void generateInvokeMethods() {
        NamingStrategy.MethodNamingStrategy methodNamingStrategy = kernelNamingStrategy.invokeMethod();
        interfaceBuilder.addMethod(methodNamingStrategy.methodName())
                .addParameter(ndRangeParameter(methodNamingStrategy.parameterName(0)))
                .addParameter(eventBufferParameter(methodNamingStrategy.parameterName(1)))
                .implementedBy(CLKernelGenerator.class, CLKernelGenerator::implementInvokeMethod)
                .setType(interfaceType());

        interfaceBuilder.addMethod(methodNamingStrategy.methodName())
                .addParameter(ndRangeParameter(methodNamingStrategy.parameterName(0)))
                .setType(interfaceType())
                .setDefault(mbb -> mbb.andReturn("%s(%s, null);", mbb.getMethodName(), mbb.getParameterName(0)));
    }

    Parameter eventBufferParameter(String name) {
        interfaceBuilder.addImport(Nullable.class);
        return new Parameter(interfaceBuilder.addImport(PointerBuffer.class), name).addMarkerAnnotation(Nullable.class);
    }

    private Parameter ndRangeParameter(String parameterName) {
        return new Parameter(interfaceBuilder.addImport(NDRange.class), parameterName);
    }

    Type interfaceType() {
        return interfaceBuilder.getThisType();
    }

    public KernelNamingStrategy getNamingStrategy() {
        return kernelNamingStrategy;
    }

    public ParsedKernel getParsedKernel() {
        return parsedKernel;
    }

}
