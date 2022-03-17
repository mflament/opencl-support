package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;

import javax.annotation.Nullable;

abstract class AbstractKernelArgumentMethodParameter extends AbstractKernelMethodParameter {

    public AbstractKernelArgumentMethodParameter(KernelArgumentMethod kernelMethod, int parameterIndex, Class<?> parameterType,
                                                 @Nullable String defaultValue) {
        super(kernelMethod, parameterIndex, parameterType, defaultValue);
    }

    @Override
    public KernelArgumentMethod getMethod() {
        return (KernelArgumentMethod) super.getMethod();
    }

}
