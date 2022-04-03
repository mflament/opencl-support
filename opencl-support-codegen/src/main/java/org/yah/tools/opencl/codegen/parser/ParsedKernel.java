package org.yah.tools.opencl.codegen.parser;

import org.apache.commons.lang3.StringUtils;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.codegen.parser.attribute.ParsedAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParsedKernel {
    private final ParsedProgram parsedProgram;
    private final String name;
    private final List<ParsedAttribute> attributes;
    private final List<ParsedKernelArgument> arguments = new ArrayList<>();

    ParsedKernel(ParsedProgram parsedProgram, String name, List<ParsedAttribute> attributes) {
        this.parsedProgram = Objects.requireNonNull(parsedProgram, "program is null");
        this.name = Objects.requireNonNull(name, "name is null");
        this.attributes = CLUtils.copyOfList(Objects.requireNonNull(attributes, "attributes is null"));
    }

    public ParsedProgram getParsedProgram() {
        return parsedProgram;
    }

    public String getName() {
        return name;
    }

    public List<ParsedAttribute> getAttributes() {
        return attributes;
    }

    public List<ParsedKernelArgument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("void ");
        if (!attributes.isEmpty()) {
            sb.append(attributes.stream()
                            .map(a -> "__attribute__((" + a.toString() + "))")
                            .collect(Collectors.joining(", ")))
                    .append(" ");
        }
        sb.append(name).append("(");
        String indent = StringUtils.repeat(' ', sb.length());
        int numArgs = arguments.size();
        for (int i = 0; i < numArgs; i++) {
            if (i > 0) sb.append(indent);
            sb.append(arguments.get(i).toString());
            if (i < numArgs - 1) sb.append(",\n");
        }
        sb.append(")");
        return sb.toString();
    }

    public static Builder builder(ParsedProgram program) {
        return new Builder(program);
    }

    private void addArgument(ParsedKernelArgument argument) {
        arguments.add(argument);
    }

    public static final class Builder {
        private final ParsedProgram program;
        private String name;
        private List<ParsedAttribute> attributes;
        private List<ParsedKernelArgument.Builder> argumentBuilders;

        private Builder(ParsedProgram program) {
            this.program = Objects.requireNonNull(program, "program is null");
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAttributes(List<ParsedAttribute> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder withArguments(List<ParsedKernelArgument.Builder> argumentBuilders) {
            this.argumentBuilders = argumentBuilders;
            return this;
        }

        public ParsedKernel build() {
            ParsedKernel parsedKernel = new ParsedKernel(program, name, attributes);
            argumentBuilders.stream().map(b -> b.build(parsedKernel)).forEach(parsedKernel::addArgument);
            return parsedKernel;
        }
    }
}
