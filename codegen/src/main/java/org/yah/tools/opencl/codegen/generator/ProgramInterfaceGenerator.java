package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.program.ProgramMethod;
import org.yah.tools.opencl.codegen.model.program.ProgramModel;

public class ProgramInterfaceGenerator extends AbstractCodeGenerator<ProgramModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramInterfaceGenerator.class);

    public ProgramInterfaceGenerator(ProgramModel programModel) {
        super(programModel, programModel.getBasePackage(), programModel.getName());
        declaration.setInterface(true);
        declaration.setData(PROGRAM_MODEL, source);
    }

    @Override
    public CompilationUnit generate() {
        source.getMethods().forEach(this::generateMethod);
        return compilationUnit;
    }

    private void generateMethod(ProgramMethod programMethod) {
        MethodDeclaration methodDeclaration = declaration.addMethod(programMethod.getMethodName()).removeBody();
        methodDeclaration.setData(PROGRAM_METHOD, programMethod);
        methodDeclaration.setData(PROGRAM_MODEL, source);

        if (programMethod.isCreateKernel()) {
            KernelModel kernelModel = programMethod.asCreateKernel().getKernelModel();
            Type kernelType = new ClassOrInterfaceType(null, kernelModel.getName());
            methodDeclaration.setType(kernelType);
            methodDeclaration.setData(KERNEL_MODEL, kernelModel);
            compilationUnit.addImport(kernelModel.getQualifiedName());
        } else if (programMethod.isCloseProgram()) {
            declaration.addExtendedType(AutoCloseable.class);
            methodDeclaration.addMarkerAnnotation(Override.class);
        } else {
            LOGGER.error("Unhandled program method {}", programMethod);
        }
    }
}
