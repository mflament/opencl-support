package org.yah.opencl.test.programs.kernels;

import org.yah.opencl.test.reduce.ReduceKernel;
import java.nio.Buffer;
import org.yah.tools.opencl.enums.BufferProperty;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.ndrange.NDRange;

/**
 * void reduce2(global const T* g_idata,
 *              global T* g_odata,
 *              private uint n,
 *              local T* sdata)
 */
public interface Reduce2Kernel<T extends Buffer> extends ReduceKernel<T>, AutoCloseable {

    /**
     * global const T* g_idata
     */
    Reduce2Kernel<T> createGIdata(long size, BufferProperty... bufferProperties);

    /**
     * global const T* g_idata
     */
    Reduce2Kernel<T> createGIdata(T buffer, BufferProperty... bufferProperties);

    /**
     * global const T* g_idata
     */
    Reduce2Kernel<T> updateGIdata(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global const T* g_idata
     */
    default Reduce2Kernel<T> updateGIdata(T buffer) {
        return updateGIdata(buffer, 0L, null);
    }

    /**
     * global T* g_odata
     */
    Reduce2Kernel<T> createGOdata(long size, BufferProperty... bufferProperties);

    /**
     * global T* g_odata
     */
    Reduce2Kernel<T> createGOdata(T buffer, BufferProperty... bufferProperties);

    /**
     * global T* g_odata
     */
    Reduce2Kernel<T> updateGOdata(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global T* g_odata
     */
    default Reduce2Kernel<T> updateGOdata(T buffer) {
        return updateGOdata(buffer, 0L, null);
    }

    /**
     * global T* g_odata
     */
    Reduce2Kernel<T> readGOdata(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global T* g_odata
     */
    default Reduce2Kernel<T> readGOdata(T buffer) {
        return readGOdata(buffer, 0L, null);
    }

    /**
     * private uint n
     */
    Reduce2Kernel<T> setN(int value);

    /**
     * local T* sdata
     */
    Reduce2Kernel<T> setSdataSize(long size);

    Reduce2Kernel<T> invoke(NDRange range, @Nullable PointerBuffer event);

    default Reduce2Kernel<T> invoke(NDRange range) {
        return invoke(range, null);
    }

    @Override
    void close();
}
