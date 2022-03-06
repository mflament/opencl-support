package org.yah.tools.opencl.codegen.generator.program;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.apache.commons.lang3.StringUtils;
import org.yah.tools.opencl.codegen.generator.model.old.KernelInterface;
import org.yah.tools.opencl.codegen.generator.model.old.ProgramInterface;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.generated.TypeName;

import java.util.List;

import static org.yah.tools.opencl.codegen.generator.impl.CodeGeneratorSupport.PUBLIC_MODIFIERS;
import static org.yah.tools.opencl.codegen.generator.impl.CodeGeneratorSupport.extendsAutoCloseable;

public class ProgramInterfaceGenerator {

    public ProgramInterface generateProgramInterface(TypeName interfaceName,
                                                     ParsedProgram parsedProgram,
                                                     List<KernelInterface> kernelInterfaces) {
        CompilationUnit cu = interfaceName.getPackageName().map(CompilationUnit::new).orElseGet(CompilationUnit::new);
        ClassOrInterfaceDeclaration programDeclaration = new ClassOrInterfaceDeclaration(PUBLIC_MODIFIERS, true, interfaceName.getSimpleName());
        cu.addType(programDeclaration);
        kernelInterfaces.forEach(ki -> {
            TypeDeclaration<?> interfaceDeclaration = ki.getCompilationUnit().getType(0);
            interfaceDeclaration.getFullyQualifiedName().ifPresent(cu::addImport);
            String kernelInterfaceName = interfaceDeclaration.getNameAsString();
            programDeclaration.addMethod(StringUtils.uncapitalize(kernelInterfaceName))
                    .removeBody()
                    .setType(new ClassOrInterfaceType(null, kernelInterfaceName));
        });
        extendsAutoCloseable(programDeclaration);

        return new ProgramInterface(parsedProgram, cu, kernelInterfaces);
    }

}
