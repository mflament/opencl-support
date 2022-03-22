package org.yah.tools.opencl.program;

import javax.annotation.Nullable;
import java.util.*;

/**
 * https://www.khronos.org/registry/OpenCL/sdk/1.2/docs/man/xhtml/clBuildProgram.html
 */
public class CLCompilerOptions {

    // Preprocessor Options

    private final Map<String, String> macros = new LinkedHashMap<>();
    private final List<String> includes = new ArrayList<>();

    // Math Intrinsics Options

    private boolean singlePrecisionConstant;
    private boolean denormsAreZero;
    private boolean fp32CorrectlyRoundedDivideSqrt;

    // Optimization Options

    private boolean optDisable;
    private boolean madEnable;
    private boolean noSignedZeros;
    private boolean unsafeMathOptimizations;
    private boolean finiteMathOnly;
    private boolean fastRelaxedMath;

    // Options to Request or Suppress Warnings
    private boolean noWarning;
    private boolean warningIsError;

    // Options Controlling the OpenCL C Version
    private String clStd;

    // Options for Querying Kernel Argument Information

    private boolean kernelArgInfo;

    public Map<String, String> getMacros() {
        return macros;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public boolean isSinglePrecisionConstant() {
        return singlePrecisionConstant;
    }

    public boolean isDenormsAreZero() {
        return denormsAreZero;
    }

    public boolean isFp32CorrectlyRoundedDivideSqrt() {
        return fp32CorrectlyRoundedDivideSqrt;
    }

    public boolean isOptDisable() {
        return optDisable;
    }

    public boolean isMadEnable() {
        return madEnable;
    }

    public boolean isNoSignedZeros() {
        return noSignedZeros;
    }

    public boolean isUnsafeMathOptimizations() {
        return unsafeMathOptimizations;
    }

    public boolean isFiniteMathOnly() {
        return finiteMathOnly;
    }

    public boolean isFastRelaxedMath() {
        return fastRelaxedMath;
    }

    public String getClStd() {
        return clStd;
    }

    public boolean isKernelArgInfo() {
        return kernelArgInfo;
    }

    public boolean isNoWarning() {
        return noWarning;
    }

    public boolean isWarningIsError() {
        return warningIsError;
    }

    public CLCompilerOptions withSinglePrecisionConstant() {
        this.singlePrecisionConstant = true;
        return this;
    }

    public CLCompilerOptions withDenormsAreZero() {
        this.denormsAreZero = true;
        return this;
    }

    public CLCompilerOptions withFp32CorrectlyRoundedDivideSqrt() {
        this.fp32CorrectlyRoundedDivideSqrt = true;
        return this;
    }

    public CLCompilerOptions withOptDisable() {
        this.optDisable = true;
        return this;
    }

    public CLCompilerOptions withMadEnable() {
        this.madEnable = true;
        return this;
    }

    public CLCompilerOptions withNoSignedZeros() {
        this.noSignedZeros = true;
        return this;
    }

    public CLCompilerOptions withUnsafeMathOptimizations() {
        this.unsafeMathOptimizations = true;
        return this;
    }

    public CLCompilerOptions withFiniteMathOnly() {
        this.finiteMathOnly = true;
        return this;
    }

    public CLCompilerOptions withFastRelaxedMath() {
        this.fastRelaxedMath = true;
        return this;
    }

    public CLCompilerOptions withNoWarning() {
        this.noWarning = true;
        return this;
    }

    public CLCompilerOptions withWarningIsError() {
        this.warningIsError = true;
        return this;
    }

    public CLCompilerOptions withKernelArgInfo() {
        this.kernelArgInfo = true;
        return this;
    }

    public CLCompilerOptions withoutSinglePrecisionConstant() {
        this.singlePrecisionConstant = false;
        return this;
    }

    public CLCompilerOptions withoutDenormsAreZero() {
        this.denormsAreZero = false;
        return this;
    }

    public CLCompilerOptions withoutFp32CorrectlyRoundedDivideSqrt() {
        this.fp32CorrectlyRoundedDivideSqrt = false;
        return this;
    }

    public CLCompilerOptions withoutOptDisable() {
        this.optDisable = false;
        return this;
    }

    public CLCompilerOptions withoutMadEnable() {
        this.madEnable = false;
        return this;
    }

    public CLCompilerOptions withoutNoSignedZeros() {
        this.noSignedZeros = false;
        return this;
    }

    public CLCompilerOptions withoutUnsafeMathOptimizations() {
        this.unsafeMathOptimizations = false;
        return this;
    }

    public CLCompilerOptions withoutFiniteMathOnly() {
        this.finiteMathOnly = false;
        return this;
    }

    public CLCompilerOptions withoutFastRelaxedMath() {
        this.fastRelaxedMath = false;
        return this;
    }

    public CLCompilerOptions withoutNoWarning() {
        this.noWarning = false;
        return this;
    }

    public CLCompilerOptions withoutWarningIsError() {
        this.warningIsError = false;
        return this;
    }

    public CLCompilerOptions withoutKernelArgInfo() {
        this.kernelArgInfo = false;
        return this;
    }

    public CLCompilerOptions withClStd(String clStd) {
        this.clStd = clStd;
        return this;
    }

    public CLCompilerOptions putMacro(String name, String value) {
        macros.put(name, value);
        return this;
    }

    public CLCompilerOptions putMacro(String name) {
        return putMacro(name, name);
    }

    @Nullable
    public String getMacro(String name) {
        return macros.get(name);
    }

    public CLCompilerOptions() {
    }

    public CLCompilerOptions(@Nullable CLCompilerOptions options) {
        if (options != null) {
            macros.putAll(options.macros);
            includes.addAll(options.includes);
            singlePrecisionConstant = options.singlePrecisionConstant;
            denormsAreZero = options.denormsAreZero;
            fp32CorrectlyRoundedDivideSqrt = options.fp32CorrectlyRoundedDivideSqrt;
            optDisable = options.optDisable;
            madEnable = options.madEnable;
            noSignedZeros = options.noSignedZeros;
            unsafeMathOptimizations = options.unsafeMathOptimizations;
            finiteMathOnly = options.finiteMathOnly;
            fastRelaxedMath = options.fastRelaxedMath;
            noWarning = options.noWarning;
            warningIsError = options.warningIsError;
            clStd = options.clStd;
            kernelArgInfo = options.kernelArgInfo;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        macros.forEach((m, v) -> sb.append("-D ").append(printMacro(m, v)).append(" "));
        includes.forEach(i -> sb.append("-I ").append(i).append(" "));

        append("-cl-single-precision-constant", singlePrecisionConstant, sb);
        append("-cl-denorms-are-zero", denormsAreZero, sb);
        append("-cl-fp32-correctly-rounded-divide-sqrt", fp32CorrectlyRoundedDivideSqrt, sb);
        append("-cl-opt-disable", optDisable, sb);
        append("-cl-mad-enable", madEnable, sb);
        append("-cl-no-signed-zeros", noSignedZeros, sb);
        append("-cl-unsafe-math-optimizations", unsafeMathOptimizations, sb);
        append("-cl-finite-math-only", finiteMathOnly, sb);
        append("-cl-fast-relaxed-math", fastRelaxedMath, sb);
        append("-w", noWarning, sb);
        append("-Werror", warningIsError, sb);
        append("-cl-kernel-arg-info", kernelArgInfo, sb);

        if (clStd != null)
            sb.append("-cl-std=").append(clStd).append(" ");

        return sb.toString().trim();
    }

    private static String printMacro(String name, @Nullable String value) {
        if (value == null) return name;
        return name + "=" + value;
    }

    private static void append(String what, boolean when, StringBuilder where) {
        if (when) where.append(what).append(" ");
    }

    public static CLCompilerOptions parse(@Nullable String options) {
        CLCompilerOptions res = new CLCompilerOptions();
        if (options == null)
            return res;

        String[] parts = options.split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            i = parse(parts, i, res);
        }
        return res;
    }

    private static int parse(String[] parts, int partIndex, CLCompilerOptions options) {
        String part = parts[partIndex];
        switch (part) {
            case "-cl-single-precision-constant":
                options.singlePrecisionConstant = true;
                break;
            case "-cl-denorms-are-zero":
                options.denormsAreZero = true;
                break;
            case "-cl-fp32-correctly-rounded-divide-sqrt":
                options.fp32CorrectlyRoundedDivideSqrt = true;
                break;
            case "-cl-opt-disable":
                options.optDisable = true;
                break;
            case "-cl-mad-enable":
                options.madEnable = true;
                break;
            case "-cl-no-signed-zeros":
                options.noSignedZeros = true;
                break;
            case "-cl-unsafe-math-optimizations":
                options.unsafeMathOptimizations = true;
                break;
            case "-cl-finite-math-only":
                options.finiteMathOnly = true;
                break;
            case "-cl-fast-relaxed-math":
                options.fastRelaxedMath = true;
                break;
            case "-cl-kernel-arg-info":
                options.kernelArgInfo = true;
                break;
            case "-w":
                options.noWarning = true;
                break;
            case "-Werror":
                options.warningIsError = true;
                break;
            default:
                if (part.startsWith("-cl-std=")) {
                    options.clStd = part.substring("-cl-std=".length());
                } else if (part.equals("-D")) {
                    if (partIndex + 1 < parts.length) {
                        partIndex += 1;
                        String nextPart = parts[partIndex];
                        int index = nextPart.indexOf("=");
                        String name, value;
                        if (index > 0) {
                            name = nextPart.substring(0, index);
                            value = index < nextPart.length() - 1 ? nextPart.substring(index + 1) : null;
                        } else {
                            name = nextPart;
                            value = null;
                        }
                        options.putMacro(name, value);
                    }
                } else if (part.equals("-I")) {
                    if (partIndex + 1 < parts.length) {
                        partIndex += 1;
                        String nextPart = parts[partIndex];
                        options.includes.add(nextPart);
                    }
                }
        }
        return partIndex;
    }
}
