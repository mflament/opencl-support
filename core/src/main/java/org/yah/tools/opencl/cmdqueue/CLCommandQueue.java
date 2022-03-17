package org.yah.tools.opencl.cmdqueue;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.enums.CommandQueueProperty;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.ndrange.NDRange;
import org.yah.tools.opencl.platform.CLDevice;

import javax.annotation.Nullable;
import java.nio.*;

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

    private final PointerBuffer runEventBuffer = BufferUtils.createPointerBuffer(1);

    private CLCommandQueue(CLContext context, CLDevice device, long id) {
        this.context = context;
        this.device = device;
        this.id = id;
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

    public void run(CLKernel kernel, NDRange range) {
        run(kernel, range, null, null);
    }

    public void run(CLKernel kernel, NDRange range, @Nullable PointerBuffer eventsWaitList, @Nullable PointerBuffer event) {
        PointerBuffer eventBuffer = event;
        if (eventBuffer == null)
            eventBuffer = runEventBuffer;

        check(clEnqueueNDRangeKernel(id, kernel.getId(),
                range.getDimensions(),
                range.getGlobalWorkOffsets(),
                range.getGlobalWorkSizes(),
                range.getLocalWorkSizes(),
                eventsWaitList,
                eventBuffer));

        if (event == null)
            waitForEvents(eventBuffer);
    }

    public void read(CLBuffer source, ByteBuffer target) {
        read(source, target, 0, null, null);
    }

    public void read(CLBuffer source, ByteBuffer target, long offset, @Nullable PointerBuffer waitForEvents, @Nullable PointerBuffer event) {
        check(clEnqueueReadBuffer(id, source.getId(), event == null, offset, target, waitForEvents, event));
    }

    public void read(CLBuffer source, ShortBuffer target) {
        read(source, target, 0, null, null);
    }

    public void read(CLBuffer source, ShortBuffer target, long offset, @Nullable PointerBuffer waitForEvents, @Nullable PointerBuffer event) {
        check(clEnqueueReadBuffer(id, source.getId(), event == null, offset, target, waitForEvents, event));
    }

    public void read(CLBuffer source, IntBuffer target) {
        read(source, target, 0, null, null);
    }

    public void read(CLBuffer source, IntBuffer target, long offset, @Nullable PointerBuffer waitForEvents, @Nullable PointerBuffer event) {
        check(clEnqueueReadBuffer(id, source.getId(), event == null, offset, target, waitForEvents, event));
    }

    public void read(CLBuffer source, FloatBuffer target) {
        read(source, target, 0, null, null);
    }

    public void read(CLBuffer source, FloatBuffer target, long offset, @Nullable PointerBuffer waitForEvents, @Nullable PointerBuffer event) {
        check(clEnqueueReadBuffer(id, source.getId(), event == null, offset, target, waitForEvents, event));
    }

    public void read(CLBuffer source, DoubleBuffer target) {
        read(source, target, 0, null, null);
    }

    public void read(CLBuffer source, DoubleBuffer target, long offset, @Nullable PointerBuffer waitForEvents, @Nullable PointerBuffer event) {
        check(clEnqueueReadBuffer(id, source.getId(), event == null, offset, target, waitForEvents, event));
    }

    public void write(ByteBuffer source, CLBuffer target) {
        write(source, target, 0, null, null);
    }

    public void write(ByteBuffer source, CLBuffer target, long offset, @Nullable PointerBuffer waitForEvents, @Nullable PointerBuffer event) {
        check(clEnqueueWriteBuffer(id, target.getId(), event == null, offset, source, waitForEvents, event));
    }

    public void write(ShortBuffer source, CLBuffer target) {
        write(source, target, 0, null, null);
    }

    public void write(ShortBuffer source, CLBuffer target, long offset, @Nullable PointerBuffer waitForEvents, @Nullable PointerBuffer event) {
        check(clEnqueueWriteBuffer(id, target.getId(), event == null, offset, source, waitForEvents, event));
    }

    public void write(IntBuffer source, CLBuffer target) {
        write(source, target, 0, null, null);
    }

    public void write(IntBuffer source, CLBuffer target, long offset, @Nullable PointerBuffer waitForEvents, @Nullable PointerBuffer event) {
        check(clEnqueueWriteBuffer(id, target.getId(), event == null, offset, source, waitForEvents, event));
    }

    public void write(FloatBuffer source, CLBuffer target) {
        write(source, target, 0, null, null);
    }

    public void write(FloatBuffer source, CLBuffer target, long offset, @Nullable PointerBuffer waitForEvents, @Nullable PointerBuffer event) {
        check(clEnqueueWriteBuffer(id, target.getId(), event == null, offset, source, waitForEvents, event));
    }

    public void write(DoubleBuffer source, CLBuffer target) {
        write(source, target, 0, null, null);
    }

    public void write(DoubleBuffer source, CLBuffer target, long offset, @Nullable PointerBuffer waitForEvents, @Nullable PointerBuffer event) {
        check(clEnqueueWriteBuffer(id, target.getId(), event == null, offset, source, waitForEvents, event));
    }

    public void waitForEvents(PointerBuffer events) {
        if (events.hasRemaining())
            check(clWaitForEvents(events));
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

    public static Builder builder(CLContext context) {
        return new Builder(context);
    }

    public static class Builder {

        private final CLContext context;
        private CLDevice device;
        private CommandQueueProperty[] properties = {};

        private Builder(CLContext context) {
            this.context = context;
        }

        public Builder withProperties(CommandQueueProperty... properties) {
            this.properties = properties;
            return this;
        }

        public Builder withDevice(CLDevice device) {
            this.device = device;
            return this;
        }

        public CLCommandQueue build() {
            if (device == null)
                device = context.getFirstDevice();
            else
                context.checkDevice(device);

            long id = CLException.apply(eb -> clCreateCommandQueue(context.getId(), device.getId(),
                    CommandQueueProperty.all(properties), eb));
            return new CLCommandQueue(context, device, id);
        }
    }

}
