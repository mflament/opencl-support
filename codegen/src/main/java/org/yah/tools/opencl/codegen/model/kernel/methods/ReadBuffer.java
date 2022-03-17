package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.model.kernel.AsyncKernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.param.EventBuffer;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;

public class ReadBuffer extends AbstractKernelArgumentMethod implements AsyncKernelMethod {

    private final EventBuffer eventsBuffer;

    public ReadBuffer(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument, Class<?> bufferClass) {
        super(kernelModel, parsedKernelArgument);
        eventsBuffer = createBufferParameters(bufferClass);
    }

    @Override
    public boolean isReadBuffer() {
        return true;
    }

    @Override
    public ReadBuffer asReadBuffer() {
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
}
