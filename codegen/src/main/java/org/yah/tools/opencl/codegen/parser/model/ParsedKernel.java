package org.yah.tools.opencl.codegen.parser.model;

import org.apache.commons.lang3.StringUtils;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.codegen.parser.model.attribute.ParsedAttribute;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParsedKernel {
    private final String name;
    private final List<ParsedAttribute> attributes;
    private final List<ParsedKernelArgument> arguments;

    public ParsedKernel(String name, List<ParsedAttribute> attributes, List<ParsedKernelArgument> arguments) {
        this.name = Objects.requireNonNull(name, "name is null");
        this.attributes = CLUtils.copyOf(Objects.requireNonNull(attributes, "attributes is null"));
        this.arguments = CLUtils.copyOf(Objects.requireNonNull(arguments, "arguments is null"));
    }

    public String getName() {
        return name;
    }

    public List<ParsedAttribute> getAttributes() {
        return attributes;
    }

    public List<ParsedKernelArgument> getArguments() {
        return arguments;
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
            if (i < numArgs -1) sb.append(",\n");
        }
        sb.append(")");
        return sb.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private List<ParsedAttribute> attributes;
        private List<ParsedKernelArgument> arguments;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAttributes(List<ParsedAttribute> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder withArguments(List<ParsedKernelArgument> arguments) {
            this.arguments = arguments;
            return this;
        }

        public ParsedKernel build() {
            return new ParsedKernel(name, attributes, arguments);
        }
    }
}
