package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.param.Value;
import org.yah.tools.opencl.codegen.model.kernel.param.ValueComponent;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;

import java.util.Optional;

public class SetValue extends AbstractSetKernelArgumentMethod {

    public SetValue(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument) {
        super(kernelModel, parsedKernelArgument);
        parameters.add(new Value(this, 0));
    }

    @Override
    public boolean isSetValue() {
        return true;
    }

    @Override
    public SetValue asSetValue() {
        return this;
    }

    @Override
    public Optional<KernelMethodParameter> getInvokeParameter() {
        return Optional.of(parameters.get(0));
    }

}
