package org.yah.tools.opencl.program;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

// https://www.khronos.org/registry/OpenCL/sdk/1.2/docs/man/xhtml/clBuildProgram.html
public class CompilerOptionsBuilder {

    private final Set<String> options = new LinkedHashSet<>();

    public CompilerOptionsBuilder() {
    }

    public CompilerOptionsBuilder(@Nullable String allOptions) {
        if (allOptions != null)
            this.options.addAll(parse(allOptions));
    }

    public CompilerOptionsBuilder(@Nullable Collection<String> options) {
        if (options != null) this.options.addAll(options);
    }

    ////////// Preprocessor Options /////////////
    public CompilerOptionsBuilder withMacro(String name) {
        options.add("-D " + name);
        return this;
    }

    public CompilerOptionsBuilder withMacro(String name, String value) {
        options.add("-D " + name + "=" + value);
        return this;
    }

    public CompilerOptionsBuilder withIncludeDir(String dir) {
        options.add("-I " + dir);
        return this;
    }

    ////////// Math Intrinsics Options /////////////
    public CompilerOptionsBuilder singlePrecisionConstant() {
        options.add("-cl-single-precision-constant");
        return this;
    }

    public CompilerOptionsBuilder denormsAreZero() {
        options.add("-cl-denorms-are-zero");
        return this;
    }

    public CompilerOptionsBuilder fp32CorrectlyRoundedDivideSqrt() {
        options.add("-cl-fp32-correctly-rounded-divide-sqrt");
        return this;
    }

    // Optimization Options

    public CompilerOptionsBuilder optDisable() {
        options.add("-cl-opt-disable");
        return this;
    }

    public CompilerOptionsBuilder madEnable() {
        options.add("-cl-mad-enable");
        return this;
    }

    public CompilerOptionsBuilder noSignedZeros() {
        options.add("-cl-no-signed-zerose");
        return this;
    }

    public CompilerOptionsBuilder unsafeMathOptimizations() {
        options.add("-cl-unsafe-math-optimizations");
        return this;
    }

    public CompilerOptionsBuilder finiteMathOnly() {
        options.add("-cl-finite-math-only");
        return this;
    }

    public CompilerOptionsBuilder fastRelaxedMath() {
        options.add("-cl-fast-relaxed-math");
        return this;
    }

    //// Options to Request or Suppress Warnings

    public CompilerOptionsBuilder suppressWarnings() {
        options.add("-w");
        return this;
    }

    public CompilerOptionsBuilder suppressWarning(String error) {
        options.add("-W" + error);
        return this;
    }

    /// Options Controlling the OpenCL C Version

    public CompilerOptionsBuilder clStd(String std) {
        options.add("-cl-std=" + std);
        return this;
    }

    //// Options for Querying Kernel Argument Information
    public CompilerOptionsBuilder kernelArgInfo() {
        options.add("-cl-kernel-arg-info");
        return this;
    }

    public String build() {
        return options.stream().map(String::trim).collect(Collectors.joining(" "));
    }

    private static List<String> parse(String allOptions) {
        String[] parts = allOptions.split(" ");
        List<String> options = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            String option = part;
            if (part.equals("-D") || part.equals("-I")) {
                option += " " + parts[++i];
            }
            options.add(option);
        }
        return options;
    }

}
