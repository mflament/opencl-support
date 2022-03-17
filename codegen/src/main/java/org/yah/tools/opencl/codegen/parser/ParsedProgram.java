package org.yah.tools.opencl.codegen.parser;

import org.yah.tools.opencl.program.CLCompilerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParsedProgram {
    private final String filePath;
    private final CLCompilerOptions compilerOptions;
    private final List<ParsedKernel> kernels = new ArrayList<>();

    public ParsedProgram(String filePath, CLCompilerOptions compilerOptions) {
        this.filePath = Objects.requireNonNull(filePath, "relativePath is null");
        this.compilerOptions = Objects.requireNonNull(compilerOptions, "compilerOptions is null");
    }

    public String getFilePath() {
        return filePath;
    }

    public List<ParsedKernel> getKernels() {
        return kernels;
    }

    public CLCompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    void addKernel(ParsedKernel kernel) {
        kernels.add(kernel);
    }

}
