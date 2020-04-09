package org.yah.tools.opencl.cmdqueue;

import static org.lwjgl.BufferUtils.createPointerBuffer;
import static org.lwjgl.opencl.CL10.clCreateCommandQueue;
import static org.lwjgl.opencl.CL10.clEnqueueNDRangeKernel;
import static org.lwjgl.opencl.CL10.clEnqueueReadBuffer;
import static org.lwjgl.opencl.CL10.clEnqueueWriteBuffer;
import static org.lwjgl.opencl.CL10.clFinish;
import static org.lwjgl.opencl.CL10.clFlush;
import static org.lwjgl.opencl.CL10.clReleaseCommandQueue;
import static org.lwjgl.opencl.CL10.clWaitForEvents;
import static org.yah.tools.opencl.CLException.check;

import java.math.BigInteger;
import java.nio.*;

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
 * @noinspection UnusedReturnValue
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
                ByteBuffer::getInt);
        maxWorkSize = BigInteger.valueOf(2).pow(addressBits);
        maxDimensions = CLDevice.readDeviceInfo(device,
                DeviceInfo.DEVICE_MAX_WORK_ITEM_DIMENSIONS,
                ByteBuffer::getInt);
        maxWorkGroupSize = CLDevice.readDeviceInfo(device, DeviceInfo.DEVICE_MAX_WORK_GROUP_SIZE,
                PointerBuffer::get);
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
    public long getId() {
        return id;
    }

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

    public void write(CLBuffer buffer, IntBuffer target) {
        write(buffer, target, true, 0, EventsParams.EMPTY_PARAM);
    }

    public void write(CLBuffer buffer, DoubleBuffer target) {
        write(buffer, target, true, 0, EventsParams.EMPTY_PARAM);
    }

    public void write(CLBuffer buffer, FloatBuffer target) {
        write(buffer, target, true, 0, EventsParams.EMPTY_PARAM);
    }

    public long write(CLBuffer buffer, IntBuffer target, boolean blocking, long offset,
                      EventsParams events) {
        check(clEnqueueWriteBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long write(CLBuffer buffer, DoubleBuffer target, boolean blocking, long offset,
                      EventsParams events) {
        check(clEnqueueWriteBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long write(CLBuffer buffer, FloatBuffer target, boolean blocking, long offset,
                      EventsParams events) {
        check(clEnqueueWriteBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long write(CLBuffer buffer, ByteBuffer target, boolean blocking, long offset,
                      EventsParams events) {
        check(clEnqueueWriteBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public void waitForEvents(EventsParams params) {
        PointerBuffer ewl = params.getEventWaitListBuffer();
        if (ewl != null)
            check(clWaitForEvents(ewl));
    }

    public void waitForEvent(long event) {
        check(clWaitForEvents(event));
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

        private final PointerBuffer eventBuffer = BufferUtils.createPointerBuffer(1).limit(0);

        public EventsParams reset() {
            if (eventWaitListBuffer != null)
                eventWaitListBuffer.limit(0);
            eventBuffer.limit(0);
            return this;
        }

        public EventsParams waitForEvent(long event) {
            if (event == 0 && eventWaitListBuffer != null) {
                eventWaitListBuffer.position(0).limit(0);
            } else if (event != 0) {
                ensureEventWaitListCapacity(1);
                eventWaitListBuffer.position(0).put(event).flip();
            }
            return this;
        }

        public EventsParams waitForEvents(long... events) {
            ensureEventWaitListCapacity(events.length);
            for (long event : events) {
                if (event > 0)
                    eventWaitListBuffer.put(event);
            }
            eventWaitListBuffer.flip();
            return this;
        }

        public EventsParams dontWaitForEvents() {
            if (eventWaitListBuffer != null)
                eventWaitListBuffer.position(0).limit(0);
            return this;
        }

        public EventsParams requestEvent() {
            this.eventBuffer.position(0).limit(1);
            return this;
        }

        private void ensureEventWaitListCapacity(int capacity) {
            if (eventWaitListBuffer == null || eventWaitListBuffer.capacity() < capacity)
                eventWaitListBuffer = createPointerBuffer(Math.max(capacity, MIN_EVENTS_CAPACITY));
            eventWaitListBuffer.position(0).limit(eventWaitListBuffer.capacity());
        }

        protected long flushEvent() {
            if (eventBuffer.hasRemaining()) {
                return eventBuffer.get();
            }
            return 0;
        }

        PointerBuffer getEventWaitListBuffer() {
            if (eventWaitListBuffer != null && eventWaitListBuffer.hasRemaining())
                return eventWaitListBuffer;
            return null;
        }

        PointerBuffer getEventBuffer() {
            if (eventBuffer.hasRemaining())
                return eventBuffer;
            return null;
        }
    }

    public final class KernelNDRange extends EventsParams {
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

        public KernelNDRange reset(long... sizes) {
            reset();
            return globalWorkSizes(sizes);
        }

        @Override
        public KernelNDRange reset() {
            super.reset();
            dimensions = 0;
            globalWorkSizesBuffer.limit(0);
            if (globalWorkOffsetsBuffer != null)
                globalWorkOffsetsBuffer.limit(0);
            if (localWorkSizesBuffer != null)
                localWorkSizesBuffer.limit(0);
            return this;
        }

        public PointerBuffer localWorkSizesBuffer() {
            if (localWorkSizesBuffer != null && localWorkSizesBuffer.hasRemaining())
                return localWorkSizesBuffer;
            return null;
        }

        public KernelNDRange globalWorkSizes(long... sizes) {
            int dim = sizes.length;
            if (dim < 1 || dim > maxDimensions)
                throw new IllegalArgumentException("dim " + dim + " is out of bound [1," + maxDimensions + "]");
            this.dimensions = dim;
            globalWorkSizesBuffer
                    .limit(dim)
                    .put(sizes)
                    .flip();
            if (globalWorkOffsetsBuffer != null)
                globalWorkOffsetsBuffer.limit(dim);
            if (localWorkSizesBuffer != null)
                localWorkSizesBuffer.limit(dim);
            return this;
        }

        public KernelNDRange globalWorkOffsets(long... offsets) {
            checkDimension(offsets.length);
            if (globalWorkOffsetsBuffer == null) {
                globalWorkOffsetsBuffer = createPointerBuffer(maxDimensions);
            }
            globalWorkOffsetsBuffer.limit(dimensions).put(offsets).flip();
            return this;
        }

        public KernelNDRange localWorkSizes(long... sizes) {
            checkDimension(sizes.length);
            if (localWorkSizesBuffer == null) {
                localWorkSizesBuffer = createPointerBuffer(maxDimensions);
            }
            localWorkSizesBuffer.limit(dimensions).put(sizes).flip();
            return this;
        }

        public KernelNDRange validate() {
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
            return this;
        }

        private void checkDimension(int dim) {
            if (dim > dimensions)
                throw new IllegalArgumentException("dim " + dim + " is out of bound [0," + dimensions + "[");
        }

        PointerBuffer globalWorkSizesBuffer() {
            return globalWorkSizesBuffer;
        }

        PointerBuffer globalWorkOffsetsBuffer() {
            if (globalWorkOffsetsBuffer != null && globalWorkOffsetsBuffer.hasRemaining())
                return globalWorkOffsetsBuffer;
            return null;
        }

    }

    private static BigInteger mul(PointerBuffer buffer) {
        BigInteger res = BigInteger.valueOf(0);
        for (int i = 0; i < buffer.remaining(); i++) {
            res = res.add(BigInteger.valueOf(buffer.get(i)));
        }
        return res;
    }

}
