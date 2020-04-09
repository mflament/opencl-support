package org.yah.tools.opencl;

import org.lwjgl.opencl.CLCapabilities;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue.EventsParams;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue.KernelNDRange;
import org.yah.tools.opencl.cmdqueue.CommandQueueProperties;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.BufferProperties;
import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.platform.CLPlaform;
import org.yah.tools.opencl.program.CLProgram;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.yah.tools.opencl.platform.CLPlaform.createPlatform;

/**
 * @author Yah
 */
public class CLEnvironment implements AutoCloseable {

    private final CLPlaform plaform;
    private final CLContext context;
    private final CLCommandQueue commandQueue;
    private final CLProgram program;

    private final List<AutoCloseable> resources;

    private CLEnvironment(Builder builder, List<AutoCloseable> resources) {
        this.context = builder.context;
        this.commandQueue = builder.commandQueue;
        this.program = builder.program;
        this.plaform = createPlatform(context.getPlatform());
        this.resources = resources;
    }

    @Override
    public void close() {
        for (AutoCloseable resource : resources) {
            try {
                resource.close();
            } catch (Exception ignored) {
            }
        }
    }

    public long getDevice() {
        return context.getDevice();
    }

    public long getPlaform() {
        return plaform.getId();
    }

    public CLCapabilities getCapabilities() {
        return plaform.getCapabilities();
    }

    public CLKernel kernel(String name) {
        return addResource(new CLKernel(program, name));
    }

    public CLBuffer mem(ByteBuffer hostBuffer, BufferProperties... properties) {
        return addResource(new CLBuffer(context, hostBuffer, properties));
    }

    public CLBuffer mem(IntBuffer hostBuffer, BufferProperties... properties) {
        return addResource(new CLBuffer(context, hostBuffer, properties));
    }

    public CLBuffer mem(FloatBuffer hostBuffer, BufferProperties... properties) {
        return addResource(new CLBuffer(context, hostBuffer, properties));
    }

    public CLBuffer mem(DoubleBuffer hostBuffer, BufferProperties... properties) {
        return addResource(new CLBuffer(context, hostBuffer, properties));
    }

    public CLBuffer mem(int size, BufferProperties... properties) {
        return addResource(new CLBuffer(context, size, properties));
    }

    public KernelNDRange kernelRange() {
        return commandQueue.createKernelRange();
    }

    public void run(CLKernel kernel, long[] globalWorkSizes) {
        commandQueue.run(kernel, globalWorkSizes);
    }

    public long run(CLKernel kernel, KernelNDRange range) {
        return commandQueue.run(kernel, range);
    }

    public long read(CLBuffer buffer, ByteBuffer target, boolean blocking, long offset, EventsParams events) {
        return commandQueue.read(buffer, target, blocking, offset, events);
    }

    public void read(CLBuffer buffer, FloatBuffer target) {
        commandQueue.read(buffer, target);
    }

    public void read(CLBuffer buffer, DoubleBuffer target) {
        commandQueue.read(buffer, target);
    }

    public long read(CLBuffer buffer, IntBuffer target, boolean blocking, long offset, EventsParams events) {
        return commandQueue.read(buffer, target, blocking, offset, events);
    }

    public long read(CLBuffer buffer, FloatBuffer target, boolean blocking, long offset, EventsParams events) {
        return commandQueue.read(buffer, target, blocking, offset, events);
    }

    public long read(CLBuffer buffer, DoubleBuffer target, boolean blocking, long offset, EventsParams events) {
        return commandQueue.read(buffer, target, blocking, offset, events);
    }

    public void read(CLBuffer buffer, ByteBuffer target) {
        commandQueue.read(buffer, target);
    }

    public void read(CLBuffer buffer, IntBuffer target) {
        commandQueue.read(buffer, target);
    }

    public void write(CLBuffer buffer, ByteBuffer target) {
        commandQueue.write(buffer, target);
    }

    public void write(CLBuffer buffer, ByteBuffer target, boolean blocking, long offset, EventsParams events) {
        commandQueue.write(buffer, target, blocking, offset, events);
    }

    public long getId() {
        return commandQueue.getId();
    }

    public KernelNDRange createKernelRange() {
        return commandQueue.createKernelRange();
    }

    public void write(CLBuffer buffer, IntBuffer target) {
        commandQueue.write(buffer, target);
    }

    public void write(CLBuffer buffer, DoubleBuffer target) {
        commandQueue.write(buffer, target);
    }

    public void write(CLBuffer buffer, FloatBuffer target) {
        commandQueue.write(buffer, target);
    }

    public long write(CLBuffer buffer, IntBuffer target, boolean blocking, long offset, EventsParams events) {
        return commandQueue.write(buffer, target, blocking, offset, events);
    }

    public long write(CLBuffer buffer, DoubleBuffer target, boolean blocking, long offset, EventsParams events) {
        return commandQueue.write(buffer, target, blocking, offset, events);
    }

    public long write(CLBuffer buffer, FloatBuffer target, boolean blocking, long offset, EventsParams events) {
        return commandQueue.write(buffer, target, blocking, offset, events);
    }

    public void finish() {
        commandQueue.finish();
    }

    public void flush() {
        commandQueue.flush();
    }

    public void waitForEvents(EventsParams params) {
        commandQueue.waitForEvents(params);
    }

    public void waitForEvent(long event) {
        commandQueue.waitForEvent(event);
    }

    private <T extends AutoCloseable> T addResource(T resource) {
        resources.add(resource);
        return resource;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static void onError(String message, ByteBuffer data) {
        System.err.println("OpenCL error: " + message);
    }

    public static final class Builder {
        private CLContext context;
        private CLCommandQueue commandQueue;
        private CLProgram program;

        private final CLProgram.Builder programBuilder = CLProgram.builder();

        private CommandQueueProperties[] queueProperties = {};

        private Builder() {
        }

        public Builder withContext(CLContext context) {
            this.context = context;
            return this;
        }

        public Builder withCommandQueue(CLCommandQueue commandQueue) {
            this.commandQueue = commandQueue;
            return this;
        }

        public Builder withProgram(CLProgram program) {
            this.program = program;
            return this;
        }

        public Builder withCommandQueueProperties(CommandQueueProperties... properties) {
            this.queueProperties = properties;
            return this;
        }

        public Builder withOptions(String options) {
            programBuilder.withOptions(options);
            return this;
        }

        public Builder withSourceFiles(String... files) throws IOException {
            programBuilder.withSourceFiles(files);
            return this;
        }

        public Builder withSourcePaths(Path... files) throws IOException {
            programBuilder.withSourcePaths(files);
            return this;
        }

        public Builder withSourceResource(String... resources)
                throws IOException {
            programBuilder.withSourceResource(resources);
            return this;
        }

        public CLEnvironment build() {
            List<AutoCloseable> resources = new ArrayList<>();
            if (context == null) {
                context = CLContext.createDefault(CLEnvironment::onError);
                resources.add(context);
            }

            if (program == null) {
                programBuilder.withContext(context);
                program = programBuilder.build();
                resources.add(program);
            }

            if (commandQueue == null) {
                commandQueue = new CLCommandQueue(context, context.getDevice(), queueProperties);
                resources.add(commandQueue);
            }

            return new CLEnvironment(this, resources);
        }
    }

}
