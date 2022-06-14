package org.yah.opencl.test.programs.cl.kernels;

import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.generated.AbstractGeneratedKernel;
import org.yah.opencl.test.programs.cl.CLSumProgramInt;
import org.yah.opencl.test.programs.kernels.MySumKernel;
import java.nio.IntBuffer;
import org.yah.tools.opencl.enums.BufferProperty;
import org.lwjgl.PointerBuffer;
import javax.annotation.Nullable;
import org.yah.tools.opencl.ndrange.NDRange;

public class CLMySumKernelInt extends AbstractGeneratedKernel implements MySumKernel<IntBuffer> {

    private CLBuffer a;

    private CLBuffer b;

    private CLBuffer results;

    public CLMySumKernelInt(CLSumProgramInt program, String kernelName) {
        super(program, kernelName);
    }

    public MySumKernel<IntBuffer> createA(long size, BufferProperty... bufferProperties) {
        a = closeAndCreate(0, a, bufferProperties, DEFAULT_WRITE_PROPERTIES, builder -> builder.build(size * 4L));
        return this;
    }

    public MySumKernel<IntBuffer> createA(IntBuffer buffer, BufferProperty... bufferProperties) {
        a = closeAndCreate(0, a, bufferProperties, DEFAULT_WRITE_PROPERTIES, builder -> builder.build(buffer));
        return this;
    }

    public MySumKernel<IntBuffer> updateA(IntBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().write(buffer, a, offset, null, event);
        return this;
    }

    public MySumKernel<IntBuffer> createB(long size, BufferProperty... bufferProperties) {
        b = closeAndCreate(1, b, bufferProperties, DEFAULT_WRITE_PROPERTIES, builder -> builder.build(size * 4L));
        return this;
    }

    public MySumKernel<IntBuffer> createB(IntBuffer buffer, BufferProperty... bufferProperties) {
        b = closeAndCreate(1, b, bufferProperties, DEFAULT_WRITE_PROPERTIES, builder -> builder.build(buffer));
        return this;
    }

    public MySumKernel<IntBuffer> updateB(IntBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().write(buffer, b, offset, null, event);
        return this;
    }

    public MySumKernel<IntBuffer> createResults(long size, BufferProperty... bufferProperties) {
        results = closeAndCreate(2, results, bufferProperties, DEFAULT_READ_PROPERTIES, builder -> builder.build(size * 4L));
        return this;
    }

    public MySumKernel<IntBuffer> createResults(IntBuffer buffer, BufferProperty... bufferProperties) {
        results = closeAndCreate(2, results, bufferProperties, DEFAULT_READ_PROPERTIES, builder -> builder.build(buffer));
        return this;
    }

    public MySumKernel<IntBuffer> updateResults(IntBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().write(buffer, results, offset, null, event);
        return this;
    }

    public MySumKernel<IntBuffer> readResults(IntBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().read(results, buffer, offset, null, event);
        return this;
    }

    public MySumKernel<IntBuffer> setLength(int value) {
        kernel.setArg(3, value);
        return this;
    }

    public MySumKernel<IntBuffer> invoke(NDRange range, @Nullable PointerBuffer event) {
        run(range, event);
        return this;
    }
}
