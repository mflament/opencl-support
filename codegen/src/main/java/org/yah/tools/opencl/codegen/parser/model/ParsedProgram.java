package org.yah.tools.opencl.codegen.parser.model;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParsedProgram {
    private final String filePath;
    private final List<ParsedKernel> kernels;
    @Nullable
    private final String compilerOptions;

    private ParsedProgram(String filePath, List<ParsedKernel> kernels, @Nullable String compilerOptions) {
        this.filePath = Objects.requireNonNull(filePath, "relativePath is null");
        this.kernels = Objects.requireNonNull(kernels, "kernels is null");
        this.compilerOptions = compilerOptions;
    }

    public String getFilePath() {
        return filePath;
    }

    public List<ParsedKernel> getKernels() {
        return kernels;
    }

    @Nullable
    public String getCompilerOptions() {
        return compilerOptions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String filePath;
        private final List<ParsedKernel> kernels = new ArrayList<>();
        private String compilerOptions;

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

        public Builder withCompilerOptions(String compilerOptions) {
            this.compilerOptions = compilerOptions;
            return this;
        }

        public ParsedProgram build() {
            return new ParsedProgram(filePath, kernels, compilerOptions);
        }
    }
}
