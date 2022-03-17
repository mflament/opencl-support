package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.SetKernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.methods.Invoke;

import java.util.Objects;

public class InvokeArgumentParameter extends AbstractKernelMethodParameter {

    private final SetKernelArgumentMethod setKernelArgumentMethod;

    public InvokeArgumentParameter(Invoke invokeMethod, int parameterIndex, SetKernelArgumentMethod setKernelArgumentMethod) {
        super(invokeMethod, parameterIndex,
                setKernelArgumentMethod.getInvokeParameter().map(KernelMethodParameter::getParameterType).orElseThrow(IllegalArgumentException::new),
                null);
        this.setKernelArgumentMethod = Objects.requireNonNull(setKernelArgumentMethod, "setKernelArgumentMethod is null");
    }

    @Override
    public Invoke getMethod() {
        return (Invoke) super.getMethod();
    }

    public SetKernelArgumentMethod getSetKernelArgumentMethod() {
        return setKernelArgumentMethod;
    }

    @Override
    public boolean isInvokeArgument() {
        return true;
    }

    @Override
    public InvokeArgumentParameter asInvokeArgument() {
        return this;
    }
}
