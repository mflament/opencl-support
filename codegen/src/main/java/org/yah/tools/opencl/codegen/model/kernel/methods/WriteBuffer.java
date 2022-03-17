package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.model.kernel.AsyncKernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.param.BufferProperties;
import org.yah.tools.opencl.codegen.model.kernel.param.EventBuffer;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;

import java.util.Optional;

public class WriteBuffer extends AbstractSetKernelArgumentMethod implements AsyncKernelMethod {

    private final EventBuffer eventsBuffer;

    public WriteBuffer(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument, Class<?> bufferClass) {
        super(kernelModel, parsedKernelArgument);
        eventsBuffer = createBufferParameters(bufferClass);
    }

    @Override
    public boolean isWriteBuffer() {
        return true;
    }

    @Override
    public WriteBuffer asWriteBuffer() {
        return this;
    }

    @Override
    public boolean isAsyncKernelMethod() {
        return true;
    }

    @Override
    public AsyncKernelMethod asAsyncKernelMethod() {
        return this;
    }

    @Override
    public EventBuffer getEventsBufferParameter() {
        return eventsBuffer;
    }

    @Override
    public Optional<KernelMethodParameter> getInvokeParameter() {
        return Optional.of(parameters.get(0));
    }
}
