package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;

public class BufferSize extends AbstractKernelArgumentMethodParameter {

    /**
     * buffer item size, in bytes
     */
    private final int itemSize;

    public BufferSize(KernelArgumentMethod kernelMethod, int itemSize) {
        super(kernelMethod, 0, Long.TYPE, null);
        this.itemSize = itemSize;
    }

    public int getItemSize() {
        return itemSize;
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
