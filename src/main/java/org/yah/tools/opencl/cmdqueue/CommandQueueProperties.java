package org.yah.tools.opencl.cmdqueue;

import static org.lwjgl.opencl.CL10.CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE;
import static org.lwjgl.opencl.CL10.CL_QUEUE_PROFILING_ENABLE;

import java.util.EnumSet;
import java.util.Set;

import org.yah.tools.opencl.CLEnum;
import org.yah.tools.opencl.CLVersion;

public enum CommandQueueProperties implements CLEnum {
    QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE(CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE),
    QUEUE_PROFILING_ENABLE(CL_QUEUE_PROFILING_ENABLE);

    private final int id;

    CommandQueueProperties(int id) {
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

    public static long combine(CommandQueueProperties... props) {
        long res = 0;
        for (CommandQueueProperties p : props) {
            res |= p.id;
        }
        return res;
    }

    public static Set<CommandQueueProperties> setOf(CommandQueueProperties prop, CommandQueueProperties... props) {
        return EnumSet.of(prop, props);
    }
}
