package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.methods.SetValue;
import org.yah.tools.opencl.codegen.parser.type.CLType;

public class Value extends AbstractKernelArgumentMethodParameter {

    public Value(KernelArgumentMethod kernelMethod, int parameterIndex, Class<?> parameterType) {
        super(kernelMethod, parameterIndex, parameterType, null);
    }

    @Override
    public SetValue getMethod() {
        return (SetValue) super.getMethod();
    }

    @Override
    public boolean isValue() {
        return true;
    }

    @Override
    public Value asValue() {
        return this;
    }

    public CLType getType() {
        return getMethod().getParsedKernelArgument().getType();
    }
}
