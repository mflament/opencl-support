package org.yah.tools.opencl.codegen.parser;

import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.enums.CLBitfield;
import org.yah.tools.opencl.enums.KernelArgAccessQualifier;
import org.yah.tools.opencl.enums.KernelArgAddressQualifier;
import org.yah.tools.opencl.enums.KernelArgTypeQualifier;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.yah.tools.opencl.enums.KernelArgAddressQualifier.GLOBAL;
import static org.yah.tools.opencl.enums.KernelArgTypeQualifier.CONST;

public class ParsedKernelArgument {

    private final ParsedKernel kernel;
    private final int argIndex;
    private final String argName;
    private final CLType type;
    private final KernelArgAddressQualifier addressQualifier;
    private final KernelArgAccessQualifier accessQualifier;
    private final CLBitfield<KernelArgTypeQualifier> typeQualifiers;

    private ParsedKernelArgument(ParsedKernel kernel, int argIndex, String argName, CLType type,
                                 KernelArgAddressQualifier addressQualifier,
                                 KernelArgAccessQualifier accessQualifier,
                                 CLBitfield<KernelArgTypeQualifier> typeQualifiers) {
        this.kernel = Objects.requireNonNull(kernel, "kernel is null");
        this.argIndex = argIndex;
        this.argName = Objects.requireNonNull(argName, "argName is null");
        this.type = Objects.requireNonNull(type, "type is null");
        this.addressQualifier = Objects.requireNonNull(addressQualifier, "addressQualifier is null");
        this.accessQualifier = Objects.requireNonNull(accessQualifier, "accessQualifier is null");
        this.typeQualifiers = Objects.requireNonNull(typeQualifiers, "typeQualifiers is null");
    }

    public ParsedKernel getKernel() {
        return kernel;
    }

    public int getArgIndex() {
        return argIndex;
    }

    public String getArgName() {
        return argName;
    }

    public CLType getType() {
        return type;
    }

    public KernelArgAddressQualifier getAddressQualifier() {
        return addressQualifier;
    }

    public KernelArgAccessQualifier getAccessQualifier() {
        return accessQualifier;
    }

    public Set<KernelArgTypeQualifier> getTypeQualifiers() {
        return typeQualifiers.values();
    }

    public boolean canRead() {
        // can only read decive memory if the argument is a global pointer that can be written by device (so not const)
        return type.isPointer() && addressQualifier == GLOBAL && !typeQualifiers.contains(CONST);
    }

    public boolean isAnyAddress(KernelArgAddressQualifier addressQualifier, KernelArgAddressQualifier... addressQualifiers) {
        return this.addressQualifier == addressQualifier || Arrays.asList(addressQualifiers).contains(this.addressQualifier);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (accessQualifier != KernelArgAccessQualifier.NONE)
            sb.append(accessQualifier.toString().toLowerCase());
        else
            sb.append(addressQualifier.toString().toLowerCase());
        sb.append(" ");
        String typeQualifierNames = typeQualifiers.values().stream()
                .filter(tq -> tq != KernelArgTypeQualifier.NONE)
                .map(tq -> tq.name().toLowerCase())
                .collect(Collectors.joining(" "));

        if (typeQualifierNames.length() > 0)
            sb.append(typeQualifierNames).append(" ");

        sb.append(type.getName()).append(" ").append(argName);

        return sb.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int argIndex;
        private String argName;
        private CLType type;
        private KernelArgAddressQualifier addressQualifier;
        private KernelArgAccessQualifier accessQualifier;

        private CLBitfield<KernelArgTypeQualifier> typeQualifiers;

        private Builder() {
        }

        public Builder withArgIndex(int argIndex) {
            this.argIndex = argIndex;
            return this;
        }

        public Builder withArgName(String argName) {
            this.argName = argName;
            return this;
        }

        public Builder withType(CLType type) {
            this.type = type;
            return this;
        }

        public Builder withAddressQualifier(KernelArgAddressQualifier addressQualifier) {
            this.addressQualifier = addressQualifier;
            return this;
        }

        public Builder withAccessQualifier(KernelArgAccessQualifier accessQualifier) {
            this.accessQualifier = accessQualifier;
            return this;
        }

        public Builder withTypeQualifiers(CLBitfield<KernelArgTypeQualifier> typeQualifiers) {
            this.typeQualifiers = typeQualifiers;
            return this;
        }

        public ParsedKernelArgument build(ParsedKernel kernel) {
            return new ParsedKernelArgument(kernel, argIndex, argName, type, addressQualifier, accessQualifier, typeQualifiers);
        }
    }

}
