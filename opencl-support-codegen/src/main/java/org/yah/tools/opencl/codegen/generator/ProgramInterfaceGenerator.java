package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.yah.tools.opencl.codegen.builder.InterfaceBuilder;
import org.yah.tools.opencl.codegen.builder.JavaTypeBuilder;
import org.yah.tools.opencl.codegen.generator.kernel.KernelInterfaceGenerator;
import org.yah.tools.opencl.codegen.generator.type.JavaTypeVariables;
import org.yah.tools.opencl.codegen.naming.NamingStrategy.KernelNamingStrategy;
import org.yah.tools.opencl.codegen.naming.NamingStrategy.ProgramNamingStrategy;
import org.yah.tools.opencl.codegen.parser.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.ParsedProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProgramInterfaceGenerator {

    private final ParsedProgram parsedProgram;
    private final ProgramGeneratorRequest request;
    private final ProgramNamingStrategy namingStrategy;
    private final InterfaceBuilder interfaceBuilder;
    private final JavaTypeVariables typeVariables;

    public ProgramInterfaceGenerator(ParsedProgram parsedProgram,
                                     ProgramGeneratorRequest request,
                                     ProgramNamingStrategy namingStrategy,
                                     JavaTypeVariables typeVariables) {
        this.parsedProgram = parsedProgram;
        this.request = Objects.requireNonNull(request, "request is null");
        this.namingStrategy = Objects.requireNonNull(namingStrategy, "namingStrategy is null");
        this.typeVariables = Objects.requireNonNull(typeVariables, "typeVariables is null");
        this.interfaceBuilder = new InterfaceBuilder(getBasePackage(), namingStrategy.interfaceName());
        typeVariables.getVariables().stream()
                .map(v -> v.createTypeParameter(interfaceBuilder))
                .forEach(interfaceBuilder::addTypeParameter);
    }

    public ProgramGeneratorRequest getRequest() {
        return request;
    }

    public ClassOrInterfaceDeclaration getDeclaration() {
        return interfaceBuilder.getDeclaration();
    }

    public CompilationUnit getCompilationUnit() {
        return interfaceBuilder.getCompilationUnit();
    }

    public List<CompilationUnit> generate() {
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        for (ParsedKernel kernel : parsedProgram.getKernels()) {
            KernelNamingStrategy kernelNamingStrategy = namingStrategy.kernel(kernel);
            KernelInterfaceGenerator kig = new KernelInterfaceGenerator(kernel, request, kernelNamingStrategy, typeVariables);
            CompilationUnit kernelInterfaceCompilationUnit = kig.generate();
            generateCreateKernel(kernel, kernelInterfaceCompilationUnit);
            compilationUnits.add(kernelInterfaceCompilationUnit);
        }
        interfaceBuilder.makeAutoCloseable();

        compilationUnits.add(interfaceBuilder.getCompilationUnit());
        return compilationUnits;
    }

    private void generateCreateKernel(ParsedKernel kernel, CompilationUnit compilationUnit) {
        ClassOrInterfaceDeclaration kernelDeclaration = compilationUnit.getType(0).asClassOrInterfaceDeclaration();
        interfaceBuilder.addImport(kernelDeclaration);
        interfaceBuilder.addMethod(namingStrategy.createKernel(kernel))
                .withJavaDoc(kernel.toString())
                .setType(JavaTypeBuilder.createType(kernelDeclaration))
                .implementedBy(CLProgramGenerator.class, g -> g.implementCreateKernel(kernel, kernelDeclaration));
    }

    public String getBasePackage() {
        return request.getBasePackage();
    }
}
