package org.yah.tools.opencl.codegen.generator.model.old;

import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProgramImplementation {
    private final ProgramInterface programInterface;
    private final CompilationUnit compilationUnit;
    private final List<KernelImplementation> kernels;

    public ProgramImplementation(ProgramInterface programInterface, CompilationUnit compilationUnit, List<KernelImplementation> kernels) {
        this.programInterface = Objects.requireNonNull(programInterface, "programInterface is null");
        this.compilationUnit = Objects.requireNonNull(compilationUnit, "compilationUnit is null");
        this.kernels = Objects.requireNonNull(kernels, "kernels is null");
    }

    public ProgramInterface getProgramInterface() {
        return programInterface;
    }

    public List<KernelImplementation> getKernels() {
        return kernels;
    }

    public List<CompilationUnit> compilationUnits() {
        List<CompilationUnit>  res = new ArrayList<>();
        res.add(compilationUnit);
        kernels.stream().map(KernelImplementation::getCompilationUnit).forEach(res::add);
        return res;
    }
}
