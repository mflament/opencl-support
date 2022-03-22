package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.NamingStrategy;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.parser.type.CLType;

import javax.annotation.Nullable;
import java.util.Objects;

abstract class AbstractKernelMethodParameter implements KernelMethodParameter {

    protected final KernelMethod kernelMethod;
    protected final int parameterIndex;
    @Nullable
    protected final String defaultValue;

    private String parameterName;

    public AbstractKernelMethodParameter(KernelMethod kernelMethod, int parameterIndex, @Nullable String defaultValue) {
        this.kernelMethod = Objects.requireNonNull(kernelMethod, "kernelMethod is null");
        this.parameterIndex = parameterIndex;
        this.defaultValue = defaultValue;
    }

    @Override
    public KernelMethod getMethod() {
        return kernelMethod;
    }

    @Override
    public int getParameterIndex() {
        return parameterIndex;
    }

    protected final NamingStrategy getNamingStrategy() {
        return kernelMethod.getDeclaringType().getNamingStrategy();
    }

    @Override
    public String getParameterName() {
        if (parameterName == null)
            parameterName = getNamingStrategy().methodParameterName(this);
        return parameterName;
    }

    @Nullable
    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return parameterName;
    }

}
