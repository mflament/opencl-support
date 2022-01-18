package org.yah.tools.opencl.kernel;

import org.lwjgl.BufferUtils;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.enums.deviceinfo.DeviceInfo;
import org.yah.tools.opencl.mem.CLMemObject;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.program.CLProgram;

import java.nio.*;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opencl.CL10.*;
import static org.yah.tools.opencl.CLException.check;

public class CLKernel implements CLObject {

    private final CLProgram program;
    private final long id;
    private final int maxDimensions;

    private static final ThreadLocal<ByteBuffer> argBuffer = ThreadLocal.withInitial(() -> BufferUtils.createByteBuffer(8));

    public CLKernel(CLProgram program, String name) {
        this.program = Objects.requireNonNull(program, "program is null");
        id = CLException.apply(eb -> clCreateKernel(program.getId(), name, eb));
        maxDimensions = (int) getDevices()
                .stream()
                .mapToInt(device -> device.getDeviceInfo(DeviceInfo.DEVICE_MAX_WORK_ITEM_DIMENSIONS))
                .min()
                .orElse(0);
    }

    @Override
    public long getId() {
        return id;
    }

    public CLProgram getProgram() {
        return program;
    }

    public List<CLDevice> getDevices() {
        return program.getDevices();
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
