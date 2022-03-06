package org.yah.tools.opencl.codegen.generator.model.old;

import com.github.javaparser.ast.CompilationUnit;
import org.yah.tools.opencl.codegen.generator.model.old.GeneratorKernel;

import java.util.Objects;

public class KernelInterface {
    private final GeneratorKernel kernel;
    private final CompilationUnit compilationUnit;

    public KernelInterface(GeneratorKernel kernel, CompilationUnit compilationUnit) {
        this.kernel = Objects.requireNonNull(kernel, "kernel is null");
        this.compilationUnit = Objects.requireNonNull(compilationUnit, "compilationUnit is null");
    }

    public GeneratorKernel getKernel() {
        return kernel;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }
}
