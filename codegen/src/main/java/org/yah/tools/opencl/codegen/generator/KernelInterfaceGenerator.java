package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.SetKernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.methods.Invoke;
import org.yah.tools.opencl.codegen.model.kernel.param.EventBuffer;
import org.yah.tools.opencl.codegen.model.kernel.param.InvokeRangeParameter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class KernelInterfaceGenerator extends AbstractCodeGenerator<KernelModel> {

    public KernelInterfaceGenerator(KernelModel kernelModel) {
        super(kernelModel, kernelModel.getProgramModel().getBasePackage() + ".kernels", kernelModel.getName());
        declaration.setInterface(true);
        declaration.setJavadocComment(kernelModel.getParsedKernel().toString());
        setupData(declaration);
        addTypeParameters(kernelModel.getReferencedTypeParameters());
    }

    @Override
    public CompilationUnit generate() {
        source.getMethods().forEach(this::generateMethods);
        return compilationUnit;
    }

    private void generateMethods(KernelMethod kernelMethod) {
        if (kernelMethod.isCloseKernel()) {
            MethodDeclaration methodDeclaration = declaration.addMethod(kernelMethod.getMethodName()).removeBody();
            setupData(methodDeclaration, kernelMethod);
            declaration.addExtendedType(AutoCloseable.class);
            methodDeclaration.addMarkerAnnotation(Override.class);
        } else {
            MethodDeclaration methodDeclaration = declaration.addMethod(kernelMethod.getMethodName()).removeBody();
            setupData(methodDeclaration, kernelMethod);
            Type thisType = getThisType();
            methodDeclaration.setType(thisType);

            if (kernelMethod.isKernelArgumentMethod())
                methodDeclaration.setJavadocComment(kernelMethod.asKernelArgumentMethod().getParsedKernelArgument().toString());

            List<? extends KernelMethodParameter> parameters = kernelMethod.getParameters();
            generateMethodParameters(parameters, methodDeclaration);

            String methodName = kernelMethod.getMethodName();
            List<? extends KernelMethodParameter> requiredParameters = kernelMethod.getRequiredParameters();
            if (requiredParameters.size() < parameters.size()) {
                MethodDeclaration defaultMethodDeclaration = declaration.addMethod(methodName).setDefault(true).setType(thisType);
                setupData(defaultMethodDeclaration, kernelMethod);

                generateMethodParameters(requiredParameters, defaultMethodDeclaration);

                if (kernelMethod.isKernelArgumentMethod()) {
                    String comment = kernelMethod.asKernelArgumentMethod().getParsedKernelArgument().toString();
                    defaultMethodDeclaration.setJavadocComment(comment);
                }

                String statement = String.format("return %s(%s);", methodName, parameters.stream()
                        .map(p -> p.getDefaultValue() != null ? p.getDefaultValue() : p.getParameterName())
                        .collect(Collectors.joining(", ")));
                defaultMethodDeclaration.setBody(buildBlockStmt(statement));
            }

            if (kernelMethod.isInvoke() && !kernelMethod.asInvoke().isAbstractInvoke()) {
                Invoke invoke = kernelMethod.asInvoke();
                setupData(methodDeclaration, kernelMethod);
                StringBuilder statementBuilder = new StringBuilder();
                parameters.forEach(p -> {
                    if (p.isInvokeArgument()) {
                        SetKernelArgumentMethod setKernelArgumentMethod = p.asInvokeArgument().getSetKernelArgumentMethod();
                        statementBuilder.append(setKernelArgumentMethod.getMethodName())
                                .append("(").append(p.getParameterName()).append(").");
                    }
                });

                EventBuffer eventsBufferParameter = invoke.getEventsBufferParameter();
                InvokeRangeParameter rangeParameter = invoke.getRangetParameter();
                statementBuilder.append(kernelMethod.getMethodName())
                        .append("(")
                        .append(rangeParameter.getParameterName()).append(", ")
                        .append(eventsBufferParameter.getParameterName())
                        .append(");");
                methodDeclaration.setDefault(true)
                        .setBody(buildBlockStmt(statementBuilder.toString(), "return this;"));
            }
        }
    }

    private void generateMethodParameters(List<? extends KernelMethodParameter> parameters, MethodDeclaration methodDeclaration) {
        parameters.forEach(p -> generateMethodParameter(p, methodDeclaration));
    }

    private void generateMethodParameter(KernelMethodParameter parameter, MethodDeclaration methodDeclaration) {
        Parameter parameterDeclaration = new Parameter(resolveParameterType(parameter), parameter.getParameterName());
        if (parameter.isBufferProperties())
            parameterDeclaration.setVarArgs(true);
        else if (parameter.isOptional() && !parameterDeclaration.getType().isPrimitiveType()) {
            compilationUnit.addImport(Nullable.class);
            parameterDeclaration.addMarkerAnnotation(Nullable.class);
        }
        setupData(parameterDeclaration, methodDeclaration.getData(KERNEL_METHOD), parameter);
        methodDeclaration.addParameter(parameterDeclaration);
    }

    private void setupData(Node node, KernelMethod kernelMethod, KernelMethodParameter parameter) {
        setupData(node, kernelMethod);
        node.setData(KERNEL_METHOD_PARAMETER, parameter);
    }

    private void setupData(Node node, KernelMethod kernelMethod) {
        setupData(node);
        node.setData(KERNEL_METHOD, kernelMethod);
    }

    private void setupData(Node node) {
        node.setData(PROGRAM_MODEL, source.getProgramModel());
        node.setData(KERNEL_MODEL, source);
    }

}
