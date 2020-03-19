package org.yah.tools.opencl.kernel;

import static org.lwjgl.opencl.CL10.clCreateKernel;
import static org.lwjgl.opencl.CL10.clReleaseKernel;
import static org.lwjgl.opencl.CL10.clSetKernelArg;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.program.CLProgram;

public class DefaultCLKernel implements CLKernel {

    private long id;

    public DefaultCLKernel(CLProgram program, String name) {
        id = CLException.apply(eb -> clCreateKernel(program.getId(), name, eb));
    }

    @Override
    public long getId() { return id; }

    @Override
    public void close() throws Exception {
        if (id != 0) {
            clReleaseKernel(id);
            id = 0;
        }
    }

    @Override
    public void setArg(int index, ByteBuffer buffer) {
        clSetKernelArg(id, index, buffer);
    }

    @Override
    public void setArg(int index, CLBuffer buffer) {
        PointerBuffer pb = BufferUtils.createPointerBuffer(1);
        pb.put(buffer.getId());
        pb.flip();
        clSetKernelArg(id, index, pb);
    }

    @Override
    public void setArg(int index, short value) {
        clSetKernelArg(id, index, new short[] { value });
    }

    @Override
    public void setArg(int index, int value) {
        clSetKernelArg(id, index, new int[] { value });
    }

    @Override
    public void setArg(int index, long value) {
        clSetKernelArg(id, index, new long[] { value });
    }
    
    @Override
    public void setArg(int index, float value) {
        clSetKernelArg(id, index, new float[] { value });
    }

    @Override
    public void setArg(int index, double value) {
        clSetKernelArg(id, index, new double[] { value });
    }
}
