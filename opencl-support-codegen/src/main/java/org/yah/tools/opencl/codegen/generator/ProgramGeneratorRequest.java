package org.yah.tools.opencl.codegen.generator;

import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.codegen.parser.CLTypeVariables;
import org.yah.tools.opencl.program.CLCompilerOptions;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ProgramGeneratorRequest {

    private final String programSource;
    private final CLCompilerOptions compilerOptions;
    private final String programPath;
    private final String basePackage;
    private final CLTypeVariables clTypeVariables;
    private final List<String> kernelInterfaces;

    private ProgramGeneratorRequest(String programSource, CLCompilerOptions compilerOptions, String programPath,
                                    String basePackage, @Nullable CLTypeVariables clTypeVariables,
                                    List<String> kernelInterfaces) {
        this.programSource = Objects.requireNonNull(programSource, "programSource is null");
        this.compilerOptions = Objects.requireNonNull(compilerOptions, "compilerOptions is null");
        this.programPath = Objects.requireNonNull(programPath, "programPath is null");
        this.basePackage = Objects.requireNonNull(basePackage, "basePackage is null");
        if (basePackage.length() == 0)
            throw new IllegalArgumentException("basePackage " + basePackage + " is empty");
        this.clTypeVariables = Objects.requireNonNull(clTypeVariables, "clTypeVariables is null");
        this.kernelInterfaces = CLUtils.copyOfList(kernelInterfaces);
    }

    public String getProgramSource() {
        return programSource;
    }

    public CLCompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    public String getProgramPath() {
        return programPath;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public CLTypeVariables getTypeParametersConfig() {
        return clTypeVariables;
    }

    public List<String> getKernelInterfaces() {
        return kernelInterfaces;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String programSource;
        private CLCompilerOptions compilerOptions;
        private String programPath;
        private String basePackage;
        private CLTypeVariables typeVariables = CLTypeVariables.EMPTY;
        private final List<String> kernelSuperInterfaces = new ArrayList<>();

        private Builder() {
        }

        public Builder withProgramSource(String programSource) {
            this.programSource = programSource;
            return this;
        }

        public Builder withCompilerOptions(CLCompilerOptions compilerOptions) {
            this.compilerOptions = compilerOptions;
            return this;
        }

        public Builder withProgramPath(String programPath) {
            this.programPath = programPath;
            return this;
        }

        public Builder withBasePackage(String basePackage) {
            this.basePackage = basePackage;
            return this;
        }

        public Builder withTypeVariables(CLTypeVariables typeVariables) {
            this.typeVariables = typeVariables;
            return this;
        }

        public ProgramGeneratorRequest build() {
            if (programPath == null)
                throw new IllegalArgumentException("programPath is required");

            if (compilerOptions == null)
                compilerOptions = new CLCompilerOptions();

            if (programSource == null)
                programSource = CLProgram.loadSource(programPath);

            return new ProgramGeneratorRequest(programSource, compilerOptions, programPath, basePackage,
                    typeVariables, kernelSuperInterfaces);
        }

        public Builder withKernelInterfaces(List<String> kernelSuperInterfaces) {
            this.kernelSuperInterfaces.addAll(kernelSuperInterfaces);
            return this;
        }
    }
}
