/**
 * 
 */
package org.yah.tools.opencl.cmdqueue;

import static org.lwjgl.opencl.CL10.clCreateCommandQueue;
import static org.lwjgl.opencl.CL10.clEnqueueNDRangeKernel;
import static org.lwjgl.opencl.CL10.clEnqueueReadBuffer;
import static org.lwjgl.opencl.CL10.clEnqueueWriteBuffer;
import static org.lwjgl.opencl.CL10.clFinish;
import static org.lwjgl.opencl.CL10.clReleaseCommandQueue;
import static org.yah.tools.opencl.CLException.check;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.platform.DeviceInfo;

/**
 * @author Yah
 *
 */
public class DefaultCLCommandQueue implements CLCommandQueue {
    private long id;

    private final int maxDimensions;
    private final long maxWorkGroupSize;
    private final long[] maxWorkItemSizes;

    /**
     * 
     */
    public DefaultCLCommandQueue(CLContext context, long device,
            Set<CommandQueueProperties> properties) {
        id = CLException.apply(eb -> clCreateCommandQueue(context.getId(), device,
                CommandQueueProperties.combine(properties), eb));
        maxDimensions = CLDevice.readDeviceInfo(device,
                DeviceInfo.DEVICE_MAX_WORK_ITEM_DIMENSIONS,
                b -> b.getInt());
        maxWorkGroupSize = CLDevice.readDeviceInfo(device, DeviceInfo.DEVICE_MAX_WORK_GROUP_SIZE,
                b -> PointerBuffer.get(b));
        maxWorkItemSizes = CLDevice.readDeviceInfo(device, DeviceInfo.DEVICE_MAX_WORK_ITEM_SIZES,
                b -> {
                    long[] sizes = new long[maxDimensions];
                    for (int i = 0; i < maxDimensions; i++) {
                        sizes[i] = PointerBuffer.get(b);
                    }
                    return sizes;
                });
    }

    @Override
    public long getId() { return id; }

    @Override
    public void run(CLKernel kernel, long[] globalWorkOffsets,
            long[] globalWorkSizes,
            long[] localWorkSizes,
            List<CLEvent> eventWaitList, CLEvent event) {
        Objects.requireNonNull(kernel);
        Objects.requireNonNull(globalWorkSizes);

        if (globalWorkSizes.length < 1 || globalWorkSizes.length > maxDimensions) {
            throw new IllegalArgumentException(
                    String.format("Invalid globalWorkSizes length %d, must be [1,%d]",
                            globalWorkSizes.length, maxDimensions));
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer globalWorkSizeBuffer = stack.mallocPointer(globalWorkSizes.length);
            for (int i = 0; i < globalWorkSizes.length; i++) {
                globalWorkSizeBuffer.put(globalWorkSizes[i]);
            }
            globalWorkSizeBuffer.flip();

            PointerBuffer globalWorkOffsetBuffer = null;
            if (globalWorkOffsets != null) {
                if (globalWorkOffsets.length != globalWorkSizes.length) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "Invalid globalWorkOffsets length %d, mismatched from globalWorkSize length %d",
                                    globalWorkOffsets.length, globalWorkSizes.length));
                }

                globalWorkOffsetBuffer = stack.mallocPointer(globalWorkSizes.length);
                for (int i = 0; i < globalWorkOffsets.length; i++) {
                    globalWorkOffsetBuffer.put(globalWorkOffsets[i]);
                }
                globalWorkOffsetBuffer.flip();
            }

            PointerBuffer localWorkSizeBuffer = null;
            if (localWorkSizes != null) {
                if (localWorkSizes.length != globalWorkSizes.length) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "Invalid localWorkSize length %d, mismatched from globalWorkSize length %d",
                                    localWorkSizes.length, globalWorkSizes.length));
                }
                localWorkSizeBuffer = stack.mallocPointer(localWorkSizes.length);
                int total = 1;
                for (int i = 0; i < localWorkSizes.length; i++) {
                    if (localWorkSizes[i] > maxWorkItemSizes[i]) {
                        throw new IllegalArgumentException(
                                String.format("Invalid localWorkSize[%d]=%d, max %d", i,
                                        localWorkSizes[i], maxWorkItemSizes[i]));
                    }
                    total *= localWorkSizes[i];
                    localWorkSizeBuffer.put(localWorkSizes[i]);
                }
                localWorkSizeBuffer.flip();
                if (total > maxWorkGroupSize) {
                    throw new IllegalArgumentException(
                            String.format("Invalid total work group size %d, max %d", total,
                                    maxWorkGroupSize));
                }
            }

            check(clEnqueueNDRangeKernel(id, kernel.getId(), globalWorkSizes.length,
                    globalWorkOffsetBuffer,
                    globalWorkSizeBuffer,
                    localWorkSizeBuffer,
                    eventBuffer(stack, eventWaitList), eventBuffer(stack, event)));
        }
    }

    @Override
    public void read(CLBuffer buffer, ByteBuffer target, boolean blocking, long offset,
            List<CLEvent> eventWaitList, CLEvent event) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            check(clEnqueueReadBuffer(id, buffer.getId(), blocking, offset, target,
                    eventBuffer(stack, eventWaitList), eventBuffer(stack, event)));
        }
    }

    @Override
    public void write(CLBuffer buffer, ByteBuffer target, boolean blocking, long offset,
            List<CLEvent> eventWaitList, CLEvent event) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            check(clEnqueueWriteBuffer(id, buffer.getId(), blocking, offset, target,
                    eventBuffer(stack, eventWaitList), eventBuffer(stack, event)));
        }
    }

    @Override
    public void finish() {
        check(clFinish(id));
    }

    @Override
    public void close() {
        if (id != 0) {
            clReleaseCommandQueue(id);
            id = 0;
        }
    }

    private static PointerBuffer eventBuffer(MemoryStack stack, CLEvent event) {
        if (event == null)
            return null;
        PointerBuffer eventBuffer = stack.mallocPointer(1);
        eventBuffer.put(event.getId());
        eventBuffer.flip();
        return eventBuffer;
    }

    private static PointerBuffer eventBuffer(MemoryStack stack, List<CLEvent> eventWaitList) {
        if (eventWaitList == null)
            return null;

        PointerBuffer ewlBuffer = stack.mallocPointer(eventWaitList.size());
        for (CLEvent e : eventWaitList) {
            ewlBuffer.put(e.getId());
        }
        ewlBuffer.flip();
        return ewlBuffer;
    }

}
