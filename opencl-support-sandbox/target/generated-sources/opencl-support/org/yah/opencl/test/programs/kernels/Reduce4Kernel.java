package org.yah.opencl.test.programs.kernels;

import org.yah.opencl.test.reduce.ReduceKernel;
import java.nio.Buffer;
import org.yah.tools.opencl.enums.BufferProperty;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.ndrange.NDRange;

/**
 * void reduce4(global const T* g_idata,
 *              global T* g_odata,
 *              private uint n,
 *              local volatile T* sdata)
 */
public interface Reduce4Kernel<T extends Buffer> extends ReduceKernel<T>, AutoCloseable {

    /**
     * global const T* g_idata
     */
    Reduce4Kernel<T> createGIdata(long size, BufferProperty... bufferProperties);

    /**
     * global const T* g_idata
     */
    Reduce4Kernel<T> createGIdata(T buffer, BufferProperty... bufferProperties);

    /**
     * global const T* g_idata
     */
    Reduce4Kernel<T> updateGIdata(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global const T* g_idata
     */
    default Reduce4Kernel<T> updateGIdata(T buffer) {
        return updateGIdata(buffer, 0L, null);
    }

    /**
     * global T* g_odata
     */
    Reduce4Kernel<T> createGOdata(long size, BufferProperty... bufferProperties);

    /**
     * global T* g_odata
     */
    Reduce4Kernel<T> createGOdata(T buffer, BufferProperty... bufferProperties);

    /**
     * global T* g_odata
     */
    Reduce4Kernel<T> updateGOdata(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global T* g_odata
     */
    default Reduce4Kernel<T> updateGOdata(T buffer) {
        return updateGOdata(buffer, 0L, null);
    }

    /**
     * global T* g_odata
     */
    Reduce4Kernel<T> readGOdata(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global T* g_odata
     */
    default Reduce4Kernel<T> readGOdata(T buffer) {
        return readGOdata(buffer, 0L, null);
    }

    /**
     * private uint n
     */
    Reduce4Kernel<T> setN(int value);

    /**
     * local volatile T* sdata
     */
    Reduce4Kernel<T> setSdataSize(long size);

    Reduce4Kernel<T> invoke(NDRange range, @Nullable PointerBuffer event);

    default Reduce4Kernel<T> invoke(NDRange range) {
        return invoke(range, null);
    }

    @Override
    void close();
}
