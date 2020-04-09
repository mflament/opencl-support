package org.yah.tools.opencl.kernel;

import org.lwjgl.BufferUtils;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.mem.CLMemObject;
import org.yah.tools.opencl.program.CLProgram;

import java.nio.*;

import static org.lwjgl.opencl.CL10.*;
import static org.yah.tools.opencl.CLException.apply;
import static org.yah.tools.opencl.CLException.check;

public class CLKernel implements CLObject {

    private long id;

    private static final ThreadLocal<ByteBuffer> argBuffer = ThreadLocal.withInitial(() -> BufferUtils.createByteBuffer(8));

    public CLKernel(CLProgram program, String name) {
        id = apply(eb -> clCreateKernel(program.getId(), name, eb));
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void close() {
        if (id != 0) {
            clReleaseKernel(id);
            id = 0;
        }
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
        check(clSetKernelArg(id, index, argBuffer().putShort(value).flip()));
    }

    public void setArg(int index, int value) {
        check(clSetKernelArg(id, index, argBuffer().putInt(value).flip()));
    }

    public void setArg(int index, long value) {
        check(clSetKernelArg(id, index, argBuffer().putLong(value).flip()));
    }

    public void setArg(int index, float value) {
        check(clSetKernelArg(id, index, argBuffer().putFloat(value).flip()));
    }

    public void setArg(int index, double value) {
        check(clSetKernelArg(id, index, argBuffer().putDouble(value).flip()));
    }

    public void setArgSize(int index, long size) {
        check(clSetKernelArg(id, index, size));
    }

    private ByteBuffer argBuffer() {
        ByteBuffer b = argBuffer.get();
        return b.position(0).limit(b.capacity());
    }

}
