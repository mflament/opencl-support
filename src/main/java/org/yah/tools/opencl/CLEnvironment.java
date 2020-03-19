/**
 * 
 */
package org.yah.tools.opencl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.lwjgl.opencl.CLCapabilities;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.cmdqueue.CLEvent;
import org.yah.tools.opencl.cmdqueue.CommandQueueProperties;
import org.yah.tools.opencl.cmdqueue.DefaultCLCommandQueue;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.context.DefaultCLContext;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.kernel.DefaultCLKernel;
import org.yah.tools.opencl.mem.BufferProperties;
import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.mem.DefaultCLBuffer;
import org.yah.tools.opencl.platform.CLPlaform;
import org.yah.tools.opencl.program.CLProgram;
import org.yah.tools.opencl.program.DefaultCLProgram;

/**
 * @author Yah
 *
 */
public class CLEnvironment implements AutoCloseable {

    private final CLPlaform plaform;
    private final CLContext context;
    private final CLCommandQueue commandQueue;

    private final ClassLoader classLoader = CLEnvironment.class.getClassLoader();

    private final List<CLObject> objects = new LinkedList<CLObject>();

    public CLEnvironment() {
        this.context = DefaultCLContext.createDefault(this::onError);
        this.plaform = CLPlaform.createPlatform(context.getPlatform());
        commandQueue = new DefaultCLCommandQueue(context, context.getDevice(),
                CommandQueueProperties.setOf(CommandQueueProperties.QUEUE_PROFILING_ENABLE));
    }

    public CLEnvironment(DefaultCLContext context, CLCommandQueue commandQueue) {
        this.context = context;
        this.plaform = CLPlaform.createPlatform(context.getPlatform());
        this.commandQueue = commandQueue;
    }

    public CLCapabilities getCapabilities() { return plaform.getCapabilities(); }

    @Override
    public void close() {
        objects.forEach(CLEnvironment::closeQuietly);
        objects.clear();
        closeQuietly(context);
    }

    private void onError(String message, ByteBuffer data) {
        System.err.println("OpenCL error: " + message);
    }

    public CLKernel build(String name, String sourceResource) throws IOException {
        return build(name, sourceResource, null);
    }

    public CLKernel build(String name, String sourceResource, String options) throws IOException {
        CLProgram program = addObject(CLProgram.class,
                DefaultCLProgram.fromResource(context, options, sourceResource));
        return addObject(CLKernel.class, new DefaultCLKernel(program, name));
    }

    public CLBuffer mem(ByteBuffer hostBuffer, Set<BufferProperties> properties) {
        DefaultCLBuffer buffer = new DefaultCLBuffer(context, properties, hostBuffer);
        return addObject(CLBuffer.class, buffer);
    }

    public CLBuffer mem(int size, Set<BufferProperties> properties) {
        return addObject(CLBuffer.class, new DefaultCLBuffer(context, properties, size));
    }

    public void run(CLKernel kernel, long[] globalWorkSizes) {
        commandQueue.run(kernel, globalWorkSizes);
    }

    public void run(CLKernel kernel, long[] globalWorkOffsets, long[] globalWorkSizes,
            long[] localWorkSizes,
            List<CLEvent> eventWaitList, CLEvent event) {
        commandQueue.run(kernel, globalWorkOffsets, globalWorkSizes, localWorkSizes, eventWaitList,
                event);
    }

    public void read(CLBuffer buffer, ByteBuffer target, boolean blocking, long offset,
            List<CLEvent> eventWaitList, CLEvent event) {
        commandQueue.read(buffer, target, blocking, offset, eventWaitList, event);
    }

    public void read(CLBuffer buffer, ByteBuffer target) {
        commandQueue.read(buffer, target);
    }

    public void write(CLBuffer buffer, ByteBuffer target) {
        commandQueue.write(buffer, target);
    }

    public void write(DefaultCLBuffer buffer, ByteBuffer target, boolean blocking, long offset,
            List<CLEvent> eventWaitList, CLEvent event) {
        commandQueue.write(buffer, target, blocking, offset, eventWaitList, event);
    }

    @SuppressWarnings("unchecked")
    private <T extends CLObject> T addObject(Class<T> type, T instance) {
        objects.add(instance);
        return (T) Proxy.newProxyInstance(classLoader, new Class[] { type },
                (p, m, args) -> invokeObject(instance, m, args));
    }

    private Object invokeObject(CLObject object, Method method, Object[] args) throws Exception {
        if (method.getName().equals("close") && method.getParameterCount() == 0) {
            objects.remove(object);
        }
        return method.invoke(object, args);
    }

    private static void closeQuietly(CLObject o) {
        try {
            o.close();
        } catch (Exception e) {}
    }

    public void finish() {
        commandQueue.finish();
    }
}
