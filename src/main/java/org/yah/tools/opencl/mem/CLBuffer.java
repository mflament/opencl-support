/**
 * 
 */
package org.yah.tools.opencl.mem;

import static org.lwjgl.opencl.CL10.clCreateBuffer;
import static org.lwjgl.opencl.CL10.clReleaseMemObject;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.context.CLContext;

/**
 * @author Yah
 *
 */
public class CLBuffer implements CLMemObject {

    private long id;

    public CLBuffer(CLContext context, int size, BufferProperties... properties) {
        id = CLException.apply(eb -> clCreateBuffer(context.getId(),
                BufferProperties.combine(properties), size, eb));
    }

    public CLBuffer(CLContext context, ByteBuffer hostBuffer,
            BufferProperties... properties) {
        id = CLException.apply(eb -> clCreateBuffer(context.getId(),
                BufferProperties.combine(properties), hostBuffer, eb));
    }

    public CLBuffer(CLContext context, FloatBuffer hostBuffer,
            BufferProperties... properties) {
        id = CLException.apply(eb -> clCreateBuffer(context.getId(),
                BufferProperties.combine(properties), hostBuffer, eb));
    }
    
    public CLSubBuffer createSubBuffer(long offset, long size, BufferProperties... properties) {
        
        return new CLSubBuffer(this, offset, size, properties);
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
