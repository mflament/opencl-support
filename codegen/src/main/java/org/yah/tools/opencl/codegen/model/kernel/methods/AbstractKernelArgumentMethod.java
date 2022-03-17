package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.param.Buffer;
import org.yah.tools.opencl.codegen.model.kernel.param.BufferOffset;
import org.yah.tools.opencl.codegen.model.kernel.param.EventBuffer;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;

import java.util.Objects;

abstract class AbstractKernelArgumentMethod extends AbstractKernelMethod implements KernelArgumentMethod {

    protected final ParsedKernelArgument parsedKernelArgument;

    public AbstractKernelArgumentMethod(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument) {
        super(kernelModel);
        this.parsedKernelArgument = Objects.requireNonNull(parsedKernelArgument, "parsedKernelArgument is null");
    }

    public ParsedKernelArgument getParsedKernelArgument() {
        return parsedKernelArgument;
    }

    @Override
    public final boolean isKernelArgumentMethod() {
        return true;
    }

    @Override
    public final KernelArgumentMethod asKernelArgumentMethod() {
        return this;
    }

    protected final EventBuffer createBufferParameters(Class<?> bufferClass) {
        parameters.add(new Buffer(this, bufferClass));
        parameters.add(new BufferOffset(this));
        EventBuffer eventsBuffer = new EventBuffer(this, parameters.size());
        parameters.add(eventsBuffer);
        return eventsBuffer;
    }
}
