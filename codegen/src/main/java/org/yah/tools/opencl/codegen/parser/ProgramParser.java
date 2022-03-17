package org.yah.tools.opencl.codegen.parser;

import org.yah.tools.opencl.codegen.parser.attribute.ParsedAttribute;
import org.yah.tools.opencl.codegen.parser.clinfo.AttributeParser;
import org.yah.tools.opencl.codegen.parser.clinfo.KernelArgumentParser;
import org.yah.tools.opencl.codegen.parser.clinfo.TypeResolver;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.program.CLProgram;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProgramParser {

    private final TypeResolver typeResolver = new TypeResolver();
    private final AttributeParser attributeParser = new AttributeParser(typeResolver);
    private final KernelArgumentParser argumentParser = new KernelArgumentParser(typeResolver);

    public ParsedProgram parse(CLProgram program, String filePath) {
        ParsedProgram parsedProgram = new ParsedProgram(filePath, program.getCompilerOptions());
        List<String> kernelNames = program.getKernelNames();
        for (String kernelName : kernelNames) {
            try (CLKernel kernel = program.newKernel(kernelName)) {
                parsedProgram.addKernel(parseKernel(parsedProgram, kernel));
            }
        }
        return parsedProgram;
    }

    private ParsedKernel parseKernel(ParsedProgram parsedProgram, CLKernel kernel) {
        return ParsedKernel.builder(parsedProgram)
                .withName(kernel.getName())
                .withAttributes(parseAttributes(kernel))
                .withArguments(parseArguents(kernel))
                .build();
    }

    private List<ParsedKernelArgument> parseArguents(CLKernel kernel) {
        return IntStream.range(0, kernel.getNumArgs())
                .mapToObj(i -> argumentParser.parse(kernel, i))
                .collect(Collectors.toList());
    }

    private List<ParsedAttribute> parseAttributes(CLKernel kernel) {
        String attributes = kernel.getAttributes();
        if (attributes.length() == 0)
            return Collections.emptyList();
        return Arrays.stream(attributes.split(" ")).map(attributeParser::parse).collect(Collectors.toList());
    }
}
