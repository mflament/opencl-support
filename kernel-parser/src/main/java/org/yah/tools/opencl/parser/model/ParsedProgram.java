package org.yah.tools.opencl.parser.model;

import org.yah.tools.opencl.parser.type.CLType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ParsedProgram {
    private final List<ParsedKernel> kernels;
    private final Map<String, CLType> clTypes;

    private ParsedProgram(List<ParsedKernel> kernels, Map<String, CLType> clTypes) {
        this.kernels = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(kernels, "kernels is null")));
        this.clTypes = Collections.unmodifiableMap(new HashMap<>(Objects.requireNonNull(clTypes, "clTypes is null")));
    }

    public List<ParsedKernel> getKernels() {
        return kernels;
    }

    public Map<String, CLType> getClTypes() {
        return clTypes;
    }

    @Nonnull
    public CLType getClType(String typeName) {
        CLType clType = clTypes.get(typeName);
        if (clType == null)
            throw new IllegalArgumentException("Unknown type " + typeName);
        return clType;
    }

    @Override
    public String toString() {
        return "ParsedProgram{" +
                "kernels=" + kernels +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<String, CLType> clTypes = new LinkedHashMap<>();
        private final List<ParsedKernel> kernels = new ArrayList<>();

        private Builder() {
        }

        public Builder withKernel(ParsedKernel kernel) {
            kernels.add(kernel);
            return this;
        }

        public Builder withType(CLType type) {
            clTypes.put(type.getName(), type);
            return this;
        }

        @Nullable
        public CLType getType(String name) {
            return clTypes.get(name);
        }

        public ParsedProgram build() {
            return new ParsedProgram(kernels, clTypes);
        }
    }
}
