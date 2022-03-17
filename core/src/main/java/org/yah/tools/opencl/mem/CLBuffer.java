package org.yah.tools.opencl.mem;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.enums.BufferProperty;

import java.nio.*;
import java.util.Collection;

import static org.lwjgl.opencl.CL10.clCreateBuffer;
import static org.lwjgl.opencl.CL10.clReleaseMemObject;

/**
 * @author Yah
 */
public class CLBuffer implements CLMemObject {

    private final long id;

    private final PointerBuffer pointerBuffer;

    private CLBuffer(long id) {
        this.id = id;
        this.pointerBuffer = BufferUtils.createPointerBuffer(1);
        this.pointerBuffer.put(0, id);
    }

    public CLSubBuffer createSubBuffer(long offset, long size, BufferProperty... properties) {
        return new CLSubBuffer(this, offset, size, properties);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public PointerBuffer getPointer() {
        return pointerBuffer;
    }

    @Override
    public void close() {
        clReleaseMemObject(id);
    }

    public static final class Builder {
        private final CLContext context;
        private long properties;

        public Builder(CLContext context) {
            this.context = context;
        }

        public Builder withProperties(BufferProperty... properties) {
            this.properties = BufferProperty.combine(properties);
            return this;
        }

        public Builder withProperties(Collection<BufferProperty> properties) {
            this.properties = BufferProperty.combine(properties);
            return this;
        }

        public CLBuffer build(long size) {
            long id = CLException.apply(eb -> clCreateBuffer(context.getId(), properties, size, eb));
            return new CLBuffer(id);
        }

        public CLBuffer build(ByteBuffer hostBuffer) {
            long id = CLException.apply(eb -> clCreateBuffer(context.getId(), properties, hostBuffer, eb));
            return new CLBuffer(id);
        }

        public CLBuffer build(ShortBuffer hostBuffer) {
            long id = CLException.apply(eb -> clCreateBuffer(context.getId(), properties, hostBuffer, eb));
            return new CLBuffer(id);
        }

        public CLBuffer build(IntBuffer hostBuffer) {
            long id = CLException.apply(eb -> clCreateBuffer(context.getId(), properties, hostBuffer, eb));
            return new CLBuffer(id);
        }

        public CLBuffer build(FloatBuffer hostBuffer) {
            long id = CLException.apply(eb -> clCreateBuffer(context.getId(), properties, hostBuffer, eb));
            return new CLBuffer(id);
        }

        public CLBuffer build(DoubleBuffer hostBuffer) {
            long id = CLException.apply(eb -> clCreateBuffer(context.getId(), properties, hostBuffer, eb));
            return new CLBuffer(id);
        }
    }
}
