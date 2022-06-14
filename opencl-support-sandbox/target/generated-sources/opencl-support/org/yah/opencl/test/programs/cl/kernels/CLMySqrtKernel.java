package org.yah.opencl.test.programs.cl.kernels;

import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.generated.AbstractGeneratedKernel;
import org.yah.opencl.test.programs.cl.CLSqrtProgram;
import org.yah.opencl.test.programs.kernels.MySqrtKernel;
import org.yah.tools.opencl.enums.BufferProperty;
import java.nio.DoubleBuffer;
import org.lwjgl.PointerBuffer;
import javax.annotation.Nullable;
import org.yah.tools.opencl.ndrange.NDRange;

public class CLMySqrtKernel extends AbstractGeneratedKernel implements MySqrtKernel {

    private CLBuffer a;

    private CLBuffer results;

    public CLMySqrtKernel(CLSqrtProgram program, String kernelName) {
        super(program, kernelName);
    }

    public MySqrtKernel createA(long size, BufferProperty... bufferProperties) {
        a = closeAndCreate(0, a, bufferProperties, DEFAULT_READ_PROPERTIES, builder -> builder.build(size * 8L));
        return this;
    }

    public MySqrtKernel createA(DoubleBuffer buffer, BufferProperty... bufferProperties) {
        a = closeAndCreate(0, a, bufferProperties, DEFAULT_READ_PROPERTIES, builder -> builder.build(buffer));
        return this;
    }

    public MySqrtKernel updateA(DoubleBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().write(buffer, a, offset, null, event);
        return this;
    }

    public MySqrtKernel readA(DoubleBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().read(a, buffer, offset, null, event);
        return this;
    }

    public MySqrtKernel createResults(long size, BufferProperty... bufferProperties) {
        results = closeAndCreate(1, results, bufferProperties, DEFAULT_READ_PROPERTIES, builder -> builder.build(size * 8L));
        return this;
    }

    public MySqrtKernel createResults(DoubleBuffer buffer, BufferProperty... bufferProperties) {
        results = closeAndCreate(1, results, bufferProperties, DEFAULT_READ_PROPERTIES, builder -> builder.build(buffer));
        return this;
    }

    public MySqrtKernel updateResults(DoubleBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().write(buffer, results, offset, null, event);
        return this;
    }

    public MySqrtKernel readResults(DoubleBuffer buffer, long offset, @Nullable PointerBuffer event) {
        getCommandQueue().read(results, buffer, offset, null, event);
        return this;
    }

    public MySqrtKernel setLength(int value) {
        kernel.setArg(2, value);
        return this;
    }

    public MySqrtKernel invoke(NDRange range, @Nullable PointerBuffer event) {
        run(range, event);
        return this;
    }
}
