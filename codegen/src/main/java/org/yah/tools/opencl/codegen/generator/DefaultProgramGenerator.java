package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.yah.tools.opencl.codegen.generator.model.old.*;
import org.yah.tools.opencl.codegen.generator.program.CLProgramImplementationGenerator;
import org.yah.tools.opencl.codegen.generator.kernel.KernelnterfacesGenerator;
import org.yah.tools.opencl.codegen.generator.program.ProgramInterfaceGenerator;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.generated.TypeName;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultProgramGenerator {

    private final Path outputDirectory;
    private final NamingStrategy namingStrategy;

    private final KernelnterfacesGenerator kernelnterfacesGenerator;
    private final ProgramInterfaceGenerator programInterfaceGenerator;
    private final List<ImplementationGenerator> implementationGenerators;

    public DefaultProgramGenerator(Path outputDirectory, @Nullable NamingStrategy namingStrategy) {
        this.outputDirectory = Objects.requireNonNull(outputDirectory, "outputDirectory is null");
        if (namingStrategy == null)
            namingStrategy = DefaultNamingStrategy.get();
        this.namingStrategy = namingStrategy;
        kernelnterfacesGenerator = new KernelnterfacesGenerator(namingStrategy);
        programInterfaceGenerator = new ProgramInterfaceGenerator();
        implementationGenerators = Collections.singletonList(new CLProgramImplementationGenerator());
    }

    public void generate(String packageName, ParsedProgram program) throws IOException {
        List<GeneratorKernel> kernels = program.getKernels().stream()
                .map(this::createGeneratorKernel)
                .collect(Collectors.toList());

        List<KernelInterface> kernelInterfaces = kernelnterfacesGenerator.generateKernelInterfaces(packageName, kernels);
        TypeName programInterfaceName = new TypeName(packageName, namingStrategy.programName(program));
        ProgramInterface programInterface = programInterfaceGenerator.generateProgramInterface(programInterfaceName, program, kernelInterfaces);

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        kernelInterfaces.stream().map(KernelInterface::getCompilationUnit).forEach(compilationUnits::add);
        compilationUnits.add(programInterface.getCompilationUnit());

        implementationGenerators.stream()
                .map(ig -> ig.generateImplementation(programInterface))
                .map(ProgramImplementation::compilationUnits)
                .forEach(compilationUnits::addAll);
        write(compilationUnits);
    }

    private GeneratorKernel createGeneratorKernel(ParsedKernel kernel) {
        List<GeneratedKernelArgument> arguments = kernel.getArguments().stream()
                .map(a -> new GeneratedKernelArgument(a, namingStrategy.kernelArgumentName(a)))
                .collect(Collectors.toList());
        return new GeneratorKernel(kernel, arguments);
    }

    public void write(Collection<CompilationUnit> compilationUnits) throws IOException {
        for (CompilationUnit cu : compilationUnits) {
            String packageName = cu.getPackageDeclaration().map(PackageDeclaration::getNameAsString).orElse(null);
            String typeName = declarations(cu).findFirst().map(TypeDeclaration::getNameAsString).orElseThrow(IllegalStateException::new);
            Path packageDir = packageName == null ? outputDirectory : outputDirectory.resolve(packageName.replaceAll("\\.", "/"));
            Files.createDirectories(packageDir);
            Path outputFile = packageDir.resolve(typeName + ".java");
            Files.write(outputFile, cu.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    private static Stream<ClassOrInterfaceDeclaration> declarations(CompilationUnit compilationUnit) {
        return compilationUnit.getTypes().stream()
                .filter(TypeDeclaration::isClassOrInterfaceDeclaration)
                .map(TypeDeclaration::asClassOrInterfaceDeclaration);
    }

}
