package org.yah.tools.opencl.kernel;

import static org.lwjgl.opencl.CL10.clCreateKernel;
import static org.lwjgl.opencl.CL10.clReleaseKernel;
import static org.lwjgl.opencl.CL10.clSetKernelArg;

import java.nio.ByteBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.mem.CLMemObject;
import org.yah.tools.opencl.program.CLProgram;

public class CLKernel implements CLObject {

    private long id;

    public CLKernel(CLProgram program, String name) {
        id = CLException.apply(eb -> clCreateKernel(program.getId(), name, eb));
    }

    @Override
    public long getId() { return id; }

    @Override
    public void close() {
        if (id != 0) {
            clReleaseKernel(id);
            id = 0;
        }
    }

    public void setArg(int index, ByteBuffer buffer) {
        clSetKernelArg(id, index, buffer);
    }

    public void setArg(int index, CLMemObject memObject) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pb = stack.pointers(memObject.getId());
            clSetKernelArg(id, index, pb);
        }
    }

    public void setArg(int index, short value) {
        clSetKernelArg(id, index, new short[] { value });
    }

    public void setArg(int index, int value) {
        clSetKernelArg(id, index, new int[] { value });
    }

    public void setArg(int index, long value) {
        clSetKernelArg(id, index, new long[] { value });
    }

    public void setArg(int index, float value) {
        clSetKernelArg(id, index, new float[] { value });
    }

    public void setArg(int index, double value) {
        clSetKernelArg(id, index, new double[] { value });
    }

    public void setArgSize(int index, long size) {
        clSetKernelArg(id, index, size);
    }

}
