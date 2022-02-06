package org.yah.tools.opencl.kernel;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.enums.kernewglinfo.KernelWorkGroupInfo;
import org.yah.tools.opencl.mem.CLMemObject;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.program.CLProgram;

import java.nio.*;
import java.util.Objects;
import java.util.function.Consumer;

import static org.lwjgl.opencl.CL10.*;
import static org.yah.tools.opencl.CLException.check;

public class CLKernel implements CLObject {

    private final long id;
    private final String name;
    private final int maxDimensions;

    private final ByteBuffer argBuffer = BufferUtils.createByteBuffer(32);
    private final ByteBuffer infoBuffer = BufferUtils.createByteBuffer(256);

    public CLKernel(CLProgram program, String name) {
        this.id = CLException.apply(eb -> clCreateKernel(program.getId(), name, eb));
        this.name = Objects.requireNonNull(name, "name is null");
        this.maxDimensions = program.getMaxDimensions();
    }

    @Override
    public long getId() {
        return id;
    }

    public <T> T getKernelWorkGroupInfo(CLDevice device, KernelWorkGroupInfo<T> info) {
        infoBuffer.clear();
        long deviceId = device.getId();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer sizeBuffer = stack.mallocPointer(1);
            check(clGetKernelWorkGroupInfo(this.id, deviceId, info.id(), (ByteBuffer) null, sizeBuffer));
            int size = (int) sizeBuffer.get(0);
            if (size > infoBuffer.remaining())
                throw new BufferOverflowException();
            infoBuffer.limit(size);
            check(clGetKernelWorkGroupInfo(this.id, deviceId, info.id(), infoBuffer, null));
        } catch (RuntimeException e) {
            throw new RuntimeException("Error reading device " + name + " info " + info, e);
        }
        return info.read(device, infoBuffer);
    }

    public int getMaxDimensions() {
        return maxDimensions;
    }

    @Override
    public void close() {
        clReleaseKernel(id);
    }

    public void setArg(int index, ByteBuffer buffer) {
        if (buffer == null)
            check(clSetKernelArg(id, index, 0L));
        else
            check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, IntBuffer buffer) {
        if (buffer == null)
            check(clSetKernelArg(id, index, 0L));
        else
            check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, ShortBuffer buffer) {
        if (buffer == null)
            check(clSetKernelArg(id, index, 0L));
        else
            check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, LongBuffer buffer) {
        if (buffer == null)
            check(clSetKernelArg(id, index, 0L));
        else
            check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, FloatBuffer buffer) {
        if (buffer == null)
            check(clSetKernelArg(id, index, 0L));
        else
            check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, DoubleBuffer buffer) {
        if (buffer == null)
            check(clSetKernelArg(id, index, 0L));
        else
            check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, CLMemObject memObject) {
        setArg(index, memObject == null ? 0L : memObject.getId());
    }

    public void setArg(int index, short value) {
        check(clSetKernelArg(id, index, argBuffer(b -> b.putShort(value))));
    }

    public void setArg(int index, int value) {
        check(clSetKernelArg(id, index, argBuffer(b -> b.putInt(value))));
    }

    public void setArg(int index, long value) {
        check(clSetKernelArg(id, index, argBuffer(b -> b.putLong(value))));
    }

    public void setArg(int index, float value) {
        check(clSetKernelArg(id, index, argBuffer(b -> b.putFloat(value))));
    }

    public void setArg(int index, double value) {
        check(clSetKernelArg(id, index, argBuffer(b -> b.putDouble(value))));
    }

    public void setArgSize(int index, long size) {
        check(clSetKernelArg(id, index, size));
    }

    private ByteBuffer argBuffer(Consumer<ByteBuffer> init) {
        argBuffer.position(0).limit(argBuffer.capacity());
        init.accept(argBuffer);
        argBuffer.flip();
        return argBuffer;
    }

}
