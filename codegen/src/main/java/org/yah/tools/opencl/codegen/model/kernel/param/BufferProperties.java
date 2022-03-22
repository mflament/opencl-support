package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;

public class BufferProperties extends AbstractKernelArgumentMethodParameter {

    public BufferProperties(KernelArgumentMethod kernelMethod, int parameterIndex) {
        super(kernelMethod, parameterIndex, null);
    }

    @Override
    public boolean isBufferProperties() {
        return true;
    }

    @Override
    public BufferProperties asBufferProperties() {
        return this;
    }

}
