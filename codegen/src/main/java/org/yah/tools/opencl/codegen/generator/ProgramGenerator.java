package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import org.yah.tools.opencl.codegen.DefaultNamingStrategy;
import org.yah.tools.opencl.codegen.NamingStrategy;
import org.yah.tools.opencl.codegen.model.ProgramModelBuilder;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.program.ProgramModel;
import org.yah.tools.opencl.codegen.parser.ProgramParser;
import org.yah.tools.opencl.codegen.parser.clinfo.DefaultProgramParser;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProgramGenerator {

    private final Path outputDirectory;
    private final NamingStrategy namingStrategy;

    public ProgramGenerator(Path outputDirectory, @Nullable NamingStrategy namingStrategy) {
        this.outputDirectory = Objects.requireNonNull(outputDirectory, "outputDirectory is null");
        this.namingStrategy = namingStrategy != null ? namingStrategy : DefaultNamingStrategy.get();
    }

    public void generate(CLProgram program, String programPath, String basePackage) throws IOException {
        Objects.requireNonNull(basePackage, "basePackage is null");
        if (basePackage.length() == 0)
            throw new IllegalArgumentException("empty package name");

        ProgramParser parser = new DefaultProgramParser();
        ParsedProgram parsedProgram = parser.parse(program, programPath);
        ProgramModelBuilder modelBuilder = new ProgramModelBuilder(namingStrategy, false);
        ProgramModel programModel = modelBuilder.build(basePackage, parsedProgram);

        Files.createDirectories(outputDirectory);

        ProgramInterfaceGenerator programInterfaceGenerator = new ProgramInterfaceGenerator(programModel);
        CompilationUnit programInterface = programInterfaceGenerator.generate();
        write(programInterface);

        List<CompilationUnit> kernelInterfaces = new ArrayList<>();
        for (KernelModel kernelModel : programModel.getKernels()) {
            KernelInterfaceGenerator kernelInterfaceGenerator = new KernelInterfaceGenerator(kernelModel);
            CompilationUnit kernelInterface = kernelInterfaceGenerator.generate();
            write(kernelInterface);
            kernelInterfaces.add(kernelInterface);
        }

        CLProgramGenerator clProgramGenerator = new CLProgramGenerator(programInterface);
        CompilationUnit clPorgram = clProgramGenerator.generate();
        write(clPorgram);

        for (CompilationUnit kernelInterface : kernelInterfaces) {
            CLKernelGenerator clKernelGenerator = new CLKernelGenerator(clPorgram, kernelInterface);
            write(clKernelGenerator.generate());
        }
    }

    private void write(CompilationUnit compilationUnit) throws IOException {
        String packageName = compilationUnit.getPackageDeclaration()
                .map(PackageDeclaration::getNameAsString)
                .orElseThrow(() -> new IllegalArgumentException("No package in compilation unit " + compilationUnit));
        Path packageDir = outputDirectory.resolve(packageName.replaceAll("\\.", "/"));
        Files.createDirectories(packageDir);
        String typeName = compilationUnit.getType(0).getNameAsString();
        Path outputFile = packageDir.resolve(typeName + ".java");
        Files.write(outputFile, compilationUnit.toString().getBytes(StandardCharsets.UTF_8));
    }
}
