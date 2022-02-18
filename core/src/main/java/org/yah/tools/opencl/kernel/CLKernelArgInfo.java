package org.yah.tools.opencl.kernel;

import org.yah.tools.opencl.enums.KernelArgAccessQualifier;
import org.yah.tools.opencl.enums.KernelArgAddressQualifier;
import org.yah.tools.opencl.enums.KernelArgTypeQualifier;

public class CLKernelArgInfo {
    private final KernelArgAddressQualifier addressQualifier;
    private final KernelArgAccessQualifier accessQualifier;
    private final String typeName;
    private final KernelArgTypeQualifier typeQualifier;
    private final String argName;

    private CLKernelArgInfo(KernelArgAddressQualifier addressQualifier, KernelArgAccessQualifier accessQualifier, String typeName, KernelArgTypeQualifier typeQualifier, String argName) {
        this.addressQualifier = addressQualifier;
        this.accessQualifier = accessQualifier;
        this.typeName = typeName;
        this.typeQualifier = typeQualifier;
        this.argName = argName;
    }

    public KernelArgAddressQualifier getAddressQualifier() {
        return addressQualifier;
    }

    public KernelArgAccessQualifier getAccessQualifier() {
        return accessQualifier;
    }

    public String getTypeName() {
        return typeName;
    }

    public KernelArgTypeQualifier getTypeQualifier() {
        return typeQualifier;
    }

    public String getArgName() {
        return argName;
    }

    @Override
    public String toString() {
        return "CLKernelArgInfo{" +
                "addressQualifier=" + addressQualifier +
                ", accessQualifier=" + accessQualifier +
                ", typeName='" + typeName + '\'' +
                ", typeQualifier=" + typeQualifier +
                ", argName='" + argName + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private KernelArgAddressQualifier addressQualifier;
        private KernelArgAccessQualifier accessQualifier;
        private String typeName;
        private KernelArgTypeQualifier typeQualifier;
        private String argName;

        private Builder() {
        }

        public Builder withAddressQualifier(KernelArgAddressQualifier addressQualifier) {
            this.addressQualifier = addressQualifier;
            return this;
        }

        public Builder withAccessQualifier(KernelArgAccessQualifier accessQualifier) {
            this.accessQualifier = accessQualifier;
            return this;
        }

        public Builder withTypeName(String typeName) {
            this.typeName = typeName;
            return this;
        }

        public Builder withTypeQualifier(KernelArgTypeQualifier typeQualifier) {
            this.typeQualifier = typeQualifier;
            return this;
        }

        public Builder withArgName(String argName) {
            this.argName = argName;
            return this;
        }

        public CLKernelArgInfo build() {
            return new CLKernelArgInfo(addressQualifier, accessQualifier, typeName, typeQualifier, argName);
        }
    }
}
