package org.yah.tools.opencl.codegen.generator;

import org.yah.tools.opencl.codegen.TypeParametersConfig;
import org.yah.tools.opencl.program.CLCompilerOptions;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;
import java.util.Objects;

public final class ProgramGeneratorRequest {

    private final String programSource;
    private final CLCompilerOptions compilerOptions;
    private final String programPath;
    private final String basePackage;
    @Nullable
    private final TypeParametersConfig typeParametersConfig;

    private ProgramGeneratorRequest(String programSource, CLCompilerOptions compilerOptions, String programPath, String basePackage, @Nullable TypeParametersConfig typeParametersConfig) {
        this.programSource = Objects.requireNonNull(programSource, "programSource is null");
        this.compilerOptions = Objects.requireNonNull(compilerOptions, "compilerOptions is null");
        this.programPath = Objects.requireNonNull(programPath, "programPath is null");
        this.basePackage = Objects.requireNonNull(basePackage, "basePackage is null");
        if (basePackage.length() == 0) throw new IllegalArgumentException("basePackage " + basePackage + " is empty");
        this.typeParametersConfig = typeParametersConfig;
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

    @Nullable
    public TypeParametersConfig getTypeParametersConfig() {
        return typeParametersConfig;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String programSource;
        private CLCompilerOptions compilerOptions;
        private String programPath;
        private String basePackage;
        private TypeParametersConfig typeParametersConfig;

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

        public Builder withTypeParametersConfig(TypeParametersConfig typeParametersConfig) {
            this.typeParametersConfig = typeParametersConfig;
            return this;
        }

        public ProgramGeneratorRequest build() {
            if (programPath == null)
                throw new IllegalArgumentException("programPath is required");

            if (compilerOptions == null)
                compilerOptions = new CLCompilerOptions();

            if (programSource == null)
                programSource = CLProgram.loadSource(programPath);

            return new ProgramGeneratorRequest(programSource, compilerOptions, programPath, basePackage, typeParametersConfig);
        }

    }
}
