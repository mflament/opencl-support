package org.yah.tools.opencl.codegen.model.kernel;

import org.yah.tools.opencl.codegen.model.kernel.param.EventBuffer;

public interface AsyncKernelMethod extends KernelMethod {

    EventBuffer getEventsBufferParameter();
}
