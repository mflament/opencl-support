/**
 * 
 */
package org.yah.tools.opencl.mem;

import static org.lwjgl.opencl.CL10.clCreateBuffer;
import static org.lwjgl.opencl.CL10.clReleaseMemObject;

import java.nio.ByteBuffer;
import java.util.Set;

import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.context.CLContext;

/**
 * @author Yah
 *
 */
public class DefaultCLBuffer implements CLBuffer {

    private long id;

    public DefaultCLBuffer(CLContext context, Set<BufferProperties> properties, int size) {
        id = CLException.apply(eb -> clCreateBuffer(context.getId(),
                BufferProperties.combine(properties), size, eb));
    }

    public DefaultCLBuffer(CLContext context, Set<BufferProperties> properties, ByteBuffer hostBuffer) {
        id = CLException.apply(eb -> clCreateBuffer(context.getId(),
                BufferProperties.combine(properties), hostBuffer, eb));
    }

    @Override
    public long getId() { return id; }

    @Override
    public void close() {
        if (id != 0) {
            clReleaseMemObject(id);
            id = 0;
        }
    }

}
