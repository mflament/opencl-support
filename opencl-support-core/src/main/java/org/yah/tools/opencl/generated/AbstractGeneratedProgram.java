package org.yah.tools.opencl.generated;

import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.enums.CommandQueueProperty;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.program.CLCompilerOptions;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class AbstractGeneratedProgram implements AutoCloseable {

    protected final CLContext context;
    protected final CLCommandQueue commandQueue;
    protected final CLProgram program;

    protected AbstractGeneratedProgram(CLContext context,
                                       String resourcePath,
                                       @Nullable CLDevice device,
                                       @Nullable CLCompilerOptions compilerOptions,
                                       CommandQueueProperty... commandQueueProperties) {
        this.context = Objects.requireNonNull(context, "context is null");
        if (device == null)
            device = context.getFirstDevice();
        program = context.programBuilder()
                .withFile(resourcePath)
                .withDevice(device)
                .withCompilerOptions(compilerOptions)
                .build();
        commandQueue = CLCommandQueue.builder(context)
                .withDevice(device)
                .withProperties(commandQueueProperties)
                .build();
    }

    public CLContext getContext() {
        return context;
    }

    public CLCommandQueue getCommandQueue() {
        return commandQueue;
    }

    public CLProgram getProgram() {
        return program;
    }

    @Override
    public void close() {
        program.close();
        commandQueue.close();
    }
}
