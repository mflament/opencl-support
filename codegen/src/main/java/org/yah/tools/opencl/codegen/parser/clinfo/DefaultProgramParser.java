package org.yah.tools.opencl.codegen.parser.clinfo;

import org.yah.tools.opencl.codegen.parser.ProgramParser;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.program.CLProgram;

import java.util.List;

public class DefaultProgramParser implements ProgramParser {

    private final TypeResolver typeResolver = new TypeResolver();
    private final KernelParser kernelParser = new KernelParser(typeResolver);

    @Override
    public ParsedProgram parse(CLProgram program, String filePath) {
        ParsedProgram.Builder builder = ParsedProgram.builder()
                .withCompilerOptions(program.getCompilerOptions())
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
