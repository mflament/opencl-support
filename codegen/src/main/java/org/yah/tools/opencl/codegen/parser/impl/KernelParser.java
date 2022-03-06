package org.yah.tools.opencl.codegen.parser.impl;

import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.model.attribute.ParsedAttribute;
import org.yah.tools.opencl.kernel.CLKernel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class KernelParser {

    private final AttributeParser attributeParser;
    private final KernelArgumentParser argumentParser;

    KernelParser(TypeResolver typeResolver) {
        Objects.requireNonNull(typeResolver, "typeResolver is null");
        attributeParser = new AttributeParser(typeResolver);
        argumentParser = new KernelArgumentParser(typeResolver);
    }

    public ParsedKernel parse(CLKernel kernel) {
        return ParsedKernel.builder()
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
