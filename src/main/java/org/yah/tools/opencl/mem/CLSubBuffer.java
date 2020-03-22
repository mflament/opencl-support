/**
 * 
 */
package org.yah.tools.opencl.mem;

import static org.lwjgl.opencl.CL10.clReleaseMemObject;
import static org.lwjgl.opencl.CL11.CL_BUFFER_CREATE_TYPE_REGION;
import static org.lwjgl.opencl.CL11.clCreateSubBuffer;
import static org.yah.tools.opencl.CLException.apply;

import java.nio.ByteBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

/**
 * @author Yah
 *
 */
public class CLSubBuffer implements CLMemObject {

    private long id;

    public CLSubBuffer(CLBuffer buffer, long offset, long size, BufferProperties... properties) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer bufferCreateInfo = stack.malloc(2 * PointerBuffer.POINTER_SIZE);
            PointerBuffer.put(bufferCreateInfo, offset);
            PointerBuffer.put(bufferCreateInfo, size);
            bufferCreateInfo.flip();
            id = apply(
                    eb -> clCreateSubBuffer(buffer.getId(), BufferProperties.combine(properties),
                            CL_BUFFER_CREATE_TYPE_REGION,
                            bufferCreateInfo, eb));
        }
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
