package org.yah.tools.opencl.cmdqueue;

import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.enums.CommandQueueProperty;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.platform.CLDevice;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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

    public void read(CLBuffer buffer, ByteBuffer target) {
        read(buffer, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public void read(CLBuffer buffer, IntBuffer target) {
        read(buffer, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public void read(CLBuffer buffer, FloatBuffer target) {
        read(buffer, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public void read(CLBuffer buffer, DoubleBuffer target) {
        read(buffer, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public long read(CLBuffer buffer, ByteBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer buffer, IntBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer buffer, FloatBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long read(CLBuffer buffer, DoubleBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueReadBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public void write(CLBuffer buffer, ByteBuffer target) {
        write(buffer, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public void write(CLBuffer buffer, IntBuffer target) {
        write(buffer, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public void write(CLBuffer buffer, DoubleBuffer target) {
        write(buffer, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public void write(CLBuffer buffer, FloatBuffer target) {
        write(buffer, target, true, 0, CLEventsBuffer.EMPTY_PARAM);
    }

    public long write(CLBuffer buffer, IntBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueWriteBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long write(CLBuffer buffer, DoubleBuffer target, boolean blocking, long offset, CLEventsBuffer events) {
        check(clEnqueueWriteBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long write(CLBuffer buffer, FloatBuffer target, boolean blocking, long offset,
                      CLEventsBuffer events) {
        check(clEnqueueWriteBuffer(id, buffer.getId(), blocking, offset, target,
                events.getEventWaitListBuffer(), events.getEventBuffer()));
        return events.flushEvent();
    }

    public long write(CLBuffer buffer, ByteBuffer target, boolean blocking, long offset,
                      CLEventsBuffer events) {
        check(clEnqueueWriteBuffer(id, buffer.getId(), blocking, offset, target,
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
