package org.yah.tools.opencl.codegen.generator.model.old;

import org.yah.tools.opencl.codegen.generator.model.old.GeneratedKernelArgument;

import java.util.List;

public class GeneratorKernel {
    private final ParsedKernel parsedKernel;
    private final List<GeneratedKernelArgument> arguments;

    public GeneratorKernel(ParsedKernel parsedKernel, List<GeneratedKernelArgument> arguments) {
        this.parsedKernel = parsedKernel;
        this.arguments = arguments;
    }

    public ParsedKernel getParsedKernel() {
        return parsedKernel;
    }

    public List<GeneratedKernelArgument> getArguments() {
        return arguments;
    }

}
