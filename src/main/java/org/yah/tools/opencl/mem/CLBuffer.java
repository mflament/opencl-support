package org.yah.tools.opencl.mem;

import static org.lwjgl.opencl.CL10.clCreateBuffer;
import static org.lwjgl.opencl.CL10.clReleaseMemObject;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.enums.BufferProperties;

/**
 * @author Yah
 */
public class CLBuffer implements CLMemObject {

    private final long id;

    private CLBuffer(long id) {
        this.id = id;
    }

    public CLSubBuffer createSubBuffer(long offset, long size, BufferProperties... properties) {
        return new CLSubBuffer(this, offset, size, properties);
    }

    @Override
    public long getId() {
        return id;
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

        public Builder withProperties(BufferProperties... properties) {
            this.properties = BufferProperties.combine(properties);
            return this;
        }

        public CLBuffer build(int size) {
            var id = CLException.apply(eb -> clCreateBuffer(context.getId(), properties, size, eb));
            return new CLBuffer(id);
        }

        public CLBuffer build(ByteBuffer hostBuffer) {
            var id = CLException.apply(eb -> clCreateBuffer(context.getId(), properties, hostBuffer, eb));
            return new CLBuffer(id);
        }

        public CLBuffer build(IntBuffer hostBuffer) {
            var id = CLException.apply(eb -> clCreateBuffer(context.getId(), properties, hostBuffer, eb));
            return new CLBuffer(id);
        }

        public CLBuffer build(FloatBuffer hostBuffer) {
            var id = CLException.apply(eb -> clCreateBuffer(context.getId(), properties, hostBuffer, eb));
            return new CLBuffer(id);
        }

        public CLBuffer build(DoubleBuffer hostBuffer) {
            var id = CLException.apply(eb -> clCreateBuffer(context.getId(), properties, hostBuffer, eb));
            return new CLBuffer(id);
        }
    }
}
