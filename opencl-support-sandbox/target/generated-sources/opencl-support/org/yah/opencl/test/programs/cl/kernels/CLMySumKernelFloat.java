package org.yah.opencl.test.programs.cl.kernels;

import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.generated.AbstractGeneratedKernel;
import org.yah.opencl.test.programs.cl.CLSumProgramFloat;
import org.yah.opencl.test.programs.kernels.MySumKernel;
import java.nio.FloatBuffer;
import org.yah.tools.opencl.enums.BufferProperty;
import org.lwjgl.PointerBuffer;
import javax.annotation.Nullable;
import org.yah.tools.opencl.ndrange.NDRange;

public class CLMySumKernelFloat extends AbstractGeneratedKernel implements MySumKernel<FloatBuffer> {

    private CLBuffer a;

    private CLBuffer b;

    private CLBuffer results;

    public CLMySumKernelFloat(CLSumProgramFloat program, String kernelName) {
        super(program, kernelName);
    }

    public MySumKernel<FloatBuffer> createA(long size, BufferProperty... bufferProperties) {
        a = closeAndCreate(0, a, bufferProperties, DEFAULT_WRITE_PROPERTIES, builder -> builder.build(size * 4L));
        return this;
    }

    public MySumKernel<FloatBuffer> createA(FloatBuffer buffer, BufferProperty... bufferProperties) {
        a = closeAndCreate(0, a, bufferProperties, DEFAULT_WRITE_PROPERTIES, builder -> builder.build(buffer));
        return this;
    }

    public MySumKernel<FloatBuffer> updateA(FloatBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().write(buffer, a, offset, null, event);
        return this;
    }

    public MySumKernel<FloatBuffer> createB(long size, BufferProperty... bufferProperties) {
        b = closeAndCreate(1, b, bufferProperties, DEFAULT_WRITE_PROPERTIES, builder -> builder.build(size * 4L));
        return this;
    }

    public MySumKernel<FloatBuffer> createB(FloatBuffer buffer, BufferProperty... bufferProperties) {
        b = closeAndCreate(1, b, bufferProperties, DEFAULT_WRITE_PROPERTIES, builder -> builder.build(buffer));
        return this;
    }

    public MySumKernel<FloatBuffer> updateB(FloatBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().write(buffer, b, offset, null, event);
        return this;
    }

    public MySumKernel<FloatBuffer> createResults(long size, BufferProperty... bufferProperties) {
        results = closeAndCreate(2, results, bufferProperties, DEFAULT_READ_PROPERTIES, builder -> builder.build(size * 4L));
        return this;
    }

    public MySumKernel<FloatBuffer> createResults(FloatBuffer buffer, BufferProperty... bufferProperties) {
        results = closeAndCreate(2, results, bufferProperties, DEFAULT_READ_PROPERTIES, builder -> builder.build(buffer));
        return this;
    }

    public MySumKernel<FloatBuffer> updateResults(FloatBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().write(buffer, results, offset, null, event);
        return this;
    }

    public MySumKernel<FloatBuffer> readResults(FloatBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().read(results, buffer, offset, null, event);
        return this;
    }

    public MySumKernel<FloatBuffer> setLength(int value) {
        kernel.setArg(3, value);
        return this;
    }

    public MySumKernel<FloatBuffer> invoke(NDRange range, @Nullable PointerBuffer event) {
        run(range, event);
        return this;
    }
}
