package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.methods.SetValueComponent;

public class ValueComponent extends AbstractKernelArgumentMethodParameter {

    public ValueComponent(SetValueComponent kernelMethod, int parameterIndex) {
        super(kernelMethod, parameterIndex, null);
    }

    @Override
    public SetValueComponent getMethod() {
        return (SetValueComponent) super.getMethod();
    }

    @Override
    public boolean isValueComponent() {
        return true;
    }

    @Override
    public ValueComponent asValueComponent() {
        return this;
    }

}
