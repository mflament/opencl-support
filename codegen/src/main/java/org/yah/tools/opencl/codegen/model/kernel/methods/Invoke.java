package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.model.kernel.AsyncKernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.SetKernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.param.EventBuffer;
import org.yah.tools.opencl.codegen.model.kernel.param.InvokeArgumentParameter;
import org.yah.tools.opencl.codegen.model.kernel.param.InvokeRangeParameter;

import java.util.Collections;
import java.util.List;

public class Invoke extends AbstractKernelMethod implements AsyncKernelMethod {

    private final EventBuffer eventsBuffer;

    private final boolean abstractInvoke;

    public Invoke(KernelModel kernelModel) {
        this(kernelModel, Collections.emptyList());
    }

    public Invoke(KernelModel kernelModel, List<SetKernelArgumentMethod> argumentSetters) {
        super(kernelModel);
        parameters.add(new InvokeRangeParameter(this));
        for (SetKernelArgumentMethod argumentSetter : argumentSetters) {
            parameters.add(new InvokeArgumentParameter(this, parameters.size(), argumentSetter));
        }
        parameters.add(eventsBuffer = new EventBuffer(this, parameters.size()));
        abstractInvoke = argumentSetters.isEmpty();
    }

    public boolean isAbstractInvoke() {
        return abstractInvoke;
    }

    public InvokeRangeParameter getRangetParameter() {
        return (InvokeRangeParameter) parameters.get(0);
    }

    @Override
    public boolean isInvoke() {
        return true;
    }

    @Override
    public Invoke asInvoke() {
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
