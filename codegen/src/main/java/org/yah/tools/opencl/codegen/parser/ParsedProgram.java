package org.yah.tools.opencl.codegen.parser;

import org.yah.tools.opencl.codegen.TypeParametersConfig;
import org.yah.tools.opencl.program.CLCompilerOptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParsedProgram {
    private final String filePath;
    private final CLCompilerOptions compilerOptions;
    @Nullable
    private final TypeParametersConfig typeParametersConfig;
    private final List<ParsedKernel> kernels = new ArrayList<>();

    public ParsedProgram(String filePath, CLCompilerOptions compilerOptions, @Nullable TypeParametersConfig typeParametersConfig) {
        this.filePath = Objects.requireNonNull(filePath, "relativePath is null");
        this.compilerOptions = Objects.requireNonNull(compilerOptions, "compilerOptions is null");
        this.typeParametersConfig = typeParametersConfig;
    }

    @Nullable
    public TypeParametersConfig getTypeParametersConfig() {
        return typeParametersConfig;
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
