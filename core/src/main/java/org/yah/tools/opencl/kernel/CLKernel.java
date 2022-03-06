package org.yah.tools.opencl.kernel;

import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.enums.wglinfo.KernelWorkGroupInfo;
import org.yah.tools.opencl.mem.CLMemObject;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nonnull;
import java.nio.*;
import java.util.Objects;

import static org.lwjgl.opencl.CL12.*;
import static org.yah.tools.opencl.CLException.check;

public class CLKernel implements CLObject {

    private final CLProgram program;
    private final long id;
    private final String name;

    public CLKernel(CLProgram program, String name) {
        this.program = Objects.requireNonNull(program, "program is null");
        this.name = Objects.requireNonNull(name, "name is null");
        this.id = CLException.apply(eb -> clCreateKernel(program.getId(), name, eb));
    }

    public CLKernel(CLProgram program, long id) {
        this.program = Objects.requireNonNull(program, "program is null");
        this.id = id;
        this.name = getKernelName(id);
    }

    public CLProgram getProgram() {
        return program;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public void close() {
        clReleaseKernel(id);
    }

    public <T> T getKernelWorkGroupInfo(CLDevice device, KernelWorkGroupInfo<T> info) {
        long deviceId = device.getId();
        ByteBuffer buffer = CLUtils.readSizedParam((sb, bb) -> clGetKernelWorkGroupInfo(this.id, deviceId, info.id(), bb, sb));
        return info.read(device, buffer);
    }

    public int getMaxDimensions() {
        return program.getMaxDimensions();
    }

    public int getNumArgs() {
        int[] na = new int[1];
        check(clGetKernelInfo(id, CL_KERNEL_NUM_ARGS, na, null));
        return na[0];
    }

    public String getAttributes() {
        return CLUtils.readSizedString((sb, bb) -> clGetKernelInfo(id, CL_KERNEL_ATTRIBUTES, bb, sb));
    }

    public void setArg(int index, @Nonnull CLMemObject memObject) {
        check(clSetKernelArg(id, index, memObject.getPointer()));
    }

    public void setArg(int index, ByteBuffer buffer) {
        check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, IntBuffer buffer) {
        check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, ShortBuffer buffer) {
        check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, LongBuffer buffer) {
        check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, FloatBuffer buffer) {
        check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, DoubleBuffer buffer) {
        check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, int[] buffer) {
        check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, short[] buffer) {
        check(clSetKernelArg(id, index, buffer));
    }

    public void setArgSize(int index, long size) {
        check(clSetKernelArg(id, index, size));
    }

    public void setArg(int index, long[] buffer) {
        check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, float[] buffer) {
        check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, double[] buffer) {
        check(clSetKernelArg(id, index, buffer));
    }

    public void setArg(int index, byte value) {
        check(clSetKernelArg1b(id, index, value));
    }

    public void setArg(int index, short value) {
        check(clSetKernelArg1s(id, index, value));
    }

    public void setArg(int index, int value) {
        check(clSetKernelArg1i(id, index, value));
    }

    public void setArg(int index, long value) {
        check(clSetKernelArg1l(id, index, value));
    }

    public void setArg(int index, float value) {
        check(clSetKernelArg1f(id, index, value));
    }

    public void setArg(int index, double value) {
        check(clSetKernelArg1d(id, index, value));
    }

    public void setArg(int index, byte x, byte y) {
        check(clSetKernelArg2b(id, index, x, y));
    }

    public void setArg(int index, short x, short y) {
        check(clSetKernelArg2s(id, index, x, y));
    }

    public void setArg(int index, int x, int y) {
        check(clSetKernelArg2i(id, index, x, y));
    }

    public void setArg(int index, long x, long y) {
        check(clSetKernelArg2l(id, index, x, y));
    }

    public void setArg(int index, float x, float y) {
        check(clSetKernelArg2f(id, index, x, y));
    }

    public void setArg(int index, double x, double y) {
        check(clSetKernelArg2d(id, index, x, y));
    }

    public void setArg(int index, byte x, byte y, byte z, byte w) {
        check(clSetKernelArg4b(id, index, x, y, z, w));
    }

    public void setArg(int index, short x, short y, short z, short w) {
        check(clSetKernelArg4s(id, index, x, y, z, w));
    }

    public void setArg(int index, int x, int y, int z, int w) {
        check(clSetKernelArg4i(id, index, x, y, z, w));
    }

    public void setArg(int index, long x, long y, long z, long w) {
        check(clSetKernelArg4l(id, index, x, y, z, w));
    }

    public void setArg(int index, float x, float y, float z, float w) {
        check(clSetKernelArg4f(id, index, x, y, z, w));
    }

    public void setArg(int index, double x, double y, double z, double w) {
        check(clSetKernelArg4d(id, index, x, y, z, w));
    }

    private static String getKernelName(long id) {
        return CLUtils.readSizedString((sb, bb) -> clGetKernelInfo(id, CL_KERNEL_FUNCTION_NAME, bb, sb));
    }

}
