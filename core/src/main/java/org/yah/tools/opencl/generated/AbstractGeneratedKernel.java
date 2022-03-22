package org.yah.tools.opencl.generated;

import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.enums.BufferProperty;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.ndrange.NDRange;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public abstract class AbstractGeneratedKernel implements AutoCloseable {

    protected static final EnumSet<BufferProperty> DEFAULT_WRITE_PROPERTIES = EnumSet.of(BufferProperty.MEM_READ_ONLY, BufferProperty.MEM_COPY_HOST_PTR, BufferProperty.MEM_HOST_NO_ACCESS);
    protected static final EnumSet<BufferProperty> DEFAULT_READ_PROPERTIES = EnumSet.of(BufferProperty.MEM_ALLOC_HOST_PTR, BufferProperty.MEM_WRITE_ONLY, BufferProperty.MEM_HOST_READ_ONLY);

    protected final AbstractGeneratedProgram generatedProgram;
    protected final CLKernel kernel;
    protected final List<Runnable> closeables = new ArrayList<>();

    protected AbstractGeneratedKernel(AbstractGeneratedProgram generatedProgram, String kernelName) {
        this.generatedProgram = Objects.requireNonNull(generatedProgram, "generatedProgram is null");
        kernel = generatedProgram.getProgram().newKernel(kernelName);
        closeables.add(kernel::close);
    }

    protected void run(NDRange range, @Nullable PointerBuffer eventBuffer) {
        getCommandQueue().run(kernel, range, null, eventBuffer);
    }

    public CLKernel getKernel() {
        return kernel;
    }

    public final CLCommandQueue getCommandQueue() {
        return generatedProgram.getCommandQueue();
    }

    @Override
    public void close() {
        closeables.forEach(Runnable::run);
        closeables.clear();
    }

    protected final CLBuffer newCLBuffer(int argIndex, Set<BufferProperty> bufferProperties,
                                         Function<CLBuffer.Builder, CLBuffer> finisher) {
        CLBuffer.Builder builder = generatedProgram.getContext().buildBuffer()
                .withProperties(bufferProperties);
        CLBuffer buffer = finisher.apply(builder);
        closeables.add(buffer::close);
        kernel.setArg(argIndex, buffer);
        return buffer;
    }

    protected final CLBuffer closeAndCreate(int argIndex, @Nullable CLBuffer current, BufferProperty[] properties,
                                            Set<BufferProperty> defaultProperties,
                                            Function<CLBuffer.Builder, CLBuffer> finisher) {
        if (current != null)
            current.close();
        Set<BufferProperty> propertySet;
        if (properties.length == 0)
            propertySet = defaultProperties;
        else
            propertySet = EnumSet.copyOf(Arrays.asList(properties));
        return newCLBuffer(argIndex, propertySet, finisher);
    }
}
