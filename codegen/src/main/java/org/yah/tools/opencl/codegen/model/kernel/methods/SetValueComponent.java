package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.param.Value;
import org.yah.tools.opencl.codegen.model.kernel.param.ValueComponent;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;

import java.util.Optional;

public class SetValueComponent extends AbstractSetKernelArgumentMethod {

    public SetValueComponent(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument, int componentCount) {
        super(kernelModel, parsedKernelArgument);
        for (int i = 0; i < componentCount; i++)
            parameters.add(new ValueComponent(this, i));
    }

    @Override
    public boolean isSetValueComponent() {
        return true;
    }

    @Override
    public SetValueComponent asSetValueComponent() {
        return this;
    }

    @Override
    public Optional<KernelMethodParameter> getInvokeParameter() {
        return Optional.empty();
    }

}
