/**
 * 
 */
package org.yah.tools.opencl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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

/**
 * @author Yah
 *
 */
public class CLEnvironment implements AutoCloseable {

    private final CLPlaform plaform;
    private final CLContext context;
    private final CLCommandQueue commandQueue;
    private final CLProgram program;
    private final boolean ownContext;

    public CLEnvironment(String sourceResource, CommandQueueProperties... queueProperties) throws IOException {
        this(sourceResource, null, queueProperties);
    }

    public CLEnvironment(String sourceResource,
            String options,
            CommandQueueProperties... queueProperties) throws IOException {
        this(null, sourceResource, options, queueProperties);
    }

    public CLEnvironment(CLContext context,
            String sourceResource,
            String options,
            CommandQueueProperties... queueProperties) throws IOException {
        if (context == null) {
            context = CLContext.createDefault(this::onError);
            ownContext = true;
        } else {
            ownContext = false;
        }
        this.context = context;
        this.plaform = CLPlaform.createPlatform(context.getPlatform());

        commandQueue = new CLCommandQueue(context, context.getDevice(), queueProperties);
        program = CLProgram.fromResource(context, options, sourceResource);
    }

    public CLCapabilities getCapabilities() { return plaform.getCapabilities(); }

    @Override
    public void close() {
        if (ownContext)
            closeQuietly(context);
    }

    private void onError(String message, ByteBuffer data) {
        System.err.println("OpenCL error: " + message);
    }

    public CLKernel kernel(String name) {
        return new CLKernel(program, name);
    }

    public CLBuffer mem(ByteBuffer hostBuffer, BufferProperties... properties) {
        return new CLBuffer(context, hostBuffer, properties);
    }

    public CLBuffer mem(FloatBuffer hostBuffer, BufferProperties... properties) {
        return new CLBuffer(context, hostBuffer, properties);
    }

    public CLBuffer mem(int size, BufferProperties... properties) {
        return new CLBuffer(context, size, properties);
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

    public void finish() {
        commandQueue.finish();
    }

    private static void closeQuietly(CLObject o) {
        try {
            o.close();
        } catch (Exception e) {}
    }

    public void flush() {
        commandQueue.flush();
    }

    public long getDevice() { return context.getDevice(); }

}
