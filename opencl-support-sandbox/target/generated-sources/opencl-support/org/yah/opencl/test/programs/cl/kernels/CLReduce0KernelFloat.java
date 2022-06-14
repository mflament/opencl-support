package org.yah.opencl.test.programs.cl.kernels;

import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.generated.AbstractGeneratedKernel;
import org.yah.opencl.test.programs.cl.CLOclReductionProgramFloat;
import org.yah.opencl.test.programs.kernels.Reduce0Kernel;
import java.nio.FloatBuffer;
import org.yah.tools.opencl.enums.BufferProperty;
import org.lwjgl.PointerBuffer;
import javax.annotation.Nullable;
import org.yah.tools.opencl.ndrange.NDRange;

public class CLReduce0KernelFloat extends AbstractGeneratedKernel implements Reduce0Kernel<FloatBuffer> {

    private CLBuffer gIdata;

    private CLBuffer gOdata;

    public CLReduce0KernelFloat(CLOclReductionProgramFloat program, String kernelName) {
        super(program, kernelName);
    }

    public Reduce0Kernel<FloatBuffer> createGIdata(long size, BufferProperty... bufferProperties) {
        gIdata = closeAndCreate(0, gIdata, bufferProperties, DEFAULT_WRITE_PROPERTIES, builder -> builder.build(size * 4L));
        return this;
    }

    public Reduce0Kernel<FloatBuffer> createGIdata(FloatBuffer buffer, BufferProperty... bufferProperties) {
        gIdata = closeAndCreate(0, gIdata, bufferProperties, DEFAULT_WRITE_PROPERTIES, builder -> builder.build(buffer));
        return this;
    }

    public Reduce0Kernel<FloatBuffer> updateGIdata(FloatBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().write(buffer, gIdata, offset, null, event);
        return this;
    }

    public Reduce0Kernel<FloatBuffer> createGOdata(long size, BufferProperty... bufferProperties) {
        gOdata = closeAndCreate(1, gOdata, bufferProperties, DEFAULT_READ_PROPERTIES, builder -> builder.build(size * 4L));
        return this;
    }

    public Reduce0Kernel<FloatBuffer> createGOdata(FloatBuffer buffer, BufferProperty... bufferProperties) {
        gOdata = closeAndCreate(1, gOdata, bufferProperties, DEFAULT_READ_PROPERTIES, builder -> builder.build(buffer));
        return this;
    }

    public Reduce0Kernel<FloatBuffer> updateGOdata(FloatBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().write(buffer, gOdata, offset, null, event);
        return this;
    }

    public Reduce0Kernel<FloatBuffer> readGOdata(FloatBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().read(gOdata, buffer, offset, null, event);
        return this;
    }

    public Reduce0Kernel<FloatBuffer> setN(int value) {
        kernel.setArg(2, value);
        return this;
    }

    public Reduce0Kernel<FloatBuffer> setSdataSize(long size) {
        kernel.setArg(3, size);
        return this;
    }

    public Reduce0Kernel<FloatBuffer> invoke(NDRange range, @Nullable PointerBuffer event) {
        run(range, event);
        return this;
    }
}
