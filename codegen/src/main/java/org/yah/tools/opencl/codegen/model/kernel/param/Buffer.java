package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;

public class Buffer extends AbstractKernelArgumentMethodParameter {

    public Buffer(KernelArgumentMethod kernelMethod) {
        super(kernelMethod, 0, null);
    }

    @Override
    public boolean isBuffer() {
        return true;
    }

    @Override
    public Buffer asBuffer() {
        return this;
    }
}
