package org.yah.opencl.test.programs.kernels;

import org.yah.opencl.test.reduce.ReduceKernel;
import java.nio.Buffer;
import org.yah.tools.opencl.enums.BufferProperty;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.ndrange.NDRange;

/**
 * void reduce3(global const T* g_idata,
 *              global T* g_odata,
 *              private uint n,
 *              local T* sdata)
 */
public interface Reduce3Kernel<T extends Buffer> extends ReduceKernel<T>, AutoCloseable {

    /**
     * global const T* g_idata
     */
    Reduce3Kernel<T> createGIdata(long size, BufferProperty... bufferProperties);

    /**
     * global const T* g_idata
     */
    Reduce3Kernel<T> createGIdata(T buffer, BufferProperty... bufferProperties);

    /**
     * global const T* g_idata
     */
    Reduce3Kernel<T> updateGIdata(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global const T* g_idata
     */
    default Reduce3Kernel<T> updateGIdata(T buffer) {
        return updateGIdata(buffer, 0L, null);
    }

    /**
     * global T* g_odata
     */
    Reduce3Kernel<T> createGOdata(long size, BufferProperty... bufferProperties);

    /**
     * global T* g_odata
     */
    Reduce3Kernel<T> createGOdata(T buffer, BufferProperty... bufferProperties);

    /**
     * global T* g_odata
     */
    Reduce3Kernel<T> updateGOdata(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global T* g_odata
     */
    default Reduce3Kernel<T> updateGOdata(T buffer) {
        return updateGOdata(buffer, 0L, null);
    }

    /**
     * global T* g_odata
     */
    Reduce3Kernel<T> readGOdata(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global T* g_odata
     */
    default Reduce3Kernel<T> readGOdata(T buffer) {
        return readGOdata(buffer, 0L, null);
    }

    /**
     * private uint n
     */
    Reduce3Kernel<T> setN(int value);

    /**
     * local T* sdata
     */
    Reduce3Kernel<T> setSdataSize(long size);

    Reduce3Kernel<T> invoke(NDRange range, @Nullable PointerBuffer event);

    default Reduce3Kernel<T> invoke(NDRange range) {
        return invoke(range, null);
    }

    @Override
    void close();
}
