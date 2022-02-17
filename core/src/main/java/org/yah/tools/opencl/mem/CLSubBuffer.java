package org.yah.tools.opencl.mem;

import static org.lwjgl.opencl.CL10.clReleaseMemObject;
import static org.lwjgl.opencl.CL11.CL_BUFFER_CREATE_TYPE_REGION;
import static org.lwjgl.opencl.CL11.clCreateSubBuffer;
import static org.yah.tools.opencl.CLException.apply;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.yah.tools.opencl.enums.BufferProperties;

/**
 * @author Yah
 */
public class CLSubBuffer implements CLMemObject {

    private final long id;

    private final PointerBuffer pointerBuffer;

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
            pointerBuffer = BufferUtils.createPointerBuffer(1);
            pointerBuffer.put(0, id);
        }
    }

    @Override
    public PointerBuffer getPointer() {
        return pointerBuffer;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void close() {
        clReleaseMemObject(id);
    }

}
