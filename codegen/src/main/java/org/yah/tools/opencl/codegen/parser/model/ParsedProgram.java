package org.yah.tools.opencl.codegen.parser.model;

import org.yah.tools.opencl.program.CLCompilerOptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParsedProgram {
    private final String filePath;
    private final List<ParsedKernel> kernels;
    private final CLCompilerOptions compilerOptions;

    private ParsedProgram(String filePath, List<ParsedKernel> kernels, CLCompilerOptions compilerOptions) {
        this.filePath = Objects.requireNonNull(filePath, "relativePath is null");
        this.kernels = Objects.requireNonNull(kernels, "kernels is null");
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String filePath;
        private final List<ParsedKernel> kernels = new ArrayList<>();
        private CLCompilerOptions compilerOptions;

        private Builder() {
        }

        public Builder withFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder withKernel(ParsedKernel kernel) {
            this.kernels.add(kernel);
            return this;
        }

        public Builder withCompilerOptions(CLCompilerOptions compilerOptions) {
            this.compilerOptions = compilerOptions;
            return this;
        }

        public Builder withCompilerOptions(String compilerOptions) {
            this.compilerOptions = CLCompilerOptions.parse(compilerOptions);
            return this;
        }

        public ParsedProgram build() {
            if (compilerOptions == null)
                compilerOptions = new CLCompilerOptions();
            return new ParsedProgram(filePath, kernels, compilerOptions);
        }
    }
}
