package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.yah.tools.opencl.codegen.NamingStrategy;
import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.methods.CreateBuffer;
import org.yah.tools.opencl.codegen.model.kernel.methods.SetValue;
import org.yah.tools.opencl.codegen.model.kernel.methods.WriteBuffer;
import org.yah.tools.opencl.codegen.model.kernel.param.BufferSize;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.generated.AbstractGeneratedKernel;
import org.yah.tools.opencl.mem.CLBuffer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CLKernelGenerator extends AbstractImplementationGenerator {

    private final ClassOrInterfaceType programType;

    public CLKernelGenerator(CompilationUnit clProgram, CompilationUnit kernelInterface, Map<String, CLType> typeArguments) {
        super(kernelInterface, getPackageName(kernelInterface), getTypeName(kernelInterface), typeArguments);
        programType = addImport(clProgram);
        declaration.addExtendedType(addImport(AbstractGeneratedKernel.class));
    }

    @Override
    public CompilationUnit generate() {
        generateConstructor();
        source.getType(0).getMethods().stream()
                .filter(km -> !km.isDefault())
                .forEach(this::generateMethod);
        return compilationUnit;
    }

    private void generateConstructor() {
        KernelModel kernelModel = getData(source, KERNEL_MODEL);

        List<WriteBuffer> fields = kernelModel.getMethods().stream()
                .filter(KernelMethod::isWriteBuffer)
                .map(KernelMethod::asWriteBuffer)
                .collect(Collectors.toList());
        fields.forEach(f -> declaration.addField(CLBuffer.class, getBufferFieldName(f), Keyword.PRIVATE));

        BlockStmt stmt = new BlockStmt().addStatement("super(program, kernelName);");

        ConstructorDeclaration constructorDeclaration = declaration.addConstructor(Keyword.PUBLIC)
                .addParameter(programType, "program")
                .addParameter(String.class, "kernelName");

        constructorDeclaration.setBody(stmt);
    }

    private void generateMethod(MethodDeclaration md) {
        KernelMethod kernelMethod = md.getData(KERNEL_METHOD);
        int argIndex = kernelMethod.isKernelArgumentMethod() ? kernelMethod.asKernelArgumentMethod().getParsedKernelArgument().getArgIndex() : -1;
        if (kernelMethod.isWriteBuffer()) {
            implementsMethod(md, String.format("getCommandQueue().write(%s, %s, %s, null, %s);",
                    kernelMethod.getParameter(0).getParameterName(),
                    getBufferFieldName(kernelMethod.asKernelArgumentMethod()),
                    kernelMethod.getParameter(1).getParameterName(),
                    kernelMethod.getParameter(2).getParameterName()));
        } else if (kernelMethod.isReadBuffer()) {
            implementsMethod(md, String.format("getCommandQueue().read(%s, %s, %s, null, %s);",
                    getBufferFieldName(kernelMethod.asKernelArgumentMethod()),
                    kernelMethod.getParameter(0).getParameterName(),
                    kernelMethod.getParameter(1).getParameterName(),
                    kernelMethod.getParameter(2).getParameterName()));
        } else if (kernelMethod.isCreateBuffer()) {
            CreateBuffer createBuffer = kernelMethod.asCreateBuffer();
            String bufferFieldName = getBufferFieldName(createBuffer);
            KernelMethodParameter sourceParameter = createBuffer.getParameter(0);
            String source;
            if (sourceParameter.isBufferSize())
                source = createBufferSizeExpr(sourceParameter.asBufferSize());
            else
                source = sourceParameter.getParameterName();
            KernelMethodParameter propsParameter = createBuffer.getParameter(1);
            String defaultProps = sourceParameter.isBufferSize() ? "DEFAULT_READ_PROPERTIES" : "DEFAULT_WRITE_PROPERTIES";
            implementsMethod(md, String.format("%s = closeAndCreate(%d, %s, %s, %s, builder -> builder.build(%s));",
                    bufferFieldName, argIndex, bufferFieldName,
                    propsParameter.getParameterName(), defaultProps, source));
        } else if (kernelMethod.isSetLocalSize()) {
            String size = createBufferSizeExpr(kernelMethod.getParameter(0).asBufferSize());
            implementsMethod(md, String.format("kernel.setArgSize(%d, %s);", argIndex, size));
        } else if (kernelMethod.isSetValue() || kernelMethod.isSetValueComponent()) {
            KernelArgumentMethod kernelArgumentMethod = kernelMethod.asKernelArgumentMethod();
            implementsMethod(md, String.format("kernel.setArg(%d, %s);",
                    kernelArgumentMethod.getParsedKernelArgument().getArgIndex(),
                    parameterNames(md)));
        } else if (kernelMethod.isInvoke()) {
            implementsMethod(md, String.format("run(%s);", parameterNames(md)));
        }
    }

    private String createBufferSizeExpr(BufferSize sourceParameter) {
        CLType bufferType = sourceParameter.getMethod().getParsedKernelArgument().getType();
        int elementSize = getBufferElementSize(bufferType);
        if (elementSize > 1)
            return sourceParameter.getParameterName() + " * " + elementSize + "L";
        return sourceParameter.getParameterName();
    }

    private int getBufferElementSize(CLType type) {
        CLType componentType = type.getComponentType();
        if (componentType.isCLTypeParameter()) {
            componentType = typeArguments.get(componentType.asCLTypeParameter().getName());
            if (componentType == null)
                throw unresolvedTypeError(type);
        }

        if (!componentType.isScalar())
            throw invalidTypeError(type);

        return getScalarSize(componentType.asScalar());
    }

    private void implementsMethod(MethodDeclaration md, String stmt) {
        super.implementMethod(md).setBody(new BlockStmt().addStatement(stmt).addStatement("return this;"));
    }

    private static String parameterNames(MethodDeclaration md) {
        return md.getParameters().stream().map(Parameter::getNameAsString).collect(Collectors.joining(", "));
    }

    private String getBufferFieldName(KernelArgumentMethod method) {
        NamingStrategy namingStrategy = getData(source, PROGRAM_MODEL).getNamingStrategy();
        return namingStrategy.fieldName(method.getParsedKernelArgument());
    }

    private static String getPackageName(CompilationUnit kernelInterface) {
        String basePackage = getData(kernelInterface, PROGRAM_MODEL).getBasePackage();
        return getPackageName(basePackage);
    }

    private static String getTypeName(CompilationUnit kernelInterface) {
        return getTypeName(getData(kernelInterface, KERNEL_MODEL).getName());
    }

    public static String getPackageName(String basePackage) {
        return basePackage + ".cl.kernels";
    }

    public static String getTypeName(String interfaceName) {
        return "CL" + interfaceName;
    }

}
