package org.yah.tools.opencl.generated;

import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.cmdqueue.CLEventsBuffer;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.enums.BufferProperties;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.CLBuffer;

import java.nio.ByteBuffer;
import java.util.Objects;

public class KernelBufferArgument {

    private final CLKernel kernel;
    private final int argIndex;
    private final CLCommandQueue commandQueue;
    private final BufferProperties[] properties;

    private CLBuffer buffer;
    private int capacity;
    private final CLEventsBuffer eventsBuffer = new CLEventsBuffer(1);

    public KernelBufferArgument(CLKernel kernel,
                                int argIndex,
                                CLCommandQueue commandQueue,
                                BufferProperties... properties) {
        this.kernel = Objects.requireNonNull(kernel, "kernel is null");
        this.commandQueue = Objects.requireNonNull(commandQueue, "commandQueue is null");
        this.argIndex = argIndex;
        this.properties = Objects.requireNonNull(properties, "properties is null");
    }

    public void read(ByteBuffer target) {
        read(target, 0);
    }

    public void read(ByteBuffer target, long offset) {
        commandQueue.read(buffer, target, true, offset, CLEventsBuffer.EMPTY_PARAM);
    }

    public long readAsync(ByteBuffer target) {
        return readAsync(target, 0);
    }

    public long readAsync(ByteBuffer target, long offset) {
        return commandQueue.read(buffer, target, false, offset, eventsBuffer);
    }

    public void write(ByteBuffer source) {
        write(source, 0);
    }

    public void write(ByteBuffer source, long offset) {
        ensureCapacity(offset + source.remaining());
        commandQueue.write(source, buffer, true, offset, CLEventsBuffer.EMPTY_PARAM);
    }

    private void ensureCapacity(long newCapacity) {
        if (newCapacity > capacity) {
            if (buffer != null) buffer.close();
            buffer = getContext().buildBuffer().withProperties(properties)
                    .build(newCapacity);
            kernel.setArg(argIndex, buffer);
        }
    }

    private CLContext getContext() {
        return kernel.getProgram().getContext();
    }
}
