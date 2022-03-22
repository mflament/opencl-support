package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.SetKernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.methods.Invoke;

import java.util.Objects;

public class InvokeArgumentParameter extends AbstractKernelMethodParameter {

    private final SetKernelArgumentMethod setKernelArgumentMethod;

    public InvokeArgumentParameter(Invoke invokeMethod, int parameterIndex, SetKernelArgumentMethod setKernelArgumentMethod) {
        super(invokeMethod, parameterIndex, null);
        this.setKernelArgumentMethod = Objects.requireNonNull(setKernelArgumentMethod, "setKernelArgumentMethod is null");
        setKernelArgumentMethod.getInvokeParameter()
                .orElseThrow(() -> new IllegalArgumentException("argument setter method " + setKernelArgumentMethod + " has no invoke parameter"));
    }

    @Override
    public Invoke getMethod() {
        return (Invoke) super.getMethod();
    }

    public KernelMethodParameter getSetterParameter() {
        return setKernelArgumentMethod.getInvokeParameter().orElseThrow(IllegalStateException::new);
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
