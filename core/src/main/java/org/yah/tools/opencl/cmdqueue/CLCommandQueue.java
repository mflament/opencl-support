package org.yah.tools.opencl.cmdqueue;

import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.enums.CommandQueueProperty;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.platform.CLDevice;

import java.nio.*;
import java.util.Objects;

import static org.lwjgl.opencl.CL12.*;
import static org.yah.tools.opencl.CLException.check;

/**
 * @author Yah
 * @noinspection UnusedReturnValue
 */
public class CLCommandQueue implements CLObject {

    private final CLContext context;
    private final CLDevice device;
    private final long id;

    /**
     *
     */
    private CLCommandQueue(Builder builder) {
        this.context = Objects.requireNonNull(builder.context, "context is null");
        this.device = Objects.requireNonNull(builder.device, "device is null");
        long clproperties = CommandQueueProperty.all(builder.properties);
        id = CLException.apply(eb -> clCreateCommandQueue(context.getId(), device.getId(), clproperties, eb));
    }

    @Override
    public long getId() {
        return id;
    }

    public CLContext getContext() {
        return context;
    }

    public CLDevice getDevice() {
        return device;
    }

    public long run(CLKernel kernel, NDRange range) {
        check(clEnqueueNDRangeKernel(id, kernel.getId(),
                range.getDimensions(),
                range.getGlobalWorkOffsetsBuffer(),
                range.getGlobalWorkSizesBuffer(),
                range.getLocalWorkSizesBuffer(),
                range.getEventWaitListBuffer(),
                range.getEventBuffer()));
        return range.flushEvent();
    }

    public void read(CLBuffer source, ByteBuffer target) {
        read(source, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public void read(CLBuffer source, IntBuffer target) {
        read(source, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public void read(CLBuffer source, FloatBuffer target) {
        read(source, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public void read(CLBuffer source, DoubleBuffer target) {
        read(source, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public long read(CLBuffer source, ByteBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, source.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }


    public long read(CLBuffer source, ShortBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, source.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer source, IntBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, source.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer source, FloatBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, source.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer source, DoubleBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, source.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer source, short[] target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, source.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer source, int[] target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, source.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer source, float[] target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, source.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer source, double[] target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, source.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public void write(ByteBuffer source, CLBuffer target) {
        write(source, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public void write(IntBuffer source, CLBuffer target) {
        write(source, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public void write(DoubleBuffer source, CLBuffer target) {
        write(source, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public void write(FloatBuffer source, CLBuffer target) {
        write(source, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public long write(IntBuffer source, CLBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueWriteBuffer(id, target.getId(), blocking, offset, source,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long write(DoubleBuffer source, CLBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueWriteBuffer(id, target.getId(), blocking, offset, source,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long write(FloatBuffer source, CLBuffer target, boolean blocking, long offset,
                      CLEventsBuffer events) {
        check(clEnqueueWriteBuffer(id, target.getId(), blocking, offset, source,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long write(ByteBuffer source, CLBuffer target, boolean blocking, long offset,
                      CLEventsBuffer events) {
        check(clEnqueueWriteBuffer(id, target.getId(), blocking, offset, source,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public void waitForEvents(CLEventsBuffer params) {
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
        clReleaseCommandQueue(id);
    }

    public static Builder builder(CLContext context, CLDevice device) {
        return new Builder(context, device);
    }

    public static class Builder {

        private final CLContext context;
        private final CLDevice device;
        private CommandQueueProperty[] properties = {};

        private Builder(CLContext context, CLDevice device) {
            this.context = context;
            this.device = device;
        }

        public Builder withProperties(CommandQueueProperty... properties) {
            this.properties = properties;
            return this;
        }

        public CLCommandQueue build() {
            return new CLCommandQueue(this);
        }
    }

}
