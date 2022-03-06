package org.yah.tools.opencl.codegen.generator.model.old;

import com.github.javaparser.ast.CompilationUnit;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;

import java.util.List;
import java.util.Objects;

public class ProgramInterface {
    private final ParsedProgram parsedProgram;
    private final CompilationUnit compilationUnit;
    private final List<KernelInterface> kernelInterfaces;

    public ProgramInterface(ParsedProgram parsedProgram, CompilationUnit compilationUnit, List<KernelInterface> kernelInterfaces) {
        this.parsedProgram = Objects.requireNonNull(parsedProgram, "parsedProgram is null");
        this.compilationUnit = Objects.requireNonNull(compilationUnit, "programInterface is null");
        this.kernelInterfaces = Objects.requireNonNull(kernelInterfaces, "kernelInterfaces is null");

    }

    public ParsedProgram getParsedProgram() {
        return parsedProgram;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public List<KernelInterface> getKernelInterfaces() {
        return kernelInterfaces;
    }
}
