package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;

public class EventBuffer extends AbstractKernelMethodParameter {

    public EventBuffer(KernelMethod kernelMethod, int parameterIndex) {
        super(kernelMethod, parameterIndex, "null");
    }

    @Override
    public boolean isEventBuffer() {
        return true;
    }

    @Override
    public EventBuffer asEventBuffer() {
        return this;
    }

}
