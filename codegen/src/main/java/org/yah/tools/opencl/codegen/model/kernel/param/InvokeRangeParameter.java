package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;

public class InvokeRangeParameter extends AbstractKernelMethodParameter {
    public InvokeRangeParameter(KernelMethod kernelMethod) {
        super(kernelMethod, 0, null);
    }

    @Override
    public boolean isInvokeRangeParameter() {
        return true;
    }

    @Override
    public InvokeRangeParameter asInvokeRangeParameter() {
        return this;
    }

}
