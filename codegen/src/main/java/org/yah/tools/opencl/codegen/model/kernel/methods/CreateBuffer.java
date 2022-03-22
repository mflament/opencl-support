package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.param.Buffer;
import org.yah.tools.opencl.codegen.model.kernel.param.BufferProperties;
import org.yah.tools.opencl.codegen.model.kernel.param.BufferSize;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.type.CLType;

import java.lang.reflect.Type;
import java.util.Optional;

public class CreateBuffer extends AbstractSetKernelArgumentMethod {

    public static CreateBuffer fromBuffer(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument) {
        CreateBuffer cb = new CreateBuffer(kernelModel, parsedKernelArgument);
        cb.parameters.add(0, new Buffer(cb));
        return cb;
    }

    public static CreateBuffer fromSize(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument) {
        CreateBuffer cb = new CreateBuffer(kernelModel, parsedKernelArgument);
        cb.parameters.add(0, new BufferSize(cb));
        return cb;
    }

    private CreateBuffer(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument) {
        super(kernelModel, parsedKernelArgument);
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
