package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.model.kernel.KernelModel;

public class CloseKernel extends AbstractKernelMethod {

    public CloseKernel(KernelModel kernelModel) {
        super(kernelModel);
    }

    @Override
    public String getMethodName() {
        return "close";
    }

    @Override
    public boolean isCloseKernel() {
        return true;
    }

    @Override
    public CloseKernel asCloseKernel() {
        return this;
    }
}
