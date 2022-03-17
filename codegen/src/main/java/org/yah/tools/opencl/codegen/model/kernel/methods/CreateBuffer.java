package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.param.Buffer;
import org.yah.tools.opencl.codegen.model.kernel.param.BufferProperties;
import org.yah.tools.opencl.codegen.model.kernel.param.BufferSize;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;

import java.util.Optional;
import java.util.function.Function;

public class CreateBuffer extends AbstractSetKernelArgumentMethod {

    public CreateBuffer(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument, Class<?> bufferClass) {
        this(kernelModel, parsedKernelArgument, m -> new Buffer(m, bufferClass));
    }

    public CreateBuffer(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument, int itemSize) {
        this(kernelModel, parsedKernelArgument, m -> new BufferSize(m, itemSize));
    }

    private CreateBuffer(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument,
                         Function<CreateBuffer, KernelMethodParameter> sourceParamFactory) {
        super(kernelModel, parsedKernelArgument);
        parameters.add(sourceParamFactory.apply(this));
        parameters.add(new BufferProperties(this, parameters.size()));
    }

    @Override
    public boolean isCreateBuffer() {
        return true;
    }

    @Override
    public CreateBuffer asCreateBuffer() {
        return this;
    }

    @Override
    public Optional<KernelMethodParameter> getInvokeParameter() {
        return Optional.of(parameters.get(0));
    }
}
