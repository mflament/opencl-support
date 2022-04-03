package org.yah.opencl.test.reduce;

import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.enums.BufferProperty;
import org.yah.tools.opencl.ndrange.NDRange;

import javax.annotation.Nullable;
import java.nio.Buffer;

public interface ReduceKernel<T extends Buffer> extends AutoCloseable {

    /**
     * global T* g_idata
     */
    ReduceKernel<T> createGIdata(T buffer, BufferProperty... bufferProperties);

    /**
     * global T* g_odata
     */
    ReduceKernel<T> createGOdata(long size, BufferProperty... bufferProperties);

    /**
     * global T* g_odata
     */
    ReduceKernel<T> readGOdata(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global T* g_odata
     */
    default ReduceKernel<T> readGOdata(T buffer) {
        return readGOdata(buffer, 0L, null);
    }

    /**
     * private uint n
     */
    ReduceKernel<T> setN(int value);

    /**
     * local T* sdata
     */
    ReduceKernel<T> setSdataSize(long size);

    ReduceKernel<T> invoke(NDRange range, @Nullable PointerBuffer event);

    default ReduceKernel<T> invoke(NDRange range) {
        return invoke(range, null);
    }

    @Override
    void close();
}
