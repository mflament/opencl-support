package org.yah.tools.opencl.codegen.generator.model.old;

import com.github.javaparser.ast.CompilationUnit;

import java.util.Objects;

public class KernelImplementation {
    private final KernelInterface kernelInterface;
    private final CompilationUnit compilationUnit;

    public KernelImplementation(KernelInterface kernelInterface, CompilationUnit compilationUnit) {
        this.kernelInterface = Objects.requireNonNull(kernelInterface, "kernelInterface is null");
        this.compilationUnit = Objects.requireNonNull(compilationUnit, "compilationUnit is null");
    }

    public KernelInterface getKernelInterface() {
        return kernelInterface;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }
}
