package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;

public class BufferSize extends AbstractKernelArgumentMethodParameter {

    public BufferSize(KernelArgumentMethod kernelMethod) {
        super(kernelMethod, 0, null);
    }

    @Override
    public boolean isBufferSize() {
        return true;
    }

    @Override
    public BufferSize asBufferSize() {
        return this;
    }

}
