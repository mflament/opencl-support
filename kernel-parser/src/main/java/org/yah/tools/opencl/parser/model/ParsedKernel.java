package org.yah.tools.opencl.parser.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ParsedKernel {
    private final String name;
    private final List<ParsedKernelArgument> arguments;

    private ParsedKernel(String name, List<ParsedKernelArgument> arguments) {
        this.name = Objects.requireNonNull(name, "name is null");
        this.arguments = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(arguments, "arguments is null")));
    }

    public String getName() {
        return name;
    }

    public List<ParsedKernelArgument> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "ParsedKernel{" +
                "name='" + name + '\'' +
                ", arguments=" + arguments +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private final List<ParsedKernelArgument> arguments = new ArrayList<>();

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withArgument(ParsedKernelArgument argument) {
            this.arguments.add(argument);
            return this;
        }

        public ParsedKernel build() {
            return new ParsedKernel(name, arguments);
        }
    }
}
