package org.yah.tools.opencl.cmdqueue;

import java.nio.ByteBuffer;
import java.util.List;

import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.CLBuffer;

public interface CLCommandQueue extends CLObject {

    default void run(CLKernel kernel, long[] globalWorkSizes) {
        run(kernel, null, globalWorkSizes, null, null, null);
    }

    void run(CLKernel kernel, long[] globalWorkOffsets, long[] globalWorkSizes,
            long[] localWorkSizes, List<CLEvent> eventWaitList, CLEvent event);

    void read(CLBuffer buffer, ByteBuffer target, boolean blocking, long offset,
            List<CLEvent> eventWaitList, CLEvent event);

    default void read(CLBuffer buffer, ByteBuffer target) {
        read(buffer, target, true, 0, null, null);
    }

    void write(CLBuffer buffer, ByteBuffer target, boolean blocking, long offset,
            List<CLEvent> eventWaitList, CLEvent event);

    default void write(CLBuffer buffer, ByteBuffer target) {
        write(buffer, target, true, 0, null, null);
    }

    void finish();

}