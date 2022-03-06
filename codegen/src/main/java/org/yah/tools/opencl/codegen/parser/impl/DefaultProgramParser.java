package org.yah.tools.opencl.codegen.parser.impl;

import org.yah.tools.opencl.codegen.parser.ProgramParser;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.program.CLProgram;
import org.yah.tools.opencl.program.CompilerOptionsBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class DefaultProgramParser implements ProgramParser {

    private final CLContext context;
    private final String compilerOptions;

    private final TypeResolver typeResolver = new TypeResolver();
    private final KernelParser kernelParser = new KernelParser(typeResolver);

    public DefaultProgramParser(CLContext context, @Nullable String compilerOptions) {
        this.context = Objects.requireNonNull(context, "context is null");
        this.compilerOptions = new CompilerOptionsBuilder(compilerOptions).kernelArgInfo().build();
    }

    @Override
    public ParsedProgram parse(String filePath) {
        try (CLProgram program = loadProgram(filePath)) {
            ParsedProgram.Builder builder = ParsedProgram.builder()
                    .withCompilerOptions(compilerOptions)
                    .withFilePath(filePath);
            List<String> kernelNames = program.getKernelNames();
            for (String kernelName : kernelNames) {
                try (CLKernel kernel = program.newKernel(kernelName)) {
                    builder.withKernel(kernelParser.parse(kernel));
                }
            }
            return builder.build();
        }
    }

    private CLProgram loadProgram(String filePath) {
        return CLProgram.builder(context)
                .withFile(filePath)
                .withOptions(compilerOptions)
                .build();
    }

}
