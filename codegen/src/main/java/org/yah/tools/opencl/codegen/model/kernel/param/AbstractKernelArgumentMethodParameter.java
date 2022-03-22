package org.yah.tools.opencl.codegen.model.kernel.param;

import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethodParameter;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.type.CLType;

import javax.annotation.Nullable;

abstract class AbstractKernelArgumentMethodParameter extends AbstractKernelMethodParameter implements KernelArgumentMethodParameter {

    public AbstractKernelArgumentMethodParameter(KernelArgumentMethod kernelMethod, int parameterIndex, @Nullable String defaultValue) {
        super(kernelMethod, parameterIndex, defaultValue);
    }

    @Override
    public KernelArgumentMethod getMethod() {
        return (KernelArgumentMethod) super.getMethod();
    }

    @Override
    public ParsedKernelArgument getParsedKernelArgument() {
        return getMethod().getParsedKernelArgument();
    }

}
