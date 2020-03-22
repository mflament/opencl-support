package org.yah.tools.opencl.cmdqueue;

import static org.lwjgl.opencl.CL10.CL_COMPLETE;
import static org.lwjgl.opencl.CL10.clReleaseEvent;
import static org.lwjgl.opencl.CL11.clCreateUserEvent;
import static org.lwjgl.opencl.CL11.clSetEventCallback;

import java.util.function.Consumer;

import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.context.CLContext;

public class CLEvent implements CLObject {

    private long id;

    public CLEvent() {}

    public CLEvent(CLContext context) {
        id = CLException.apply(eb -> clCreateUserEvent(context.getId(), eb));
    }

    public void setCallback(Consumer<CLEvent> callback) {
        if (id == 0)
            throw new IllegalStateException("Event id not set");
        clSetEventCallback(id, CL_COMPLETE, (e, s, ud) -> callback.accept(this), 0);
    }

    @Override
    public long getId() { return id; }

    @Override
    public void close() {
        if (id != 0) {
            clReleaseEvent(id);
            id = 0;
        }
    }

    void setId(long id) { this.id = id; }

}
