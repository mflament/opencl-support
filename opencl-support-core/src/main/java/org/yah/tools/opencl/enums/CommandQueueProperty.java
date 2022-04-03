package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

import static org.lwjgl.opencl.CL10.CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE;
import static org.lwjgl.opencl.CL10.CL_QUEUE_PROFILING_ENABLE;

public enum CommandQueueProperty implements CLEnum {

    QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE(CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE),
    QUEUE_PROFILING_ENABLE(CL_QUEUE_PROFILING_ENABLE);

    private final int id;

    CommandQueueProperty(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public CLVersion version() {
        return CLVersion.CL10;
    }

    public static long all(CommandQueueProperty... props) {
        long res = 0;
        for (CommandQueueProperty p : props) {
            res |= p.id;
        }
        return res;
    }

}
