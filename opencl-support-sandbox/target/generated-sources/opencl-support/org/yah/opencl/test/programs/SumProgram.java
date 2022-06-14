package org.yah.opencl.test.programs;

import java.nio.Buffer;
import org.yah.opencl.test.programs.kernels.MySumKernel;

public interface SumProgram<T extends Buffer> extends AutoCloseable {

    /**
     * void mySum(global const T* a,
     *            global const T* b,
     *            global T* results,
     *            private int length)
     */
    MySumKernel<T> mySum();

    @Override
    void close();
}
