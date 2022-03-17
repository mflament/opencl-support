package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.param.Value;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;

import java.util.Optional;

public class SetValue extends AbstractSetKernelArgumentMethod {

    public SetValue(KernelModel kernelModel,
                    ParsedKernelArgument parsedKernelArgument,
                    Class<?> valueClass) {
        this(kernelModel, parsedKernelArgument, valueClass, 1);
    }

    public SetValue(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument, Class<?> valueClass, int parametersCount) {
        super(kernelModel, parsedKernelArgument);
        for (int i = 0; i < parametersCount; i++)
            parameters.add(new Value(this, i, valueClass));
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
        return parameters.size() == 1 ? Optional.of(parameters.get(0)) : Optional.empty();
    }

}
