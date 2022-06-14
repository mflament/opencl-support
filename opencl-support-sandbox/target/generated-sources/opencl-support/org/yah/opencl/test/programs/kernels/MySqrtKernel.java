package org.yah.opencl.test.programs.kernels;

import org.yah.tools.opencl.enums.BufferProperty;
import java.nio.DoubleBuffer;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.ndrange.NDRange;

/**
 * void mySqrt(global double* a,
 *             global double* results,
 *             private int length)
 */
public interface MySqrtKernel extends AutoCloseable {

    /**
     * global double* a
     */
    MySqrtKernel createA(long size, BufferProperty... bufferProperties);

    /**
     * global double* a
     */
    MySqrtKernel createA(DoubleBuffer buffer, BufferProperty... bufferProperties);

    /**
     * global double* a
     */
    MySqrtKernel updateA(DoubleBuffer buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global double* a
     */
    default MySqrtKernel updateA(DoubleBuffer buffer) {
        return updateA(buffer, 0L, null);
    }

    /**
     * global double* a
     */
    MySqrtKernel readA(DoubleBuffer buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global double* a
     */
    default MySqrtKernel readA(DoubleBuffer buffer) {
        return readA(buffer, 0L, null);
    }

    /**
     * global double* results
     */
    MySqrtKernel createResults(long size, BufferProperty... bufferProperties);

    /**
     * global double* results
     */
    MySqrtKernel createResults(DoubleBuffer buffer, BufferProperty... bufferProperties);

    /**
     * global double* results
     */
    MySqrtKernel updateResults(DoubleBuffer buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global double* results
     */
    default MySqrtKernel updateResults(DoubleBuffer buffer) {
        return updateResults(buffer, 0L, null);
    }

    /**
     * global double* results
     */
    MySqrtKernel readResults(DoubleBuffer buffer, long offset, @Nullable PointerBuffer event);

    /**
     * global double* results
     */
    default MySqrtKernel readResults(DoubleBuffer buffer) {
        return readResults(buffer, 0L, null);
    }

    /**
     * private int length
     */
    MySqrtKernel setLength(int value);

    MySqrtKernel invoke(NDRange range, @Nullable PointerBuffer event);

    default MySqrtKernel invoke(NDRange range) {
        return invoke(range, null);
    }

    @Override
    void close();
}
