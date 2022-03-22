package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.param.BufferSize;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.type.CLType;

import java.util.Optional;

public class SetLocalSize extends AbstractSetKernelArgumentMethod {

    public SetLocalSize(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument) {
        super(kernelModel, parsedKernelArgument);
        parameters.add(new BufferSize(this));
    }

    @Override
    public boolean isSetLocalSize() {
        return true;
    }

    @Override
    public SetLocalSize asSetLocalSize() {
        return this;
    }

    @Override
    public Optional<KernelMethodParameter> getInvokeParameter() {
        return Optional.of(parameters.get(0));
    }
}
