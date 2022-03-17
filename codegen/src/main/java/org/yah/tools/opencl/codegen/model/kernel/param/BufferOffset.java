package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;

public class BufferOffset extends AbstractKernelArgumentMethodParameter {

    public BufferOffset(KernelArgumentMethod kernelMethod) {
        super(kernelMethod, 1, Long.TYPE, "0L");
    }

    @Override
    public boolean isBufferOffset() {
        return true;
    }

    @Override
    public BufferOffset asBufferOffset() {
        return this;
    }
}
