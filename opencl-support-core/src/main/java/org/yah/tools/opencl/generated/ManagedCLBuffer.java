package org.yah.tools.opencl.generated;

import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.enums.BufferProperty;
import org.yah.tools.opencl.mem.CLBuffer;

import javax.annotation.Nullable;
import java.nio.*;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class ManagedCLBuffer implements AutoCloseable {

    private final CLCommandQueue commandQueue;
    private final Consumer<CLBuffer> bufferListener;

    private CLBuffer buffer;
    private long capacity;

    public ManagedCLBuffer(CLCommandQueue commandQueue, Consumer<CLBuffer> bufferListener) {
        this.commandQueue = Objects.requireNonNull(commandQueue, "commandQueue is null");
        this.bufferListener = Objects.requireNonNull(bufferListener, "bufferListener is null");
    }

    public long getCapacity() {
        return capacity;
    }

    public void create(long size, Set<BufferProperty> bufferProperties) {
        createBuffer(size, bufferProperties, builder -> builder.build(size));
    }

    public void create(ByteBuffer buffer, Set<BufferProperty> bufferProperties) {
        createBuffer(buffer.remaining(), bufferProperties, builder -> builder.build(buffer));
    }

    public void create(ShortBuffer buffer, Set<BufferProperty> bufferProperties) {
        createBuffer(buffer.remaining() * 2L, bufferProperties, builder -> builder.build(buffer));
    }

    public void create(IntBuffer buffer, Set<BufferProperty> bufferProperties) {
        createBuffer(buffer.remaining() * 4L, bufferProperties, builder -> builder.build(buffer));
    }

    public void create(FloatBuffer buffer, Set<BufferProperty> bufferProperties) {
        createBuffer(buffer.remaining() * 4L, bufferProperties, builder -> builder.build(buffer));
    }

    public void create(DoubleBuffer buffer, Set<BufferProperty> bufferProperties) {
        createBuffer(buffer.remaining() * 8L, bufferProperties, builder -> builder.build(buffer));
    }

    public void read(ByteBuffer target) {
        read(target, 0, null);
    }

    public void read(ByteBuffer target, long offset, @Nullable PointerBuffer event) {
        checkBuffer(offset, target.remaining());
        commandQueue.read(buffer, target, offset, null, event);
    }

    public void read(ShortBuffer target) {
        read(target, 0, null);
    }

    public void read(ShortBuffer target, long offset, @Nullable PointerBuffer event) {
        checkBuffer(offset, target.remaining() * 2L);
        commandQueue.read(buffer, target, offset, null, event);
    }

    public void read(IntBuffer target) {
        read(target, 0, null);
    }

    public void read(IntBuffer target, long offset, @Nullable PointerBuffer event) {
        checkBuffer(offset, target.remaining() * 4L);
        commandQueue.read(buffer, target, offset, null, event);
    }

    public void read(FloatBuffer target) {
        read(target, 0, null);
    }

    public void read(FloatBuffer target, long offset, @Nullable PointerBuffer event) {
        checkBuffer(offset, target.remaining());
        commandQueue.read(buffer, target, offset, null, event);
    }

    public void read(DoubleBuffer target) {
        read(target, 0, null);
    }

    public void read(DoubleBuffer target, long offset, @Nullable PointerBuffer event) {
        checkBuffer(offset, target.remaining());
        commandQueue.read(buffer, target, offset, null, event);
    }

    public void write(ByteBuffer source) {
        write(source, 0, null);
    }

    public void write(ByteBuffer source, long offset, @Nullable PointerBuffer event) {
        checkBuffer(offset, source.remaining());
        commandQueue.write(source, buffer, offset, null, event);
    }

    public void write(ShortBuffer source) {
        write(source, 0, null);
    }

    public void write(ShortBuffer source, long offset, @Nullable PointerBuffer event) {
        checkBuffer(offset, source.remaining());
        commandQueue.write(source, buffer, offset, null, event);
    }

    public void write(IntBuffer source) {
        write(source, 0, null);
    }

    public void write(IntBuffer source, long offset, @Nullable PointerBuffer event) {
        checkBuffer(offset, source.remaining());
        commandQueue.write(source, buffer, offset, null, event);
    }

    public void write(FloatBuffer source) {
        write(source, 0, null);
    }

    public void write(FloatBuffer source, long offset, @Nullable PointerBuffer event) {
        checkBuffer(offset, source.remaining());
        commandQueue.write(source, buffer, offset, null, event);
    }

    public void write(DoubleBuffer source) {
        write(source, 0, null);
    }

    public void write(DoubleBuffer source, long offset, @Nullable PointerBuffer event) {
        checkBuffer(offset, source.remaining());
        commandQueue.write(source, buffer, offset, null, event);
    }

    @Override
    public void close() {
        if (buffer != null) {
            buffer.close();
            buffer = null;
        }
    }

    private void createBuffer(long size, Set<BufferProperty> bufferProperties, Function<CLBuffer.Builder, CLBuffer> factory) {
        if (buffer != null)
            buffer.close();

        CLBuffer.Builder builder = commandQueue.getContext()
                .buildBuffer()
                .withProperties(bufferProperties);
        buffer = factory.apply(builder);
        capacity = size;
        bufferListener.accept(buffer);
    }

    private void checkBuffer(long offset, long length) {
        if (buffer == null)
            throw new IllegalStateException("Buffer not created");
        if (offset + length >= capacity)
            throw new IllegalStateException("Invalid range [" + offset + ", " + (offset + length) + "), actual [0," + capacity + ")");
    }
}
