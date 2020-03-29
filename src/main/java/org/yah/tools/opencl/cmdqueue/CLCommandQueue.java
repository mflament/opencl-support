/**
 * 
 */
package org.yah.tools.opencl.cmdqueue;

import static org.lwjgl.BufferUtils.createPointerBuffer;
import static org.lwjgl.opencl.CL10.clCreateCommandQueue;
import static org.lwjgl.opencl.CL10.clEnqueueNDRangeKernel;
import static org.lwjgl.opencl.CL10.clEnqueueReadBuffer;
import static org.lwjgl.opencl.CL10.clEnqueueWriteBuffer;
import static org.lwjgl.opencl.CL10.clFinish;
import static org.lwjgl.opencl.CL10.clFlush;
import static org.lwjgl.opencl.CL10.clReleaseCommandQueue;
import static org.yah.tools.opencl.CLException.check;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.platform.DeviceInfo;

/**
 * @author Yah
 *
 */
public class CLCommandQueue implements CLObject {
    private long id;

    private final int maxDimensions;
    private final BigInteger maxWorkSize;
    private final long maxWorkGroupSize;
    private final long[] maxWorkItemSizes;

    /**
     * 
     */
    public CLCommandQueue(CLContext context, long device,
            CommandQueueProperties... properties) {
        id = CLException.apply(eb -> clCreateCommandQueue(context.getId(), device,
                CommandQueueProperties.combine(properties), eb));
        int addressBits = CLDevice.readDeviceInfo(device,
                DeviceInfo.DEVICE_ADDRESS_BITS,
                b -> b.getInt());
        maxWorkSize = BigInteger.valueOf(2).pow(addressBits);
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

    public KernelNDRange createKernelRange() {
        return new KernelNDRange();
    }

    public void run(CLKernel kernel, long[] globalWorkSizes) {
        KernelNDRange range = createKernelRange();
        range.globalWorkSizes(globalWorkSizes);
        run(kernel, range);
    }

    public long run(CLKernel kernel, KernelNDRange range) {
        check(clEnqueueNDRangeKernel(id, kernel.getId(), range.dimensions,
                range.globalWorkOffsetsBuffer(),
                range.globalWorkSizesBuffer(),
                range.localWorkSizesBuffer(),
                range.getEventWaitListBuffer(),
                range.getEventBuffer()));
        return range.flushEvent();
    }

    public void read(CLBuffer buffer, ByteBuffer target) {
        read(buffer, target, true, 0, EventsParams.EMPTY_PARAM);
    }

    public void read(CLBuffer buffer, IntBuffer target) {
        read(buffer, target, true, 0, EventsParams.EMPTY_PARAM);
    }

    public void read(CLBuffer buffer, FloatBuffer target) {
        read(buffer, target, true, 0, EventsParams.EMPTY_PARAM);
    }

    public void read(CLBuffer buffer, DoubleBuffer target) {
        read(buffer, target, true, 0, EventsParams.EMPTY_PARAM);
    }

    public long read(CLBuffer buffer, ByteBuffer target, boolean blocking, long offset, EventsParams events) {
        check(clEnqueueReadBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer buffer, IntBuffer target, boolean blocking, long offset, EventsParams events) {
        check(clEnqueueReadBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer buffer, FloatBuffer target, boolean blocking, long offset, EventsParams events) {
        check(clEnqueueReadBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer buffer, DoubleBuffer target, boolean blocking, long offset, EventsParams events) {
        check(clEnqueueReadBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public void write(CLBuffer buffer, ByteBuffer target) {
        write(buffer, target, true, 0, EventsParams.EMPTY_PARAM);
    }

    public long write(CLBuffer buffer, ByteBuffer target, boolean blocking, long offset,
            EventsParams events) {
        check(clEnqueueWriteBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public void flush() {
        check(clFlush(id));
    }

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

    public static class EventsParams {
        private static final int MIN_EVENTS_CAPACITY = 10;

        private static final EventsParams EMPTY_PARAM = new EventsParams();

        private PointerBuffer eventWaitListBuffer;

        private PointerBuffer eventBuffer = BufferUtils.createPointerBuffer(1).limit(0);

        public EventsParams reset() {
            if (eventWaitListBuffer != null)
                eventWaitListBuffer.limit(0);
            eventBuffer.limit(0);
            return this;
        }

        protected long flushEvent() {
            if (eventBuffer.hasRemaining()) { return eventBuffer.get(); }
            return 0;
        }

        public PointerBuffer getEventWaitListBuffer() {
            if (eventWaitListBuffer != null && eventWaitListBuffer.hasRemaining())
                return eventWaitListBuffer;
            return null;
        }

        public PointerBuffer getEventBuffer() {
            if (eventBuffer.hasRemaining())
                return eventBuffer;
            return null;
        }

        public void setEventWaitList(long event) {
            if (event == 0 && eventWaitListBuffer != null) {
                eventWaitListBuffer.position(0);
                eventWaitListBuffer.limit(0);
            } else if (event != 0) {
                ensureEventWaitListCapacity(1);
                eventWaitListBuffer.position(0);
                eventWaitListBuffer.put(event);
                eventWaitListBuffer.flip();
            }
        }
        
        public void setEventWaitList(long... events) {
            if (events.length == 0 && eventWaitListBuffer != null) {
                eventWaitListBuffer.position(0);
                eventWaitListBuffer.limit(0);
            } else if (events.length > 0) {
                ensureEventWaitListCapacity(events.length);
                eventWaitListBuffer.position(0);
                for (int i = 0; i < events.length; i++) {
                    eventWaitListBuffer.put(events[i]);
                }
                eventWaitListBuffer.flip();
            }
        }

        public void requestEvent() {
            this.eventBuffer.limit(1);
        }

        private void ensureEventWaitListCapacity(int capacity) {
            if (eventWaitListBuffer == null || eventWaitListBuffer.capacity() < capacity)
                eventWaitListBuffer = createPointerBuffer(Math.max(capacity, MIN_EVENTS_CAPACITY));
            eventWaitListBuffer.limit(eventWaitListBuffer.capacity());
        }
    }

    private static BigInteger mul(PointerBuffer buffer) {
        BigInteger res = BigInteger.valueOf(0);
        for (int i = 0; i < buffer.remaining(); i++) {
            res.add(BigInteger.valueOf(buffer.get(i)));
        }
        return res;
    }

    public class KernelNDRange extends EventsParams {
        private int dimensions;
        private final PointerBuffer globalWorkSizesBuffer;
        private PointerBuffer globalWorkOffsetsBuffer;

        private PointerBuffer localWorkSizesBuffer;

        public KernelNDRange() {
            globalWorkSizesBuffer = createPointerBuffer(maxDimensions);
        }

        public BigInteger workSize() {
            return mul(globalWorkSizesBuffer);
        }

        public int workGroupSize() {
            return mul(localWorkSizesBuffer).intValue();
        }

        public void reset(long... sizes) {
            reset();
            globalWorkSizes(sizes);
        }

        @Override
        public KernelNDRange reset() {
            super.reset();
            dimensions = 0;
            globalWorkSizesBuffer.limit(0);
            if (globalWorkSizesBuffer != null)
                globalWorkSizesBuffer.limit(0);
            if (localWorkSizesBuffer != null)
                localWorkSizesBuffer.limit(0);
            return this;
        }

        public PointerBuffer globalWorkSizesBuffer() {
            return globalWorkSizesBuffer;
        }

        public PointerBuffer globalWorkOffsetsBuffer() {
            if (globalWorkOffsetsBuffer != null && globalWorkOffsetsBuffer.hasRemaining())
                return globalWorkOffsetsBuffer;
            return null;
        }

        public PointerBuffer localWorkSizesBuffer() {
            if (localWorkSizesBuffer != null && localWorkSizesBuffer.hasRemaining())
                return localWorkSizesBuffer;
            return localWorkSizesBuffer;
        }

        public void globalWorkSizes(long... sizes) {
            int dim = sizes.length;
            if (dim < 1 || dim > maxDimensions)
                throw new IllegalArgumentException("dim " + dim + " is out of bound [1," + maxDimensions + "]");
            this.dimensions = dim;
            globalWorkSizesBuffer.limit(dim);
            if (globalWorkOffsetsBuffer != null)
                globalWorkOffsetsBuffer.limit(dim);
            if (localWorkSizesBuffer != null)
                localWorkSizesBuffer.limit(dim);
            for (int d = 0; d < sizes.length; d++) {
                globalWorkSizesBuffer.put(d, sizes[d]);
            }
        }

        public void globalWorkOffsets(long... offsets) {
            checkDimension(offsets.length);
            if (globalWorkOffsetsBuffer == null) {
                globalWorkOffsetsBuffer = createPointerBuffer(maxDimensions);
                globalWorkOffsetsBuffer.limit(dimensions);
            }
            for (int d = 0; d < offsets.length; d++) {
                globalWorkOffsetsBuffer.put(d, offsets[d]);
            }
        }

        public void localWorkSizes(long... sizes) {
            checkDimension(sizes.length);
            if (localWorkSizesBuffer == null) {
                localWorkSizesBuffer = createPointerBuffer(maxDimensions);
                localWorkSizesBuffer.limit(dimensions);
            }
            localWorkSizesBuffer.put(sizes);
            localWorkSizesBuffer.flip();
        }

        public void validate() {
            if (workSize().compareTo(maxWorkSize) > 0)
                throw new IllegalArgumentException(
                        String.format("Invalid workSize %d, max %d", workSize(), maxWorkSize));

            if (localWorkSizesBuffer != null) {
                long total = 1;
                for (int d = 0; d < localWorkSizesBuffer.remaining(); d++) {
                    long size = localWorkSizesBuffer.get(d);
                    if (size > maxWorkItemSizes[d]) {
                        throw new IllegalArgumentException(
                                String.format("Invalid localWorkSize[%d]=%d, max %d", d, size, maxWorkItemSizes[d]));
                    }
                    total *= size;
                }

                if (total > maxWorkGroupSize) {
                    throw new IllegalArgumentException(
                            String.format("Invalid total work group size %d, max %d", total,
                                    maxWorkGroupSize));
                }
            }
        }

        private void checkDimension(int dim) {
            if (dim > dimensions)
                throw new IllegalArgumentException("dim " + dim + " is out of bound [0," + dimensions + "[");
        }

    }

}
