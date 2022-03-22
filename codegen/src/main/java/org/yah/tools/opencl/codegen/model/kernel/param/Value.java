package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.methods.SetValue;

public class Value extends AbstractKernelArgumentMethodParameter {

    public Value(SetValue kernelMethod, int parameterIndex) {
        super(kernelMethod, parameterIndex, null);
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

}
