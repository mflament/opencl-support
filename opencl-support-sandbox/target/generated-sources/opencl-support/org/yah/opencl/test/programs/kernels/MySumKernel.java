package org.yah.opencl.test.programs.kernels;

import java.nio.Buffer;
import org.yah.tools.opencl.enums.BufferProperty;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.ndrange.NDRange;

/**
 * void mySum(global const T* a,
 *            global const T* b,
 *            global T* results,
 *            private int length)
 */
public interface MySumKernel<T extends Buffer> extends AutoCloseable {

    /**
     * global const T* a
     */
    MySumKernel<T> createA(long size, BufferProperty... bufferProperties);

    /**
     * global const T* a
     */
    MySumKernel<T> createA(T buffer, BufferProperty... bufferProperties);

    /**
     * global const T* a
     */
    MySumKernel<T> updateA(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global const T* a
     */
    default MySumKernel<T> updateA(T buffer) {
        return updateA(buffer, 0L, null);
    }

    /**
     * global const T* b
     */
    MySumKernel<T> createB(long size, BufferProperty... bufferProperties);

    /**
     * global const T* b
     */
    MySumKernel<T> createB(T buffer, BufferProperty... bufferProperties);

    /**
     * global const T* b
     */
    MySumKernel<T> updateB(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global const T* b
     */
    default MySumKernel<T> updateB(T buffer) {
        return updateB(buffer, 0L, null);
    }

    /**
     * global T* results
     */
    MySumKernel<T> createResults(long size, BufferProperty... bufferProperties);

    /**
     * global T* results
     */
    MySumKernel<T> createResults(T buffer, BufferProperty... bufferProperties);

    /**
     * global T* results
     */
    MySumKernel<T> updateResults(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global T* results
     */
    default MySumKernel<T> updateResults(T buffer) {
        return updateResults(buffer, 0L, null);
    }

    /**
     * global T* results
     */
    MySumKernel<T> readResults(T buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global T* results
     */
    default MySumKernel<T> readResults(T buffer) {
        return readResults(buffer, 0L, null);
    }

    /**
     * private int length
     */
    MySumKernel<T> setLength(int value);

    MySumKernel<T> invoke(NDRange range, @Nullable PointerBuffer event);

    default MySumKernel<T> invoke(NDRange range) {
        return invoke(range, null);
    }

    @Override
    void close();
}
