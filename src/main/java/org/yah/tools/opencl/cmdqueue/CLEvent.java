package org.yah.tools.opencl.cmdqueue;

import static org.lwjgl.opencl.CL10.clReleaseEvent;
import static org.lwjgl.opencl.CL11.clCreateUserEvent;

import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.context.DefaultCLContext;

public class CLEvent implements CLObject {

    private long id;

    public CLEvent(DefaultCLContext context) {
        id = CLException.apply(eb -> clCreateUserEvent(context.getId(), eb));
    }

    @Override
    public long getId() { return id; }

    @Override
    public void close() throws Exception {
        if (id != 0) {
            clReleaseEvent(id);
            id = 0;
        }
    }

}
